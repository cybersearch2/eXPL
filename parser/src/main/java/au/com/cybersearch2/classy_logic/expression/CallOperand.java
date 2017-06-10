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

import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * CallOperand
 * Variable which is set using a supplied function object and parameters contained in a template.
 * The generic "R" type is the function return type.
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class CallOperand<R> extends Variable implements DebugTarget
{
    protected static List<Term> EMPTY_TERM_LIST = Collections.emptyList();
    /** Template containing parameters or null for no arguments */
    protected Template template;
    protected CallEvaluator<R> callEvaluator;

    /**
     * Construct a CallOperand object
     * @param qname Qualified name
     * @param tmplate Template containing parameters or null for no arguments
     * @param callEvaluator Executes function using parameters and returns object of generic type "R"
     */
    public CallOperand(QualifiedName qname, Template template, CallEvaluator<R> callEvaluator) 
    {
        super(qname);
        this.callEvaluator = callEvaluator;
        this.template = template;
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    public EvaluationStatus evaluate(int id)
    {
        List<Term> termList;
        if (template != null)
        {
            template.evaluate(null);
            termList = template.toArray();
        }
        else
            termList = EMPTY_TERM_LIST;
        setValue(callEvaluator.evaluate(termList));
        this.id = id;
        return EvaluationStatus.COMPLETE;
    }
 
    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int id)
    {
        if (template != null)
            template.backup(id != 0);
        return super.backup(id);
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.expression.Variable#toString()
     */
    @Override
    public String toString()
    {
        if (empty)
        {
            StringBuilder builder = new StringBuilder(qname.toString());
            builder.append('(');
            if (template != null)
            {
                Term op1 = template.getTermByIndex(0);
                builder.append(op1.toString());
                int count = template.getTermCount();
                if (count > 1)
                {
                    Term op2 = template.getTermByIndex(count - 1);
                    builder.append(" ... ").append(op2.toString());
                }
            }
            builder.append(')');
            return builder.toString();
        }
        return super.toString();
    }
 
    /**
     * setExecutionContext
     * @see au.com.cybersearch2.classy_logic.interfaces.DebugTarget#setExecutionContext(au.com.cybersearch2.classy_logic.debug.ExecutionContext)
     */
    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        callEvaluator.setExecutionContext(context);
    }
}
