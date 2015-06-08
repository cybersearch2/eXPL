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

import au.com.cybersearch2.classy_logic.QueryProgram;
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
	static final String LISTS = 
		"axiom grades (student, english, maths, history):\n" +
		" (\"George\", 15, 13, 16),\n" +
		" (\"Sarah\", 12, 17, 15),\n" +
		" (\"Amy\", 14, 16, 6);\n" +
		" axiom alpha_marks :\n" +
		"(\n" +
		" \"f-\", \"f\", \"f+\",\n" +
		" \"e-\", \"e\", \"e+\",\n" +
		" \"d-\", \"d\", \"d+\",\n" +
		" \"c-\", \"c\", \"c+\",\n" +
		" \"b-\", \"b\", \"b+\",\n" +
		" \"a-\", \"a\", \"a+\"\n" +
		");\n" +
		" list<term> mark(alpha_marks);\n" +
		" template score(student, mark[(english)], mark[(maths)], mark[(history)]);\n" +
		" query marks(grades : score);"
		;

	/**
	 * Compiles the LISTS script and runs the "marks" query, displaying the solution on the console.<br/>
	 * This sample demonstrates using an Axiom Term list as a value list.
	 * The expected result:<br/>
	 * 	score(student = George, mark.english = a-, mark.maths = b, mark.history = a)<br/>
	 *	score(student = Sarah, mark.english = b-, mark.maths = a+, mark.history = a-)<br/>
	 *	score(student = Amy, mark.english = b+, mark.maths = a, mark.history = d-)<br/>
	 */
	public void displayLists()
	{
		QueryProgram queryProgram = new QueryProgram(LISTS);
		queryProgram.executeQuery("marks", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("score").toString());
				return true;
			}});
	}

	public static void main(String[] args)
	{
		try 
		{
	        StudentScores2 listsDemo = new StudentScores2();
			listsDemo.displayLists();
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
