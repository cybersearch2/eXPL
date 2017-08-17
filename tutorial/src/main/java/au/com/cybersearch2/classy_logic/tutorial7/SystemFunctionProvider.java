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
package au.com.cybersearch2.classy_logic.tutorial7;

import java.util.List;
import java.util.Random;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * TimestampProvider
 * @author Andrew Bowley
 * 22Jun.,2017
 */
public class SystemFunctionProvider implements FunctionProvider
{
    @Override
    public String getName()
    {
        return "system";
    }

    @Override
    public CallEvaluator<Axiom> getCallEvaluator(String identifier)
    {
        if (identifier.equals("random"))
            return new CallEvaluator<Axiom>(){

                @Override
                public void setExecutionContext(ExecutionContext context)
                {
                }

                @Override
                public String getName()
                {
                    return "random";
                }

                @Override
                public Axiom evaluate(List<Term> argumentList)
                {
                    Axiom axiom = new Axiom("Random");
                    Random random = new Random();
                    if (argumentList.size() > 0)
                    {
                        Long bound = (Long) argumentList.get(0).getValue();
                        axiom.addTerm(new Parameter(Term.ANONYMOUS, (long)random.nextInt(bound.intValue())));
                        return axiom;
                    }
                    axiom.addTerm(new Parameter(Term.ANONYMOUS, random.nextLong()));
                    return axiom;
                }
        };

        throw new ExpressionException("Unknown function identifier: " + identifier);
    }
}
