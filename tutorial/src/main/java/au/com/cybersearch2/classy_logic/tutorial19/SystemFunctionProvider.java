/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial19;

import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * SystemFunctionProvider
 * @author Andrew Bowley
 * 22Jun.,2017
 */
public class SystemFunctionProvider implements FunctionProvider<Void>
{
    @Override
    public String getName()
    {
        return "system";
    }

    @Override
    public CallEvaluator<Void> getCallEvaluator(String identifier)
    {
        if (identifier.equals("print"))
            return new CallEvaluator<Void>(){

                @Override
                public String getName()
                {
                    return "print";
                }

                @Override
                public Void evaluate(List<Term> argumentList)
                {
                    for (Term term: argumentList)
                        System.out.print(term.getValue().toString());
                    System.out.println();
                    return null;
                }

                @Override
                public void setExecutionContext(ExecutionContext context)
                {
                }
                
        };
        throw new ExpressionException("Unknown function identifier: " + identifier);
    }
}
