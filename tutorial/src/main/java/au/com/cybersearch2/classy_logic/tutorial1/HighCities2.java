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

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * HighCities2
 * Solves:  Given list of cities with their elevations, which cities are at 5,000 feet or higher.
 * The cities are defined as an axiom source with each axiom containing a name term and an evelation term.
 * The terms are name, so unification term pairing is performed by name and position does not matter as
 * shown by the fact the axiom terms are in reverse order to the template terms.
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class HighCities2 
{
	static final String CITY_EVELATIONS =
	    "axiom city(altitude, name)\n" + 
	        "    {1718, \"bilene\"}\n" +
	        "    {8000, \"addis ababa\"}\n" +
	        "    {5280, \"denver\"}\n" +
	        "    {6970, \"flagstaff\"}\n" +
	        "    {8, \"jacksonville\"}\n" +
	        "    {10200, \"leadville\"}\n" +
	        "    {1305, \"madrid\"}\n" +
	        "    {19, \"richmond\"}\n" +
	        "    {1909, \"spokane\"}\n" +
	        "    {1305, \"wichita\"};\n" +
		"template high_city(name ? altitude > 5000, altitude);\n" +
	    "query high_cities (city : high_city);\n"; 

	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * high_city(name = addis ababa, altitude = 8000)<br/>
	 * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 */
	public void displayHighCities()
	{
		QueryProgram queryProgram = new QueryProgram(CITY_EVELATIONS);
		queryProgram.executeQuery("high_cities", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("high_city").toString());
				return true;
			}});
	}

	public static void main(String[] args)
	{
		try 
		{
	        HighCities2 highCities = new HighCities2();
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
