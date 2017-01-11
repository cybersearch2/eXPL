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

import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.ScopeContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryLauncher;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * QueryEvaluator
 * Adapter to run a query as a function call and return result as an AxiomTermList.
 * @author Andrew Bowley
 * 1 Aug 2015
 */
public class QueryEvaluator  extends QueryLauncher implements CallEvaluator<AxiomTermList>
{
    /** Query parameters */
    protected QueryParams queryParams;
    /** Caller's scope */
    protected Scope callerScope;
    /** Flag to indicate if call to Calculator in same scope */
    protected boolean isCallInScope;
    /** Optional inner Template to receive query results */
    protected Template innerTemplate;
    /** Term belonging to inner template - holds AxiomTermList */
    protected Term innerTerm;

    /**
     * Construct a QueryEvaluator object for a query specified by query parameters
     * @param queryParams  Query parameters
     * @param scope The caller's scope
     * @param innerTemplate Optional inner Template to receive query results
     */
    public QueryEvaluator(QueryParams queryParams, Scope scope, Template innerTemplate)
    {
        this.queryParams = queryParams;
        this.callerScope = scope;
        this.isCallInScope = queryParams.getScope() == scope;
        this.innerTemplate = innerTemplate;
        OperandMap callerOperandMap = callerScope.getParserAssembler().getOperandMap();
        if (innerTemplate != null)
        {   // Get term belonging to inner template 
            QualifiedName innerTemplateName = innerTemplate.getQualifiedName();
            innerTerm = callerOperandMap.get(innerTemplateName);
        }
        else
        {   // Create empty AxiomTermList to avoid null issues
            KeyName firstKeyname = queryParams.getQuerySpec().getKeyNameList().get(0);
            QualifiedName qname = QualifiedName.parseName(firstKeyname.getTemplateName().getName(), callerOperandMap.getQualifiedContextname());
            // Create empty AxiomTermList to return as query result. 
            innerTerm = new Parameter(qname.getName(), new AxiomTermList(qname, firstKeyname.getTemplateName().getTemplate()));
        }
            
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
    public AxiomTermList evaluate(List<Term> argumentList)
    {
        QuerySpec querySpec = queryParams.getQuerySpec();
        String templateName = getCalculatorKeyName(querySpec).getTemplateName().getTemplate();
        Scope scope = queryParams.getScope();
        Template template = scope.findTemplate(templateName);
        final String solutionName =  template.getQualifiedName().toString();
        // Marshall arguments provided as a list of Variables into a properties container 
        if (argumentList.size() > 0)
            prepareArguments(argumentList, template);
        // Set SolutionHander to collect results
        SolutionHandler solutionHandler = getSolutionHandler(solutionName, template.getId());
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
            queryParams.clearParameters(templateName);
            if (scopeContext != null)
               // Scope restored to original state
                scopeContext.resetScope();
            else
                template.pop();
        }
        return (AxiomTermList) innerTerm.getValue();
    }

    /**
     * Set query parameters object with query arguments packed into an axiom
     * @param argumentList List of terms holding argument values
     * @param template Target query template
     */
    protected void prepareArguments(List<Term> argumentList, Template template)
    {
        int index = 0; // Map unnamed arguments to template term names
        Axiom axiom = new Axiom(template.getKey());
        for (Term argument: argumentList)
        {
            // All parameters are Parameters with names set by caller and possibly empty for match by position.
            String argName = argument.getName();
            if (argName.isEmpty())
            {
                if (index == template.getTermCount())
                    throw new ExpressionException("Unnamed argument at position " + index + " out of bounds");
                argName = template.getTermByIndex(index).getName();
            }
            axiom.addTerm(new Parameter(argName, argument.getValue()));
            ++index;
        }
        queryParams.putParameters(template.getQualifiedName(), axiom);
    }

    /**
     * Returns solution handler to marshall query result into axiom term list
     * @param solutionName Name to use to obtain solution
     * @param id Modification id
     * @return SolutionHandler object which does nothing if there is no inner template
     */
    protected SolutionHandler getSolutionHandler(final String solutionName, final int id)
    {
        return innerTemplate == null ? 
                QueryParams.DO_NOTHING : 
                new SolutionHandler(){
        @Override
        public boolean onSolution(Solution solution)
        {
            Axiom axiom = solution.getAxiom(solutionName);
            if (axiom != null)
            {
                // No backup, so reset before unification
                innerTemplate.reset();
                if (axiom.unifyTemplate(innerTemplate, solution))
                {
                    if (innerTemplate.evaluate() == EvaluationStatus.COMPLETE);
                    {
                        Axiom innerAxiom = new Axiom(innerTemplate.getKey());
                        for (int i = 0; i < (innerTemplate.getTermCount()); i++)
                        {
                            String termName = innerTemplate.getTermByIndex(i).getName();
                            Term term = axiom.getTermByName(termName);
                            if (term == null)
                                term = new Parameter(termName);
                            innerAxiom.addTerm(term);
                        }
                        axiomTermListInstance(id).setAxiom(innerAxiom);
                    }
                }
            }
            return true;
        }};
    }
 
    /**
     * Returns axiom term list instance. Also stores it in inner term
     * @param id Modification id
     * @return Empty AxiomTermList object
     */
    protected AxiomTermList axiomTermListInstance(int id)
    {
        // Create AxiomTermList to contain query result. 
        AxiomTermList axiomTermList = new AxiomTermList(innerTemplate.getQualifiedName(), innerTemplate.getKey());
        // The inner term is the axiomTermList container.
        // Over write any previous value, which will be in the solution by now
        Parameter innerTermValue = new Parameter(Term.ANONYMOUS, axiomTermList);
        innerTermValue.setId(id);
        innerTerm.assign(innerTermValue);
        return axiomTermList;
    }
}
