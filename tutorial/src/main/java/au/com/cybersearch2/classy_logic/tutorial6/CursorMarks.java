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
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * AssignMarks
 * Demonstrates list cursor.
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class CursorMarks 
{
/* append-marks.xpl
axiom grades (student, english, maths, history)
 {"George", 15, 13, 16}
 {"Sarah", 12, 17, 15}
 {"Amy", 14, 16, 6};
 
list<string> mark(1,18);
 mark[18] = "a+";
 mark[17] = "a";
 mark[16] = "a-";
 mark[15] = "b+";
 mark[14] = "b";
 mark[13] = "b-";
 mark[12] = "c+";
 mark[11] = "c";
 mark[10] = "c-";
 mark[9]  = "d+";
 mark[8]  = "d";
 mark[7]  = "d-";
 mark[6]  = "e+";
 mark[5]  = "e";
 mark[4]  = "e-";
 mark[3]  = "f+";
 mark[2]  = "f";
 mark[1]  = "f-";
 
list<integer> student_grades;

template<term> grades
(
  student, 
  student_grades[0] = english, 
  student_grades[1] = maths, 
  student_grades[2] = history
);

template score
+ export list<string> student_marks;
(
  student_cursor = student_grades.cursor,
  student_marks += student + 
    ": English = " + mark[student_cursor++] + 
    ", Maths ="    + mark[student_cursor++] + 
    ", History = " + mark[student_cursor++]
);
 
query marks(grades : grades) -> (score);

*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public CursorMarks()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
	/**
	 * Compiles the cursor-marks.xpl script and runs the "marks" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
        George: English = b+, Maths =b-, History = a-<br/>
        Sarah: English = c+, Maths =a, History = b+<br/>
        Amy: English = b, Maths =a-, History = e+<br/>	
     */
	public Iterator<String> displayStudentMarks()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("cursor-marks.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("marks");
        return result.stringIterator("student_marks.score@");
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
	        CursorMarks listsDemo = new CursorMarks();
			Iterator<String> iterator = listsDemo.displayStudentMarks();
			while (iterator.hasNext())
	            System.out.println(iterator.next());
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
