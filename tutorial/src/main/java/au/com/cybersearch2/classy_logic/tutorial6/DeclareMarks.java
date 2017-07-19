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
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * DeclareMarks
 * Shows basic type list set using initializer set in declaration. 
 * The marks array starts at index 1 by inserting a blank term at the first position.
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class DeclareMarks 
{
/* declare-marks.xpl
axiom grades (student, english, maths, history)
  {"George", 15, 13, 16}
  {"Sarah", 12, 17, 15}
  {"Amy", 14, 16, 6};
 
list<string> mark =
{
   "", // Index = 0 is out of range
   "f-", "f", "f+", "e-", "e", "e+", "d-", "d", "d+", 
   "c-", "c", "c+", "b-", "b", "b+", "a-", "a", "a+"
};
 
template score(student, mark[english], mark[maths], mark[history]);
 
query<axiom> marks(grades : score);

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public DeclareMarks()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
        Archetype.CASE_INSENSITIVE_NAME_MATCH = false;
    }
    
	/**
	 * Compiles the declare-marks.xpl script and runs the "marks" query, displaying the solution on the console.<br/>
	 * This sample demonstrates using an Axiom Term list as a value list.
	 * The expected result:<br/>
        score(student=George, English=b+, Maths=b-, History=a-)<br/>
        score(student=Sarah, English=c+, Maths=a, History=b+)<br/>
        score(student=Amy, English=b, Maths=a-, History=e+)<br/>	 
     */
    public Iterator<Axiom> displayLists()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("declare-marks.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("marks");
        return result.axiomIterator("marks");
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		try 
		{
	        DeclareMarks listsDemo = new DeclareMarks();
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
