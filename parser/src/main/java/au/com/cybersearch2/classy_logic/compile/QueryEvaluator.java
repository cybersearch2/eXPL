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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.ScopeContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryLauncher;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * QueryEvaluator
 * Adapter to run a query as a function call and return result as an AxiomList.
 * @author Andrew Bowley
 * 1 Aug 2015
 */
public class QueryEvaluator  extends QueryLauncher implements CallEvaluator<Void>
{
    /** Query parameters */
    protected QueryParams queryParams;
    /** Flag to indicate if call to Calculator in same scope */
    protected boolean isCallInScope;
    /** Optional inner Template to receive query results */
    protected Template innerTemplate;
    /** Caller's scope */
    protected Scope callerScope;

    /**
     * Construct a QueryEvaluator object for a query specified by query parameters
     * @param queryParams  Query parameters
     * @param isCallInScope Flag set true if call to query in same scope
     * @param innerTemplate Optional inner Template to receive query results
     */
    public QueryEvaluator(QueryParams queryParams, Scope scope, Template innerTemplate)
    {
        this.queryParams = queryParams;
        this.callerScope = scope;
        this.isCallInScope = queryParams.getScope() == scope;
        this.innerTemplate = innerTemplate;
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
    public Void evaluate(List<Term> argumentList)
    {
        QuerySpec querySpec = queryParams.getQuerySpec();
        String templateName = getCalculatorKeyName(querySpec).getTemplateName();
        Scope scope = queryParams.getScope();
        Template template = scope.findTemplate(templateName);
        final String solutionName =  template.getQualifiedName().toString();
        // Marshall arguments provided as a list of Variables into a properties container 
        if (argumentList.size() > 0)
        {
            Map<String, Object> properties = new HashMap<String, Object>();
            int index = 0; // Map unnamed arguments to template term names
            for (Term argument: argumentList)
            {
                // All parameters are Operands. Use name part of qualified name to get true name
                Operand operand = (Operand)argument;
                String argName = operand.getQualifiedName().getName();
                if (argName.isEmpty())
                {
                    if (index == template.getTermCount())
                        throw new ExpressionException("Unnamed argument at position " + index + " out of bounds");
                    argName = template.getTermByIndex(index++).getName();
                    // Strip down to name-only for unification
                    int pos = argName.lastIndexOf('.');
                    if (pos != -1)
                        argName = argName.substring(pos + 1);
                }
                properties.put(argName, argument.getValue());
            }
            // TODO - Do not use just template name as can cause accidental unification
            // when query axiom is employed
            queryParams.putProperties(templateName, properties);
        }
        // Set SolutionHander to collect results
        SolutionHandler solutionHandler = new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution)
            {
                Axiom axiom = solution.getAxiom(solutionName);
                if ((axiom != null) && (innerTemplate != null))
                {
                    // No backup, so reset before unification
                    innerTemplate.reset();
                    if (axiom.unifyTemplate(innerTemplate, solution))
                    {
                        if (innerTemplate.evaluate() == EvaluationStatus.COMPLETE);
                        {
                            QualifiedName innerTemplateName = innerTemplate.getQualifiedName();
                            Term innerTerm = callerScope.getParserAssembler().getOperandMap().get(innerTemplateName);
                            AxiomTermList axiomTermList = (AxiomTermList)(innerTerm.getValue());
                            Axiom innerAxiom = new Axiom(innerTemplate.getKey());
                            for (int i = 0; i < (innerTemplate.getTermCount()); i++)
                            {
                                String termName = innerTemplate.getTermByIndex(i).getName();
                                // Strip down to name-only to match axiom
                                int pos = termName.lastIndexOf('.');
                                if (pos != -1)
                                    termName = termName.substring(pos + 1);
                                Term term = axiom.getTermByName(termName);
                                if (term == null)
                                    term = new Parameter(termName);
                                innerAxiom.addTerm(term);
                            }
                            axiomTermList.setAxiom(innerAxiom);
                        }
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
        return null;
    }

}
