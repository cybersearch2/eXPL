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
package au.com.cybersearch2.classy_logic.tutorial14;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classyinject.DI;

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
	        "axiom city (name, altitude): resource \"cities\";\n" + 
            "// Template for name and altitude of a high city\n" +
            "template high_city(string name, altitude ? altitude > 5000);\n" +
            "// Solution is a list named 'city_list' which receives 'high_city' axioms\n" +
            "list city_list(high_city);\n" +
            "// Calculator to perform insert sort on city_list\n" +
            "calc insert_sort (\n" +
            "// i is index to last item appended to the list\n" +
            "integer i = length(city_list) - 1,\n" +
            "// Skip first time when only one item in list\n" +
            ": i < 1,\n" +
            "// j is the swap index\n" + 
            "integer j = i - 1,\n" +
            "// Get last altitude for sort comparison\n" + 
            "integer altitude = city_list[i][altitude],\n" +
            "// Save axiom to swap\n" +
            "temp = city_list[i],\n" +
            "// Shuffle list until sort order restored\n" + 
            "{\n" +
            "  ? altitude < city_list[j][altitude],\n" +
            "  city_list[j + 1] = city_list[j],\n" +
            "  ? --j >= 0\n" +
            "},\n" +
            "// Insert saved axiom in correct position\n" +
            "city_list[j + 1] = temp);\n" +
	    "query high_cities (city : high_city) >> calc(insert_sort);\n"; 

	@Inject
	ProviderManager providerManager;

	public HighCitiesSorted()
	{
		// Configure dependency injection to get resource "cities"
		new DI(new CitiesModule()).validate();
		DI.inject(this);
		providerManager.putAxiomProvider(new CitiesAxiomProvider());
	}
	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * high_city(name = denver, altitude = 5280)<br/>
	 * high_city(name = flagstaff, altitude = 6970)<br/>
	 * high_city(name = addis ababa, altitude = 8000)<br/>
	 * high_city(name = leadville, altitude = 10200)<br/>
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
    public Iterator<AxiomTermList> displayHighCities() throws ParseException
	{
		QueryProgram queryProgram = compileScript(CITY_EVELATIONS);
		Result result = queryProgram.executeQuery("high_cities");
		return (Iterator<AxiomTermList>) result.getList("city_list").iterator();
	}

	protected QueryProgram compileScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
		return queryProgram;
	}

	public static void main(String[] args) throws SQLException
	{
		HighCitiesSorted highCities = new HighCitiesSorted();
		try 
		{
			Iterator<AxiomTermList> iterator = highCities.displayHighCities();
	        while(iterator.hasNext())
	            System.out.println(iterator.next().toString());
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
