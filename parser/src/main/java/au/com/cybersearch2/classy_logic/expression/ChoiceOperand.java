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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * ChoiceOperand
 * @author Andrew Bowley
 * 5 Sep 2015
 */
public class ChoiceOperand extends BooleanOperand
{
    protected Template template;
    protected Choice choice;
    /** Root of Operand tree for unification */
    protected Operand termsTreeRoot;
    
    public ChoiceOperand(QualifiedName qname, Template template, Choice choice)
    {
        super(qname);
        this.template = template;
        this.choice = choice;
        termsTreeRoot = buildOperandTree();
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
        if (choice.completeSolution(template, id))
            assign(Boolean.TRUE); // Value indicates successful completion
        else
        {    
            // Only backup local changes
            template.backup(true);
            assign(Boolean.FALSE); // Value indicates no match
        }
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
        for (int i = 0; i < template.getTermCount(); i++)
        {
            template.getTermByIndex(i).backup(id);
        }
        return true;
    }

    /**
     * Returns root operand of template terms tree     
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return termsTreeRoot;
    }


    /**
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
     */
    @Override
    public String toString() 
    {
        return name;
    }

    /**
     * Build parameter tree for unification
     */
    public Operand buildOperandTree()
    {
        int index = 0;
        Operand[] params = new Operand[2];
        params[0] = (Operand)template.getTermByIndex(index++);
        while (true)
        {
            if (index == template.getTermCount())
                break;
            params[1] = (Operand)template.getTermByIndex(index++);
            params[0] = new Evaluator(params[0], ",", params[1]);
        }
        return params[0];
    }
}
