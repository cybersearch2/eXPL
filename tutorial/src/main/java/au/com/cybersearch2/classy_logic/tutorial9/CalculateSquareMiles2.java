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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;
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

	protected String localeCountry;
	
	@Inject
	ProviderManager providerManager;

	public CalculateSquareMiles2()
	{
		new DI(new QueryParserModule()).validate();
		DI.inject(this);
		AxiomProvider axiomProvider = new AxiomProvider(){

			@Override
			public String getName() {
				return "locale";
			}

			@Override
			public void setResourceProperties(String axiomName,
					Map<String, Object> properties) {
			}

			@Override
			public AxiomSource getAxiomSource(String axiomName,
					List<String> axiomTermNameList) {
				AxiomSource axiomSource = null;
				Axiom localeAxiom = new Axiom("locale", new Parameter("locale", localeCountry));
				axiomSource = new SingleAxiomSource(localeAxiom);
				return axiomSource;
			}

			@Override
			public AxiomListener getAxiomListener() {
				return new AxiomListener(){

					@Override
					public void onNextAxiom(Axiom axiom) {
					}};
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
		providerManager.putAxiomProvider(axiomProvider );
	}
	
	public void displaySurfaceAreaOutsideUsa() throws ParseException
	{
		localeCountry = "AU";
		QueryProgram queryProgram = compileScript(COUNTRY_SURFACE_AREA);
		Result result = queryProgram.executeQuery("surface_area_mi2");
		@SuppressWarnings("unchecked")
		Iterator<AxiomTermList> iterator = (Iterator<AxiomTermList>) result.getList("surface_area_by_country").iterator();
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
	}

	public void displaySurfaceAreaInUsa() throws ParseException
	{
		localeCountry = "US";
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
