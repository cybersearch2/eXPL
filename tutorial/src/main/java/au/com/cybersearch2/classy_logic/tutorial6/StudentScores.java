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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Lists
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class StudentScores 
{
	static final String LISTS = 
		"axiom grades (student, english, maths, history):\n" +
		" (\"George\", 15, 13, 16),\n" +
		" (\"Sarah\", 12, 17, 15),\n" +
		" (\"Amy\", 14, 16, 6);\n" +
		" list<string> mark;\n" +
		" mark[0] = \"f-\";\n" +
		" mark[1] = \"f\";\n" +
		" mark[2] = \"f+\";\n" +
		" mark[3] = \"e-\";\n" +
		" mark[4] = \"e\";\n" +
		" mark[5] = \"e+\";\n" +
		" mark[6] = \"d-\";\n" +
		" mark[7] = \"d\";\n" +
		" mark[8] = \"d+\";\n" +
		" mark[9] = \"c-\";\n" +
		" mark[10] = \"c\";\n" +
		" mark[11] = \"c+\";\n" +
		" mark[12] = \"b-\";\n" +
		" mark[13] = \"b\";\n" +
		" mark[14] = \"b+\"\n;" +
		" mark[15] = \"a-\"\n;" +
		" mark[16] = \"a\";\n" +
		" mark[17] = \"a+\";\n" +
		" template score(student, mark[english], mark[maths], mark[history]);\n" +
		" query marks(grades : score);"
		;

	/**
	 * Compiles the LISTS script and runs the "marks" query, displaying the solution on the console.<br/>
	 * Demonstrates a values list. See StudentScores2 for perhaps a better alternative to using a values list.
	 * The expected result:<br/>
	 * 	score(student = George, mark.english = a-, mark.maths = b, mark.history = a)<br/>
	 *	score(student = Sarah, mark.english = b-, mark.maths = a+, mark.history = a-)<br/>
	 *	score(student = Amy, mark.english = b+, mark.maths = a, mark.history = d-)<br/>
	 * @throws ParseException
	 */
	public void displayLists() throws ParseException
	{
		QueryProgram queryProgram = compileScript(LISTS);
		queryProgram.executeQuery("marks", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("score").toString());
				return true;
			}});
	}

	protected QueryProgram compileScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
		return queryProgram;
	}
	
	public static void main(String[] args)
	{
		StudentScores listsDemo = new StudentScores();
		try 
		{
			listsDemo.displayLists();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
