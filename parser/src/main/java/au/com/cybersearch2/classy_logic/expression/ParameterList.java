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
package au.com.cybersearch2.classy_logic.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ParameterList
 * Contains named parameters which are passed to a function which creates an object dependent on these parameters.
 * Note that the ower of the parameter list is responsible for performing unification on the Operand objects it contains.
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterList<R>
{
    protected static List<Term> EMPTY_TERM_LIST;
    /** Performs function using parameters collected after query evaluation and returns value */
    protected CallEvaluator<R> callEvaluator;
    /** List of Operand arguments or null for no arguments */
    protected List<Template> templateParamList;
    Template parametersTemplate;
    
    static
    {
        EMPTY_TERM_LIST = Collections.emptyList();
    }
    
    /**
     * Construct a ParameterList object which uses parameters in an an Expression operand 
     * and a supplied evaluator object to create it's value
     * @param templateParamList List of Operand arguments or null for no arguments
     * @param callEvaluator Executes function using parameters and returns object of generic type
     */
    public ParameterList(List<Template> templateParamList, CallEvaluator<R> callEvaluator) 
    {
        this.templateParamList = templateParamList;
        this.callEvaluator = callEvaluator;
    }

    /**
     * Construct a ParameterList object which uses parameters in an an Expression operand 
     * and a supplied evaluator object to create it's value
     * @param parametersTemplate Operand arguments packaged in an inner template or null for no arguments
     * @param callEvaluator Executes function using parameters and returns object of generic type
     */
    public ParameterList(Template parametersTemplate, CallEvaluator<R> callEvaluator) 
    {
        this.parametersTemplate = parametersTemplate;
        this.callEvaluator = callEvaluator;
    }
    
    /**
     * Returns list of parameters
     * @return List of OperandParam objects
     */
    public int size()
    {
        return parametersTemplate != null ? 1 : templateParamList.size();
    }

    /**
     * Perform function using parameters
     * @return Object of generic type
     */
    public R evaluate(int id)
    {
        if (parametersTemplate== null)
        {
            if ((templateParamList == null) || templateParamList.isEmpty())
                return callEvaluator.evaluate(EMPTY_TERM_LIST);
        }
        List<Term> argumentList = null;
        if (templateParamList != null)
        {
            argumentList = new ArrayList<Term>(templateParamList.size());
            if ((templateParamList != null) && !templateParamList.isEmpty())
            {   
                for (Template template: templateParamList)
                {
                    template.evaluate(null);
                    argumentList.add(new Parameter(Term.ANONYMOUS, template.toArray()));
                }
            }
        }
        else
        {
            parametersTemplate.evaluate(null);
            argumentList = parametersTemplate.toArray();
        }
        return callEvaluator.evaluate(argumentList);
    }

    public void backup(int id)
    {
        if ((templateParamList != null) && !templateParamList.isEmpty())
        {   
            for (Template template: templateParamList)
                template.backup(id != 0);
        }
        else if (parametersTemplate != null)
            parametersTemplate.backup(id != 0);
    }

    public void setExecutionContext(ExecutionContext context)
    {
        callEvaluator.setExecutionContext(context);
    }
}
