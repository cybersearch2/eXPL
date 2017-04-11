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
 * Lists
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class StudentScores2 
{
/* student-scores2.xpl
axiom grades (student, english, maths, history)
 {"George", 15, 13, 16}
 {"Sarah", 12, 17, 15}
 {"Amy", 14, 16, 6};
 axiom alpha_marks()
{
 "", // Start at index 1
 "f-", "f", "f+",
 "e-", "e", "e+",
 "d-", "d", "d+",
 "c-", "c", "c+",
 "b-", "b", "b+",
 "a-", "a", "a+"
};
 list<term> mark(alpha_marks);
 template score(student, english = mark[(english)], maths = mark[(maths)], history = mark[(history)]);
 query marks(grades : score);

*/

    protected QueryProgramParser queryProgramParser;

    public StudentScores2()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
	/**
	 * Compiles the LISTS script and runs the "marks" query, displaying the solution on the console.<br/>
	 * This sample demonstrates using an Axiom Term list as a value list.
	 * The expected result:<br/>
	 * 	score(student = George, english = b+, maths = b-, history = a-)<br/>
	 *	score(student = Sarah, english = c+, maths = a, history = b+)<br/>
	 *	score(student = Amy, english = b, maths = a-, history = e+)<br/>
	 */
    public ParserContext displayLists(SolutionHandler solutionHandler)
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("student-scores2.xpl");
		queryProgram.executeQuery("marks", solutionHandler);
        return queryProgramParser.getContext();
	}

	public static void main(String[] args)
	{
		try 
		{
	        StudentScores2 listsDemo = new StudentScores2();
			listsDemo.displayLists(new SolutionHandler(){
	            @Override
	            public boolean onSolution(Solution solution) {
	                System.out.println(solution.getAxiom("score").toString());
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
