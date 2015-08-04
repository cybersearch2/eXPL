package au.com.cybersearch2.classy_logic.expression;
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

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * CallOperand
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public class CallOperand extends Variable
{
    protected CallEvaluator callEvaluator;
    
    public CallOperand(String name, CallEvaluator callEvaluator, Operand expression)
    {
        super(name, expression);
        this.callEvaluator = callEvaluator;
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = EvaluationStatus.COMPLETE;
        final List<Variable> argumentList = new ArrayList<Variable>();
        if (expression != null)
        {
            status = expression.evaluate(id);
            OperandVisitor visitor = new OperandVisitor(){

                @Override
                public boolean next(Term term, int depth)
                {
                    // Assumes compiler wraps a arguments in Variable wrapper
                    Variable argument = (Variable)term;
                    argumentList.add(argument);
                    return true;
                }};
                visit(expression, visitor, 1);
        }
        this.empty = false;
        this.id = id;
        setValue(callEvaluator.evaluate(argumentList));
        return status;
    }
 
    /**
     * Visit a node of the Operand tree. Recursively navigates left and right operands, if any.
     * @param term The term being visited
     * @param visitor Object implementing OperandVisitor interface
     * @param depth Depth in tree. The root has depth 1.
     * @return flag set true if entire tree formed by this term is navigated. 
     */
    protected boolean visit(Term term, OperandVisitor visitor, int depth)
    {
        // Only Terms which also implement Operand interface will have left and right Operands
        if (!(term instanceof Operand))
        {
            visitor.next(term, depth);
            return true;
        }
        Operand operand = (Operand)term;
        Operand left = operand.getLeftOperand();
        if (left != null)
        {
            if (!(left instanceof Evaluator))
                visitor.next(left, depth);
            else
                visit(left, visitor, depth + 1);
        }
        Operand right = operand.getRightOperand();
        if (right != null)
        {
            if (!(right instanceof Evaluator))
                visitor.next(right, depth);
            else
                visit(right, visitor, depth + 1);
        }
        return true;
    }


}
