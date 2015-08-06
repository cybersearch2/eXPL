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
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.ScopeContext;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryLauncher;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * QueryEvaluator
 * Adapter to run a query as a function call.
 * @author Andrew Bowley
 * 1 Aug 2015
 */
public class QueryEvaluator  extends QueryLauncher implements CallEvaluator
{
    /** Query parameters */
    protected QueryParams queryParams;

    /**
     * Construct a QueryEvaluator object for a query specified by query parameters
     * @param queryParams  Query parameters
     */
    public QueryEvaluator(QueryParams queryParams)
    {
        this.queryParams = queryParams;
    }
    
    /**
     * getName
     * @see au.com.cybersearch2.classy_logic.interfaces.CallEvaluator#getName()
     */
    @Override
    public String getName()
    {
        return "query";
    }

    /**
     * evaluate
     * @see au.com.cybersearch2.classy_logic.interfaces.CallEvaluator#evaluate(java.util.List)
     */
    @Override
    public Object evaluate(List<Variable> argumentList)
    {
        QuerySpec querySpec = queryParams.getQuerySpec();
        final String templateName = getCalculatorKeyName(querySpec).getTemplateName();
        // Marshall arguments provided as a list of Variables into a properties container 
        if (argumentList.size() > 0)
        {
            Map<String, Object> properties = new HashMap<String, Object>();
            for (Variable argument: argumentList)
                properties.put(argument.getName(), argument.getValue());
            queryParams.putProperties(templateName, properties);
        }
        // Set SolutionHander to collect results
        final List<Axiom> resultList = new ArrayList<Axiom>();
        SolutionHandler solutionHandler = new SolutionHandler(){
            SolutionHandler parentSolutionHandler = queryParams.getSolutionHandler();
            @Override
            public boolean onSolution(Solution solution)
            {
                Axiom axiom = solution.getAxiom(templateName);
                if (axiom != null)
                {   
                    if ((parentSolutionHandler != null) &&
                        !parentSolutionHandler.onSolution(solution))
                        return false;
                    resultList.add(axiom);
                }
                return true;
            }};
        queryParams.setSolutionHandler(solutionHandler);
        // Do query using QueryLauncher utility class
        Scope scope = queryParams.getScope();
        ScopeContext scopeContext = scope.getContext(true);
        AxiomList axiomList = null;
        try
        {
            launch(queryParams);
            // Marshall Axioms in result list into AxiomList object
            axiomList = new AxiomList(templateName, templateName);
            ParserAssembler parserAssembler = scope.getGlobalParserAssembler();
            if (parserAssembler.getTemplate(templateName) == null)
                parserAssembler = scope.getParserAssembler();
            List<String> axiomTermNameList = 
                parserAssembler.setAxiomTermNameList(templateName, axiomList);
            if (axiomTermNameList != null)
                axiomList.setAxiomTermNameList(axiomTermNameList);
            for (int i = 0; i < resultList.size(); i++)
            {   // Each axiom is wrapped in an AxiomTermList to allow access from script
                AxiomTermList axiomTermList = new AxiomTermList(templateName, templateName);
                axiomTermList.setAxiom(resultList.get(i));
                if (axiomTermNameList != null)
                    axiomTermList.setAxiomTermNameList(axiomTermNameList);
                axiomList.assignItem(i, axiomTermList);
            }
            
        }
        finally
        {   // Clear call properties so query params can be recycled
            queryParams.clearProperties(templateName);
            // Scope restored to original state
            scopeContext.resetScope();
        }
        return axiomList;
    }

}
