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
 * The cities are defined as an axiom source with each axiom containing a name term and an evelation term.
 * The terms are anonymous, so unification term pairing is performed by position.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class HighCitiesSorted 
{
	static final String CITY_EVELATIONS =
	    "axiom city (name, altitude) :\n" + 
        "    (\"bilene\", 1718),\n" +
        "    (\"addis ababa\", 8000),\n" +
        "    (\"denver\", 5280),\n" +
        "    (\"flagstaff\", 6970),\n" +
        "    (\"jacksonville\", 8),\n" +
        "    (\"leadville\", 10200),\n" +
        "    (\"madrid\", 1305),\n" +
        "    (\"richmond\",19),\n" +
        "    (\"spokane\", 1909),\n" +
        "    (\"wichita\", 1305);\n" +
        "// Solution is a list named 'high_cities'\n" +
        "axiom high_cities = {};\n" +
        "// Template to filter high cities\n" +
        "template high_city(\n" +
        "  altitude ? altitude > 5000,\n" +
        "  high_cities += axiom { name, altitude }\n" +
        ");\n" +
        "// Calculator to perform insert sort on high_cities\n" +
        "calc insert_sort (\n" +
        "// i is index to last item appended to the list\n" +
        "integer i = length(high_cities) - 1,\n" +
        "// Skip first time when only one item in list\n" +
        ": i < 1,\n" +
        "// j is the swap index\n" + 
        "integer j = i - 1,\n" +
        "// Get last altitude for sort comparison\n" + 
        "integer altitude = high_cities[i][altitude],\n" +
        "// Save axiom to swap\n" +
        "temp = high_cities[i],\n" +
        "// Shuffle list until sort order restored\n" + 
        "{\n" +
        "  ? altitude < high_cities[j][altitude],\n" +
        "  high_cities[j + 1] = high_cities[j],\n" +
        "  ? --j >= 0\n" +
        "},\n" +
        "// Insert saved axiom in correct position\n" +
        "high_cities[j + 1] = temp);\n" +
	    "query high_cities (city : high_city) >> (insert_sort);\n"; 

	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = addis ababa, altitude = 8000)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 */
	public void displayHighCities()
	{
		QueryProgram queryProgram = new QueryProgram(CITY_EVELATIONS);
		Result result = queryProgram.executeQuery("high_cities");
		Iterator<Axiom> iterator = result.getIterator(QualifiedName.parseGlobalName("high_cities"));
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
	}

	public static void main(String[] args)
	{
		HighCitiesSorted highCities = new HighCitiesSorted();
		try 
		{
			highCities.displayHighCities();
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
