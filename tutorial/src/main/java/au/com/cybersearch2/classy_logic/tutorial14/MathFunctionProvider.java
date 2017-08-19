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
package au.com.cybersearch2.classy_logic.tutorial14;

import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * MathFunctionProvider
 * Implements math.add() function
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class MathFunctionProvider implements FunctionProvider
{

    @Override
    public String getName()
    {
        return "math";
    }

    @Override
    public CallEvaluator<Axiom> getCallEvaluator(String identifier)
    {
        if (identifier.equals("add"))
            return new CallEvaluator<Axiom>(){

            /**
             * Returns function name
             * @see au.com.cybersearch2.classy_logic.interfaces.CallEvaluator#getName()
             */
                @Override
                public String getName()
                {
                    return "add";
                }

                /**
                 * Returns sum of terms passed to function
                 * @see au.com.cybersearch2.classy_logic.interfaces.CallEvaluator#evaluate(java.util.List)
                 */
                @Override
                public Axiom evaluate(List<Term> argumentList)
                {
                    // Return not-a-number value if argument list is invalid
                    // Note return value type, if specified must be number type - integer, double or decimal
                    if ((argumentList == null) || argumentList.isEmpty())
                    {
                        Axiom axiom = new Axiom("NaN");
                        axiom.addTerm(new Parameter(Term.ANONYMOUS, Double.NaN));
                        return axiom;
                    }
                    long addendum = 0;
                    // Sum values assuming all are of type integer
                    for (int i = 0; i < argumentList.size(); i++)
                    {
                        Term term = argumentList.get(i);
                        // Check type and throw exception for invalid type
                        if (term.getValueClass() == Long.class)
                        {
                            Long param = (Long)term.getValue();
                            addendum += param.longValue();
                        }
                        else
                            throw new ExpressionException("math.add passed invalid value: " + term.getValue().toString());
                    }
                    Axiom axiom = new Axiom(Long.toString(addendum));
                    axiom.addTerm(new Parameter(Term.ANONYMOUS, addendum));
                    return axiom;
                }

                @Override
                public void setExecutionContext(ExecutionContext context)
                {
                    // Not supported
                }
            };
            
         // Throw exception for unrecognized function name   
         throw new ExpressionException("Unknown function identifier: " + identifier);
    }
}
