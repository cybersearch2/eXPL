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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;

/**
 * NestedLoops
 * @author Andrew Bowley
 * 5 Mar 2015
 */
public class NestedLoops 
{
    static final String INSERT_SORT =
    		"axiom unsorted : (12, 3, 1, 5, 8);\n" +
            "list<term> sorted(unsorted);\n" +
    		"calc insert_sort (\n" +
    		"integer i = 1, \n" +
    		"{\n" +
    		"  integer j = i - 1, \n" +
    		"  integer temp = sorted[i], \n" +
    		"  {\n" +
    		"    ? temp < sorted[j],\n" +
       		"    sorted[j + 1] = sorted[j],\n" +
    		"    ? --j >= 0\n" +
    		"  },\n" +
     		"  sorted[j + 1] = temp,\n" +
    		"  ? ++i < length(sorted)\n" +
    		"}\n" +
            ")\n;" +
    	    "query sort_axiom calc(insert_sort);\n" 
    		;

	/**
	 * Compiles the INSERT_SORT script and runs the "sort_axiom" query, displaying the solution on the console.<br/>
	 * Demonstrates one loop nested inside another. The inner loop does an insert sort 
	 * while the outer loop advances to the next axiom term to be sorted.
	 * The expected result:<br/>
	 * 1, 3, 5, 8, 12<br/>
	 * @throws ParseException
	 */
	public void displayAxiomSort() throws ParseException
	{
		QueryProgram queryProgram = compileScript(INSERT_SORT);
		Result result = queryProgram.executeQuery("sort_axiom");
		@SuppressWarnings("unchecked")
		Iterator<Object> axiomIterator = ((Iterable<Object>) result.getList("sorted")).iterator();
		boolean firstTime = true;
		while (axiomIterator.hasNext())
		{
			if (firstTime)
				firstTime = false;
			else
				System.out.print(", ");
			System.out.print(axiomIterator.next().toString());
		}
		System.out.println();
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
		NestedLoops nestedLoops = new NestedLoops();
		try 
		{
			nestedLoops.displayAxiomSort();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

}
