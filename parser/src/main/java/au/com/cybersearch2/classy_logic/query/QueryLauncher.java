/**
    Copyright (C) 2015  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.classy_logic.query;

import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * QueryLauncher
 * @author Andrew Bowley
 * 1 Aug 2015
 */
public class QueryLauncher implements DebugTarget
{
    /** Execution context for debugging */
    protected ExecutionContext context;

    /**
     * Execute query by specification
     * @param queryParams Query parameters
     */
    public void launch(QueryParams queryParams)
    {
        Scope scope = queryParams.getScope();
        QuerySpec querySpec = queryParams.getQuerySpec();
        SolutionHandler solutionHandler = queryParams.getSolutionHandler();
        ChainQueryExecuter headQuery = null;
        queryParams.initialize();
        boolean isCalculation = querySpec.getQueryType() == QueryType.calculator;
        if (!isCalculation)
            headQuery = new LogicQueryExecuter(queryParams);
        else
        {   // QueryParams need to be initialized to set up parameter axioms
            headQuery = new ChainQueryExecuter(queryParams);
            if (queryParams.hasInitialSolution())
                headQuery.setSolution(queryParams.getInitialSolution());
            else
                headQuery.setSolution(new Solution());
            chainCalculator(queryParams, querySpec, headQuery);
        }
        // Chained queries are optional
        if (querySpec.getQueryChainList() != null)
            for (QuerySpec chainQuerySpec: querySpec.getQueryChainList())
            {
                if (chainQuerySpec.getQueryType() == QueryType.calculator)
                    chainCalculator(queryParams, chainQuerySpec, headQuery);
                else
                {
                    int keynameCount = chainQuerySpec.getKeyNameList().size();
                    if (keynameCount != 1)
                        throw new IllegalArgumentException("Logic chain query with " + keynameCount + " parts not allowed");
                    QueryParams chainQueryParams = new QueryParams(scope, chainQuerySpec);
                    chainQueryParams.initialize();
                    Template template = chainQueryParams.getTemplateList().get(0);
                    String scopeName = template.getQualifiedName().getScope();
                    Scope templateScope = queryParams.getScope();
                    if (!scopeName.isEmpty()) 
                        templateScope = queryParams.getScope().findScope(scopeName);
                    ScopeNotifier scopeNotifier = getScopeNotification(headQuery, queryParams.getScope(), templateScope);
                    headQuery.chain(chainQueryParams.getAxiomCollection(), template, scopeNotifier);
                }
            }
        Solution solution = headQuery.getSolution();
        solution.setSolutionHandler(solutionHandler);
        headQuery.setExecutionContext(context);
        while (headQuery.execute())
        {
            if ((solution.evaluate() == EvaluationStatus.SHORT_CIRCUIT) || isCalculation)
                break;
        }
        // Reset all query templates so they can be recycled
        if (isCalculation)
            headQuery.backupToStart();
        else
            headQuery.reset();
    }

    /**
     * Returns key name from Calculator query specification
     * @param querySpec
     * @return KyeName object
     * @throws IllegalArgumentException if not exactly 1 key name specified
     */
    public KeyName getCalculatorKeyName(QuerySpec querySpec)
    {
        List<KeyName> keyNameList = querySpec.getKeyNameList();
        return keyNameList.get(keyNameList.size() - 1);
    }

    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        this.context = context;
    }

    /**
     * Add calculator to query chain
     * @param queryParams Query parameters
     * @param chainQuerySpec Query specification
     * @param headQuery Head of query chain
     */
    protected void chainCalculator(QueryParams queryParams, QuerySpec chainQuerySpec, ChainQueryExecuter headQuery)
    {   // Calculator uses a single template
        KeyName keyName = getCalculatorKeyName(chainQuerySpec);
        String scopeName = keyName.getTemplateName().getScope();
        Scope templateScope = queryParams.getScope();
        boolean isReplicate = false;
        if (!scopeName.isEmpty()) 
        {
            isReplicate = !scopeName.equals(templateScope.getName());
            templateScope = queryParams.getScope().findScope(scopeName);
        }
        Template calculatorTemplate = 
            templateScope.getTemplate(getCalculatorKeyName(chainQuerySpec).getTemplateName());
        if (isReplicate)
            calculatorTemplate = new Template(calculatorTemplate, keyName.getTemplateName());
        Axiom calculatorAxiom = queryParams.getParameter(calculatorTemplate.getQualifiedName());
        if (calculatorAxiom == null)
            calculatorAxiom = getCalculatorAxiom(queryParams.getScope(), chainQuerySpec);
        ScopeNotifier scopeNotifier = getScopeNotification(headQuery, queryParams.getScope(), templateScope);
        headQuery.chainCalculator(calculatorAxiom, calculatorTemplate, scopeNotifier);
    }

    /**
     * Returns Calculator axiom from supplied scope
     * @param scope Scope
     * @param querySpec QuerySpec
     * @return Axiom object
     */
    protected Axiom getCalculatorAxiom(Scope scope, QuerySpec querySpec)
    {
        QualifiedName axiomQualifiedName = getCalculatorKeyName(querySpec).getAxiomKey();
        if (!axiomQualifiedName.getName().isEmpty())
        {
            Axiom axiom = null;
            AxiomSource source = scope.findAxiomSource(axiomQualifiedName);
            if (source == null)
                // Return empty axiom as placeholder for axiom to come from solution
                axiom = new Axiom(axiomQualifiedName.getName());
            else
                axiom = source.iterator().next();
            return axiom;  
        }
        return null;
    }

    /**
     * 
     * @param templateScope
     * @param template
     * @return
     */
    static protected ScopeNotifier getScopeNotification(ChainQueryExecuter headQuery, Scope queryScope, Scope templateScope)
    {
        // Allow a global scope query to engage multiple scopes using 
        // first part of 2-part names to identify scope
        ScopeNotifier scopeNotifier = null;
        if (!templateScope.getName().equals(queryScope.getName()))
            // Create object to pre-execute scope localisation
            scopeNotifier = new ScopeNotifier(headQuery, templateScope);
        return scopeNotifier;
    }

}
