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
package au.com.cybersearch2.classy_logic.tutorial7;

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
 * HighCitiesAxioms
 * Demonstrates query type specification (query<axiom>) to send solution to result as a list.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class HighCitiesAxioms
{
/* high-cities-axioms.xpl
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

// Template for name and altitude of a high city
template high_city
(
  name ? altitude > 5000,
  altitude
);

query<axiom> high_cities (city : high_city);
*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public HighCitiesAxioms()
    {
        File resourcePath = new File("src/main/resources/tutorial7");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the high-cities-axioms script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
     * high_city(name = addis ababa, altitude = 8000)<br/>
     * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 */
	public Iterator<Axiom> getHighCities()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("high-cities-axioms.xpl");
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
		try 
		{
		    HighCitiesAxioms highCities = new HighCitiesAxioms();
	        Iterator<Axiom> iterator = highCities.getHighCities();
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
