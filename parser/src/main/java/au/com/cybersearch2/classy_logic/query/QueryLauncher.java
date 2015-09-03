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
     * @param queryParams QueryParams
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
            headQuery = new ChainQueryExecuter(scope);
            if (queryParams.hasInitialSolution())
                headQuery.setSolution(queryParams.getInitialSolution());
            else
                headQuery.setSolution(new Solution());
            chainCalculator(queryParams, scope, querySpec, headQuery);
        }
        // Chained queries are optional
        if (querySpec.getQueryChainList() != null)
            for (QuerySpec chainQuerySpec: querySpec.getQueryChainList())
            {
                if (chainQuerySpec.getQueryType() == QueryType.calculator)
                    chainCalculator(queryParams, scope, chainQuerySpec, headQuery);
                else
                {
                    QueryParams chainQueryParams = new QueryParams(scope, chainQuerySpec);
                    chainQueryParams.initialize();
                    headQuery.chain(chainQueryParams.getAxiomCollection(), chainQueryParams.getTemplateList());
                }
            }
        while (headQuery.execute())
        {
            if ((solutionHandler != null) && !solutionHandler.onSolution(headQuery.getSolution()) || isCalculation)
                break;
        }
        // Reset all query templates so they can be recycled
        if (isCalculation)
            headQuery.backupToStart();
        else
            headQuery.reset();
    }
    
    protected void chainCalculator(QueryParams queryParams, Scope scope, QuerySpec chainQuerySpec, ChainQueryExecuter headQuery)
    {   // Calculator uses a single template
        Template calculatorTemplate = getCalculatorTemplate(scope, chainQuerySpec);
        Axiom calculatorAxiom = queryParams.getParameter(calculatorTemplate.getQualifiedName());
        headQuery.chainCalculator(calculatorAxiom, calculatorTemplate);
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
        String axiomKey = getCalculatorKeyName(querySpec).getAxiomKey();
        if (!axiomKey.isEmpty())
        {
            Axiom axiom = null;
            AxiomSource source = scope.findAxiomSource(axiomKey);
            if (source == null)
                // Return empty axiom as placeholder for axiom to come from solution
                axiom = new Axiom(axiomKey);
            else
                axiom = source.iterator().next();
            return axiom;  
        }
        return null;
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
}
