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
package au.com.cybersearch2.classy_logic.tutorial6;

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Colors
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class Gaming {

/* gaming.xpl
axiom spin (r1, r2, r3, r4) {3,2,0,1};
axiom fruit() {"apple", "orange", "banana", "lemon"};
list<term> combo(fruit);
template spin(combo[(r1)], combo[(r2)], combo[(r3)], combo[(r4)]);
query spin(spin : spin);

*/
    protected QueryProgramParser queryProgramParser;

    public Gaming()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
	 * Compiles the FRUITS_POKER script and runs the "spin" query, displaying the solution on the console.<br/>
	 * The sample demonstrates enclosing a variable in parentheses to avoid it being interpreted as a term name.<br/>
	 * Class StudentScores2 provides another example of parentheses.<\br>
	 * The expected result:<br/>
	 * spin(combo_r1 = lemon, combo_r2 = banana, combo_r3 = apple, combo_r4 = orange)<br/>
	 */
	public ParserContext displayFruit(SolutionHandler solutionHandler)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("gaming.xpl");
		queryProgram.executeQuery("spin", solutionHandler);
        return queryProgramParser.getContext();
	}

	public static void main(String[] args)
	{
		try 
		{
	        Gaming gaming = new Gaming();
			gaming.displayFruit(new SolutionHandler(){
	            @Override
	            public boolean onSolution(Solution solution) {
	                System.out.println(solution.getAxiom("spin").toString());
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
