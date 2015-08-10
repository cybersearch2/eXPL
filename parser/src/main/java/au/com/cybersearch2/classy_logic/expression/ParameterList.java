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
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * ParameterList
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterList<R>
{
    /** Performs function using parameters contained in expression and returns value */
    protected CallEvaluator<R> callEvaluator;
    protected Operand expression;
    
    /**
     * Construct a ParameterList object which uses parameters in an an Expression operand 
     * and a supplied evaluator object to create it's value
     * @param name Name of Variable
     * @param expression Operand to initialize this Variable upon evaluation
     */
    protected ParameterList(Operand expression, CallEvaluator<R> callEvaluator) 
    {
        this.expression = expression;
        this.callEvaluator = callEvaluator;
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    public R evaluate()
    {
        final List<Term> argumentList = new ArrayList<Term>();
        // TODO - Change to using expression in Variable
        if ((expression != null) && !expression.isEmpty())
        {
            OperandVisitor visitor = new OperandVisitor(){
    
                @Override
                public boolean next(Term term, int depth)
                {
                    // Wrap non-Variable arguments in Variable wrapper
                    argumentList.add(term);
                    return true;
                }};
                visit(expression, visitor, 1);
        }
        return callEvaluator.evaluate(argumentList);
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
        if (!(term instanceof Evaluator))
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
