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
package au.com.cybersearch2.classy_logic.tutorial1;

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
 * NamedHighCities
 * Solves:  Given list of cities with their elevations, which cities are at 5,000 feet or higher.
 * The cities are defined as an axiom source with each axiom containing a name term and an elevation term.
 * The terms are name, so unification term pairing is performed by name and position does not matter as
 * shown by the fact the axiom terms are in reverse order to the template terms.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class NamedHighCities 
{
/* named-high-cities.xpl
 axiom city_altitude(altitude, name) 
    {1718, "bilene"}
    {8000, "addis ababa"}
    {5280, "denver"}
    {6970, "flagstaff"}
    {8, "jacksonville"}
    {10200, "leadville"}
    {1305, "madrid"}
    {19, "richmond"}
    {1909, "spokane"}
    {1305, "wichita"};

template high_city(city ? altitude > 5000, altitude);

query<axiom> high_cities (city_altitude : high_city);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    public NamedHighCities()
    {
        File resourcePath = new File("src/main/resources/tutorial1");
        queryProgramParser = new QueryProgramParser(resourcePath);
     }

    /**
     * Compiles the named-high-cities.xpl script and runs the "high_city" query
     */
    public Iterator<Axiom> findHighCities() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("named-high-cities.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("high_cities");
        return result.axiomIterator("high_cities");
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	/**
	 * Compiles the named-high-cities.xpl script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * high_city(name = addis ababa, altitude = 8000)<br/>
	 * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 */
    public static void main(String[] args)
    {
        try 
        {
            NamedHighCities highCities = new NamedHighCities();
            Iterator<Axiom> iterator = highCities.findHighCities();
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
