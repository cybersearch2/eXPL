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
package au.com.cybersearch2.classy_logic.tutorial4;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * ExpressionsTest
 * @author Andrew Bowley
 * 5Feb.,2017
 */
public class ExpressionsTest
{
    @Test
    public void testExpressions() throws Exception
    {
        Expressions expressions = new Expressions();
        expressions.checkExpressions(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                Term evaluateTerm = solution.getAxiom("evaluate").getTermByName("can_evaluate");
                assertThat(((Boolean)evaluateTerm.getValue()).booleanValue()).isTrue();
                return true;
            }});
    }
    
    protected void checkSolution(BufferedReader reader, String city)
    {
        try
        {
            String line = reader.readLine();
            assertThat(city).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
