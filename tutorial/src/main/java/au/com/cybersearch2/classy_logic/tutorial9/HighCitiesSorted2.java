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
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * HighCities
 * Demonstrates template export by assigning an axiom type to the template
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class HighCitiesSorted2 
{
/* high-cities-sorted2.xpl
axiom city (name, altitude)
    {"bilene", 1718}
    {"addis ababa", 8000}
    {"denver", 5280}
    {"flagstaff", 6970}
    {"jacksonville", 8}
    {"leadville", 10200}
    {"madrid", 1305}
    {"richmond",19}
    {"spokane", 1909}
    {"wichita", 1305};
    


// Template to filter high cities
// Solution is a list named 'high_cities'
template<axiom> high_cities
(
  name,
  altitude ? altitude > 5000
);

// Calculator to perform insert sort on high_cities
calc insert_sort 
(
  // i is index to last item appended to the list
  integer i = high_cities.length - 1,
  // Skip first time when only one item in list
  : i < 1,
  // j is the swap index
  integer j = i - 1,
  // Save axiom to swap
  temp = high_cities[i],
  // Shuffle list until sort order restored
  {
    ? altitude < high_cities[j].altitude,
    high_cities[j + 1] = high_cities[j],
    ? --j >= 0
  },
  // Insert saved axiom in correct position
  high_cities[j + 1] = temp
);

query high_cities (city : high_cities) -> (insert_sort); 

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public HighCitiesSorted2()
    {
        File resourcePath = new File("src/main/resources/tutorial9");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the high-cities-sorted2.xpl script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = addis ababa, altitude = 8000)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 */
	public Iterator<Axiom> displayHighCities()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("high-cities-sorted2.xpl");
        parserContext = queryProgramParser.getContext();
		Result result = queryProgram.executeQuery("high_cities");
		return result.axiomIterator("high_cities");
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		HighCitiesSorted2 highCities = new HighCitiesSorted2();
		try 
		{
	        Iterator<Axiom> iterator = highCities.displayHighCities();
	        while(iterator.hasNext())
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
