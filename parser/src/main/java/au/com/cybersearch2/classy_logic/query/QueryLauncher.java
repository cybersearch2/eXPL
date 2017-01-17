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
import java.util.Map;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * QueryLauncher
 * @author Andrew Bowley
 * 1 Aug 2015
 */
public class QueryLauncher
{

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
            headQuery = new QueryExecuter(queryParams);
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
                    QueryParams chainQueryParams = new QueryParams(scope, chainQuerySpec);
                    chainQueryParams.initialize();
                    headQuery.chain(chainQueryParams.getAxiomCollection(), chainQueryParams.getTemplateList());
                }
            }
        Solution solution = headQuery.getSolution();
        solution.setSolutionHandler(solutionHandler);
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
        Template calculatorTemplate = getCalculatorTemplate(templateScope, chainQuerySpec);
        if (isReplicate)
            calculatorTemplate = new Template(calculatorTemplate, keyName.getTemplateName());
        Axiom calculatorAxiom = queryParams.getParameter(calculatorTemplate.getQualifiedName());
        if (calculatorAxiom == null)
            calculatorAxiom = getCalculatorAxiom(queryParams.getScope(), chainQuerySpec);
        headQuery.chainCalculator(templateScope, calculatorAxiom, calculatorTemplate);
    }

    /**
     * Returns the single template for a Calculator query referenced as the first template in the supplied specification
     * @param scope Current scope
     * @param querySpec Calculator type query specification
     * @return Template object which is initialized with properties, if any, in the query specification  
     */
    protected Template getCalculatorTemplate(Scope scope, QuerySpec querySpec)
    {   // Calculator uses a single template
        Template template = scope.getTemplate(getCalculatorKeyName(querySpec).getTemplateName());
        Map<String, Object> properties = querySpec.getProperties(template.getName()); 
        if (properties != null)
            template.addProperties(properties);
        return template;
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

}
