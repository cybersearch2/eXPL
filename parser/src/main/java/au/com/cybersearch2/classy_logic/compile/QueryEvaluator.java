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
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryLauncher;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * QueryEvaluator
 * Adapter to run a query as a function call and return result as an AxiomList.
 * @author Andrew Bowley
 * 1 Aug 2015
 */
public class QueryEvaluator  extends QueryLauncher implements CallEvaluator<AxiomList>
{
    /** Query parameters */
    protected QueryParams queryParams;
    /** Flag to indicate if call to Calculator in same scope */
    protected boolean isCallInScope;

    /**
     * Construct a QueryEvaluator object for a query specified by query parameters
     * @param queryParams  Query parameters
     */
    public QueryEvaluator(QueryParams queryParams, boolean isCallInScope)
    {
        this.queryParams = queryParams;
        this.isCallInScope = isCallInScope;
        // TODO add call in same scope
        //if (isCallInScope)
        //    throw new ExpressionException("Call in own scope not supported");
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
    public AxiomList evaluate(List<Term> argumentList)
    {
        QuerySpec querySpec = queryParams.getQuerySpec();
        final String templateName = getCalculatorKeyName(querySpec).getTemplateName();
        Scope scope = queryParams.getScope();
        ParserAssembler parserAssembler = scope.getGlobalParserAssembler();
        Template template = parserAssembler.getTemplate(templateName);
        if (template == null)
        {
            parserAssembler = scope.getParserAssembler();
            template = parserAssembler.getTemplate(templateName);
        }
        // Marshall arguments provided as a list of Variables into a properties container 
        if (argumentList.size() > 0)
        {
            Map<String, Object> properties = new HashMap<String, Object>();
            int index = 0; // Map unnamed arguments to template term names
            for (Term argument: argumentList)
            {
                String argName = argument.getName();
                if (argName.isEmpty())
                {
                    if (index == template.getTermCount())
                        throw new ExpressionException("Unnamed argument at position " + index + " out of bounds");
                    argName = template.getTermByIndex(index++).getName();
                }
                properties.put(argName, argument.getValue());
            }
            // TODO - Do not use just template name as can cause accidental unification
            // when query axiom is employed
            queryParams.putProperties(templateName, properties);
        }
        // Set SolutionHander to collect results
        final List<Axiom> resultList = new ArrayList<Axiom>();
        final AxiomList[] axiomListHolder = new AxiomList[1];
        SolutionHandler solutionHandler = new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                Axiom axiom = solution.getAxiom(templateName);
                if (axiom != null)
                    resultList.add(axiom);
                else
                {
                    AxiomList axiomList = solution.getAxiomList(templateName);
                    if (axiomList != null)
                    {
                        axiomListHolder[0] = AxiomUtils.duplicateAxiomList(axiomList);
                        return false;
                    }
                }
                return true;
            }};
        queryParams.setSolutionHandler(solutionHandler);
        // Do query using QueryLauncher utility class
        ScopeContext scopeContext = isCallInScope ? null : scope.getContext(true);
        if (isCallInScope)
            template.push();
        try
        {
            launch(queryParams);
            if (axiomListHolder[0] == null)
            {
                // Marshall Axioms in result list into AxiomList object
                AxiomList axiomList = new AxiomList(templateName, templateName);
                List<String> axiomTermNameList = 
                    parserAssembler.setAxiomTermNameList(templateName, axiomList);
                if (axiomTermNameList != null)
                    axiomList.setAxiomTermNameList(axiomTermNameList);
                AxiomUtils.marshallAxioms(axiomList, resultList);
                axiomListHolder[0] = axiomList;
            }
            
        }
        finally
        {   // Clear call properties so query params can be recycled
            queryParams.clearProperties(templateName);
            if (scopeContext != null)
               // Scope restored to original state
                scopeContext.resetScope();
            else
                template.pop();
        }
        return axiomListHolder[0];
    }

}
