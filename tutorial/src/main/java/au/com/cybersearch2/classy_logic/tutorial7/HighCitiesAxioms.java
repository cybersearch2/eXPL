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

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * HighCities
 * Solves:  Given list of cities with their elevations, which cities are at 5,000 feet or higher.
 * This example features using an Axiom variable to grow the result list by concatenation.
 * The cities are defined as an axiom source with each axiom containing a name term and an evelation term.
 * A template cannot perform Axiom variable operations, so a calculator is used for this example.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class HighCitiesAxioms
{
	static final String CITY_EVELATIONS =
	    "axiom city (name, altitude)\n" + 
        "    {\"bilene\", 1718}\n" +
        "    {\"addis ababa\", 8000}\n" +
        "    {\"denver\", 5280}\n" +
        "    {\"flagstaff\", 6970}\n" +
        "    {\"jacksonville\", 8}\n" +
        "    {\"leadville\", 10200}\n" +
        "    {\"madrid\", 1305}\n" +
        "    {\"richmond\",19}\n" +
        "    {\"spokane\", 1909}\n" +
        "    {\"wichita\", 1305};\n" +
        "axiom high_cities = {};\n" +
        "// Template for name and altitude of a high city\n" +
        "template high_city(\n" +
        "  altitude ? altitude > 5000,\n" +
        "  high_cities += axiom { name , altitude }\n" +
        ");\n" +
	    "query high_cities (city : high_city);\n"; 

	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
     * high_city(name = addis ababa, altitude = 8000)<br/>
     * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 */
	public Iterator<Axiom> getHighCities()
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.parseScript(CITY_EVELATIONS);
		Result result = queryProgram.executeQuery("high_cities"); 
		return result.getIterator(QualifiedName.parseGlobalName("high_cities"));
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
