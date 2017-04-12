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

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
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
/*
calc factorial 
(
  integer n,
  integer i = 1,
  decimal factorial = 1,
  {
    factorial *= i,
    ? i++ < n
  }
);
query factorial4 (factorial)(n = 4); 
query factorial5 (factorial)(n = 5); 

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public Factorial()
    {
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/tutorial9"));
    }
    
    /**
	 * Compiles the FACTORIAL_CALCULATE script and runs the "factorial" query, displaying the solution on the console.<br/>
	 * Also shows use of query parameter to set a variable.<br/>
	 * The expected result:<br/>
	 * factorial(n = 4, i = 5, factorial = 24)<br/>
	 * factorial(n = 5, i = 6, factorial = 120)<br/>
	 * @throws ParseException
	 */
	public ParserContext display4Factorial(SolutionHandler solutionHandler)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("factorial.xpl");
		queryProgram.executeQuery("factorial4", solutionHandler);
        queryProgram.executeQuery("factorial5", solutionHandler);
        return queryProgramParser.getContext();
	}

	public static void main(String[] args)
	{
		Factorial factorial = new Factorial();
		try 
		{
			factorial.display4Factorial(new SolutionHandler(){
	            @Override
	            public boolean onSolution(Solution solution) {
	                System.out.println(solution.getAxiom("factorial").toString());
	                return true;
	            }});
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
