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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * HighCities
 * Solves:  Given list of cities with their elevations, which cities are at 5,000 feet or higher.
 * The cities are defined as an axiom source with each axiom containing a name term and an evelation term.
 * The terms are anonymous, so unification term pairing is performed by position.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class HighCities 
{
	static final String CITY_EVELATIONS =
	    "axiom city:\n" + 
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
		"template high_city(name ? altitude > 5000, altitude);\n" +
	    "query high_cities (city : high_city);\n"; 

	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * high_city(name = addis ababa, altitude = 8000)<br/>
	 * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 * @throws ParseException
	 */
	public void displayHighCities() throws ParseException
	{
		QueryProgram queryProgram = compileScript(CITY_EVELATIONS);
		queryProgram.executeQuery("high_cities", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("high_city").toString());
				return true;
			}});
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
		HighCities highCities = new HighCities();
		try 
		{
			highCities.displayHighCities();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
