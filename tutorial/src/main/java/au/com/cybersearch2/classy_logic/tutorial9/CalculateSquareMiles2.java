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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.TestAxiomProvider;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classyinject.DI;

/**
 * CalculateSquareMiles
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class CalculateSquareMiles2 
{
	static final String COUNTRY_SURFACE_AREA = 
			"include \"surface-land.xpl\";\n" +
	        "axiom locale(locale) : resource \"locale\";\n" +
			"boolean imperial = locale == \"US\";\n" +
			"template surface_area(country, double surface_area_Km2);\n" +
			"calc km2_to_mi2 (\n" +
			"country,\n" +
			"double surface_area = surface_area.surface_area_Km2,\n" +
			"? imperial\n" +
			"{\n" +
			"  surface_area *= 0.3861\n" +
			"}\n" +
			");\n" +
			"list surface_area_by_country(km2_to_mi2);\n" +
		    "query surface_area_mi2(surface_area : surface_area)\n" + 
		    "  >> calc(locale : km2_to_mi2);";

	public CalculateSquareMiles2()
	{
		new DI(new QueryParserModule()).validate();
	}
	
	public void displaySurfaceAreaOutsideUsa() throws ParseException
	{
		TestAxiomProvider.setlocaleCountry("AU");
		QueryProgram queryProgram = compileScript(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_mi2");
		@SuppressWarnings("unchecked")
		Iterator<AxiomTermList> iterator = (Iterator<AxiomTermList>) result.getList("surface_area_by_country").iterator();
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
	}

	public void displaySurfaceAreaInUsa() throws ParseException
	{
		TestAxiomProvider.setlocaleCountry("US");
		QueryProgram queryProgram = compileScript(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_mi2");
		@SuppressWarnings("unchecked")
		Iterator<AxiomTermList> iterator = (Iterator<AxiomTermList>) result.getList("surface_area_by_country").iterator();
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
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
		CalculateSquareMiles2 calculateSquareMiles = new CalculateSquareMiles2();
		String country = "AU"; // Default locale Australia
		if (args.length > 0)
			country = args[0];
		System.out.println("Display country surface areas for locale " + country);
		try 
		{
			if ("US".equals(country))
				calculateSquareMiles.displaySurfaceAreaInUsa();
			else
				calculateSquareMiles.displaySurfaceAreaOutsideUsa();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
