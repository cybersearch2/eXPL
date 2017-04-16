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
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * Lists
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class StudentScores 
{
/* student-scores.xpl
axiom grades (student, english, maths, history)
 {"George", 15, 13, 16}
 {"Sarah", 12, 17, 15}
 {"Amy", 14, 16, 6};
 list<string> mark;
 mark[0]  = "f-";
 mark[1]  = "f";
 mark[2]  = "f+";
 mark[3]  = "e-";
 mark[4]  = "e";
 mark[5]  = "e+";
 mark[6]  = "d-";
 mark[7]  = "d";
 mark[8]  = "d+";
 mark[9]  = "c-";
 mark[10] = "c";
 mark[11] = "c+";
 mark[12] = "b-";
 mark[13] = "b";
 mark[14] = "b+"
 mark[15] = "a-"
 mark[16] = "a";
 mark[17] = "a+";
 template score(student, mark[english], mark[maths], mark[history]);
 query<axiom> marks(grades : score);

*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public StudentScores()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
	/**
	 * Compiles the LISTS script and runs the "marks" query, displaying the solution on the console.<br/>
	 * Demonstrates a values list. See StudentScores2 for perhaps a better alternative to using a values list.
	 * The expected result:<br/>
        score(student = George, mark_english = b+, mark_maths = b-, mark_history = a-)<br/>
        score(student = Sarah, mark_english = c+, mark_maths = a, mark_history = b+)<br/>
        score(student = Amy, mark_english = b, mark_maths = a-, mark_history = e+)<br/>
	 */
	public Iterator<Axiom> displayLists()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("student-scores.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("marks");
        return result.getIterator("marks");
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	protected QueryProgram compileScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
	    ParserContext context = new ParserContext(queryProgram);
		queryParser.input(context);
		return queryProgram;
	}
	
	public static void main(String[] args)
	{
		try 
		{
	        StudentScores listsDemo = new StudentScores();
			Iterator<Axiom> iterator = listsDemo.displayLists();
			while (iterator.hasNext())
	            System.out.println(iterator.next().toString());
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
