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
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * NestedLoops
 * Demonstrates  one loop nested inside another. 
 * The inner loop does an insert sort while the outer loop advances to the next axiom term to be sorted. 
 * @author Andrew Bowley
 * 5 Mar 2015
 */
public class NestedLoops 
{
/* nested-loops.xpl
axiom unsorted() {12, 3, 1, 5, 8};
list<term> sorted(unsorted);
calc insert_sort 
(
  integer i = 1, 
  {
    integer j = i - 1, 
    integer temp = sorted[i], 
    {
      ? temp < sorted[j],
         sorted[j + 1] = sorted[j],
      ? --j >= 0
    },
    sorted[j + 1] = temp,
    ? ++i < sorted.length
  }
)
query sort_axiom (insert_sort);

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public NestedLoops()
    {
        File resourcePath = new File("src/main/resources/tutorial9");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

/**
	 * Compiles the INSERT_SORT script and runs the "sort_axiom" query, displaying the solution on the console.<br/>
	 * Demonstrates one loop nested inside another. The inner loop does an insert sort 
	 * while the outer loop advances to the next axiom term to be sorted.
	 * The expected result:<br/>
	 * 1, 3, 5, 8, 12<br/>
	 */
	public Axiom displayAxiomSort()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("nested-loops.xpl");
        parserContext = queryProgramParser.getContext();
		Result result = queryProgram.executeQuery("sort_axiom");
		return result.getAxiom("insert_sort.sorted");
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		NestedLoops nestedLoops = new NestedLoops();
		try 
		{
			Axiom axiom = nestedLoops.displayAxiomSort();
	        System.out.println(axiom.toString());
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
