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
 * Collects parameters from an Operand tree and passes them to a supplied function object.
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterList<R>
{
    /** Performs function using parameters collected after query evaluation and returns value */
    protected CallEvaluator<R> callEvaluator;
    /** Root of Operand parameter tree or null if no parameters */
    protected Operand parameters;
    
    /**
     * Construct a ParameterList object which uses parameters in an an Expression operand 
     * and a supplied evaluator object to create it's value
     * @param parameters Root of Operand parameter tree or null if no parameters
     * @param callEvaluator Executes function using parameters and returns object of generic type
     */
    public ParameterList(Operand parameters, CallEvaluator<R> callEvaluator) 
    {
        this.parameters = parameters;
        this.callEvaluator = callEvaluator;
    }

    public Operand getParameters()
    {
        return parameters;
    }

    /**
     * Perform function using parameters
     * @return Object of generic type
     */
    public R evaluate()
    {
        final List<Term> argumentList = new ArrayList<Term>();
        if ((parameters != null) && !parameters.isEmpty())
        {   // Collect parameters using visitor
            OperandVisitor visitor = new OperandVisitor(){
    
                @Override
                public boolean next(Term term, int depth)
                {
                    argumentList.add(term);
                    return true;
                }};
                visit(parameters, visitor, 1);
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
        // Only Evaluator terms will have left and right Operands containing parameters
        if (!(term instanceof Evaluator))
        {   // Collect paramater 
            visitor.next(term, depth);
            return true;
        }
        Operand operand = (Operand)term;
        Operand left = operand.getLeftOperand();
        if (left != null)
        {
            if (!(left instanceof Evaluator))
                // Collect paramater 
                visitor.next(left, depth);
            else
                // Keep walking
                visit(left, visitor, depth + 1);
        }
        Operand right = operand.getRightOperand();
        if (right != null)
        {
            if (!(right instanceof Evaluator))
                // Collect paramater 
                visitor.next(right, depth);
            else
                // Keep walking
                visit(right, visitor, depth + 1);
        }
        return true;
    }
}
