/**
    Copyright (C) 2014  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial9;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Factorial
 * Demonstrates using a calculator loop iteratation to arrive at a result
 * @author Andrew Bowley
 * 5 Mar 2015
 */
public class Factorial 
{
    static final String FACTORIAL_CALCULATE =
	 	"calc factorial (\n" +
	 	"integer i,\n" +
		"integer n,\n" +
		"decimal factorial = 1,\n" +
	    "{\n" +
		"  factorial *= i,\n" +
 		"  ? i++ < n\n" +
	    "}\n" +
		")(factorial = 1, i = 1);\n" +
	    "query factorial (factorial)(n = 4);\n" 
	    ;

	/**
	 * Compiles the FACTORIAL_CALCULATE script and runs the "factorial" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * factorial(i = 5, n = 4, factorial = 24)<br/>
	 * @throws ParseException
	 */
	public void display4Factorial()
	{
		QueryProgram queryProgram = new QueryProgram(FACTORIAL_CALCULATE);
		queryProgram.executeQuery("factorial", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("factorial").toString());
				return true;
			}});
	}

	public static void main(String[] args)
	{
		Factorial factorial = new Factorial();
		try 
		{
			factorial.display4Factorial();
		} 
		catch (ExpressionException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
        catch (QueryExecutionException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
		System.exit(0);
	}
}
