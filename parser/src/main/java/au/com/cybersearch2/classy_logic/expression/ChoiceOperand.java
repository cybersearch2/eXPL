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

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ChoiceOperand
 * @author Andrew Bowley
 * 5 Sep 2015
 */
public class ChoiceOperand extends IntegerOperand implements DebugTarget
{
    protected Template template;
    protected Choice choice;
    /** Execution context for debugging */
    protected ExecutionContext context;
    
    public ChoiceOperand(QualifiedName qname, Template template, Choice choice)
    {
        super(qname);
        this.template = template;
        this.choice = choice;
    }
    
    /**
     * Evaluate loop
     * @param id Not used as evaluation and backup are local only
     * @return Flag set true
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        this.id = id;
        Parameter param = null;
        if (choice.completeSolution(template, id, context))
            param = new Parameter(Term.ANONYMOUS, (long)choice.getSelection()); // Value indicates row index
        else
        {    
            // Only backup local changes
            template.backup(true);
            param = new Parameter(Term.ANONYMOUS, (long)Choice.NO_MATCH); // Value indicates no match
        }
        param.setId(id);
        assign(param);
        return EvaluationStatus.COMPLETE;
    }

    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Not used. 
     * @return Flag set true
     */
    @Override
    public boolean backup(int id)
    {   
        super.backup(id);
        choice.backup(id);
        // Changes managed locally
        template.backup(id);
        if (template.getId() != id)
            template.backup(id != 0);
        return true;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
     */
    @Override
    public String toString() 
    {
        return "choice " + name;
    }

    /**
     * setExecutionContext
     * @see au.com.cybersearch2.classy_logic.interfaces.DebugTarget#setExecutionContext(au.com.cybersearch2.classy_logic.debug.ExecutionContext)
     */
    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        this.context = context;
    }
}
