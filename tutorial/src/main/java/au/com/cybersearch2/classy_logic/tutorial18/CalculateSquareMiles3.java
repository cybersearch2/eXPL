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
package au.com.cybersearch2.classy_logic.tutorial18;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * CalculateSquareMiles
 * Demonstrates using a  a conditional sequence in a calculator to adjust surface area from km2 to square miles. 
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class CalculateSquareMiles3 
{
    // The surface-land.xpl file has country and surface_area terms eg.  ("United States",{9,831,510.00})
    // Note the braces indicate the surface area is formatted, so must be parsed to get numeric value
	static final String COUNTRY_SURFACE_AREA = 
			"include \"surface-land.xpl\";\n" +
		    "scope global (location = \"United States\"){}\n" +
            "scope australia (location = \"Australia\"){}\n" +
			"calc country_area (\n" +
            "  country { \"United States\" , \"Australia\" },\n" +
			"  double surface_area = surface_area_Km2,\n" +
			"  string units = \"km2\",\n" +
            "  // Use imperial measurements if country is USA\n" +
			"  ? scope[location] == \"United States\"\n" +
			"  {\n" +
			"    surface_area *= 0.3861,\n" +
			"    units = \"mi2\"\n" +
			"  }\n" +
			");\n" +
            "list surface_area_by_country(country_area);\n" +
            "query au_surface_area_query(surface_area : australia.country_area);" +
		    "query surface_area_query(surface_area : country_area);";

	public CalculateSquareMiles3()
	{
	}
	
	/**
	 * Display country surface areas
	 * The expected results:</br>
       country_area(country = Australia, surface_area = 2988885.042, units = mi2, true)</br>
       country_area(country = United States, surface_area = 3795946.011, units = mi2, true)</br>	
       country_area(country = Australia, surface_area = 7741220, units = km2, false)</br>
       country_area(country = United States, surface_area = 9831510, units = km2, false)</br>     
     */
	public void displaySurfaceArea()
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.setResourceBase(new File("src/main/resources"));

		queryProgram.parseScript(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_query");
		Iterator<Axiom> iterator = result.getIterator(QualifiedName.parseGlobalName("surface_area_by_country"));
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
        result = queryProgram.executeQuery("au_surface_area_query");
        iterator = result.getIterator(QualifiedName.parseGlobalName("surface_area_by_country"));
        while(iterator.hasNext())
            System.out.println(iterator.next().toString());
	}

	public static void main(String[] args)
	{
		CalculateSquareMiles3 calculateSquareMiles = new CalculateSquareMiles3();
		try 
		{
				calculateSquareMiles.displaySurfaceArea();
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
