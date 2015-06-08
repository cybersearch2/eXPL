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

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classyinject.DI;

/**
 * CalculateSquareMiles
 * Demonstrates using a  a conditional sequence in a calculator to adjust surface area from km2 to square miles. 
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class CalculateSquareMiles2 
{
    // The surface-land.xpl file has country and surface_area terms eg.  ("United States",{9,831,510.00})
    // Note the braces indicate the surface area is formatted, so must be parsed to get numeric value
	static final String COUNTRY_SURFACE_AREA = 
			"include \"surface-land.xpl\";\n" +
	        "axiom env(country_code) : resource \"environment\";\n" +
			"// Set imperial flag true if country is USA\n" +
			"boolean imperial = country_code == \"US\";\n" +
			"template surface_area(country, double surface_area_Km2);\n" +
			"calc filter_area (\n" +
			"country,\n" +
			"double surface_area = surface_area.surface_area_Km2,\n" +
			"? imperial\n" +
			"{\n" +
			"  surface_area *= 0.3861\n" +
			"}\n" +
			");\n" +
			"list surface_area_by_country(filter_area);\n" +
		    "query surface_area_mi2(surface_area : surface_area)\n" + 
		    "  >> calc(env : filter_area);";

    /** Environment axiom provider for USA */
	protected EnvironmentAxiomProvider usAxiomProvider;
	/** Environment axiom provider for Australia */
    protected EnvironmentAxiomProvider auAxiomProvider;
	
	@Inject
	ProviderManager providerManager;

	public CalculateSquareMiles2()
	{
		new DI(new QueryParserModule()).validate();
		DI.inject(this);
		usAxiomProvider = new EnvironmentAxiomProvider("US");
        auAxiomProvider = new EnvironmentAxiomProvider("AU");
	}
	
    /**
     * Display country surface areas in locale AU
     * The first 3 results:</br>
        filter_area(country = Afghanistan, surface_area = 652230)</br>
        filter_area(country = Albania, surface_area = 28750)</br>
        filter_area(country = Algeria, surface_area = 2381740)</br>
     */
	public void displaySurfaceAreaOutsideUsa()
	{
        providerManager.putAxiomProvider(auAxiomProvider);
		QueryProgram queryProgram = new QueryProgram(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_mi2");
		Iterator<AxiomTermList> iterator = result.getIterator("surface_area_by_country");
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
	}

	/**
	 * Display country surface areas in locale US
	 * The first 3 results:</br>
	    filter_area(country = Afghanistan, surface_area = 251826.003)</br>
        filter_area(country = Albania, surface_area = 11100.375)</br>
        filter_area(country = Algeria, surface_area = 919589.814)</br>
	 */
	public void displaySurfaceAreaInUsa()
	{
        providerManager.putAxiomProvider(usAxiomProvider);
		QueryProgram queryProgram = new QueryProgram(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_mi2");
		Iterator<AxiomTermList> iterator = result.getIterator("surface_area_by_country");
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
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
