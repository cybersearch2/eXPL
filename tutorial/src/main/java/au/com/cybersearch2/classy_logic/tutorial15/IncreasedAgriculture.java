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
package au.com.cybersearch2.classy_logic.tutorial15;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
 * IncreasedAgriculture
 * Solves:  
 * @author Andrew Bowley
 * 20 Feb 2015
 */
public class IncreasedAgriculture 
{
	static final String AGRICULTURAL_LAND = 
		"include \"agriculture-land.xpl\";\n" +
		"include \"surface-land.xpl\";\n" +
	    "template agri_10y (country ? Y2010 - Y1990 > 1.0, double Y1990, double Y2010);\n" +
		"template surface_area_increase (agri_10y.country, double surface_area = (agri_10y.Y2010 - agri_10y.Y1990)/100 * surface_area_Km2);\n" +
		"list<term> surface_area_axiom(surface_area_increase : resource \"agriculture\");\n" +
	    "query more_agriculture(Data : agri_10y, surface_area : surface_area_increase);"; 

	static final String AGRI_10_YEAR =
		"axiom surface_area_increase (country, surface_area) : resource \"agriculture\";\n" +
	    "template increased(country, surface_area);\n" +
		"list increased_list(increased);\n" +
		"query increased_query(surface_area_increase : increased);";
	
	@Inject
	ProviderManager providerManager;

	public IncreasedAgriculture()
	{
		// Configure dependency injection to get resource "cities"
		new DI(new AgriModule()).validate();
		DI.inject(this);
		providerManager.putAxiomProvider(new AgriAxiomProvider());
	}
	/**
	 * Compiles the AGRICULTURAL_LAND script and runs the "more_agriculture" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * @throws ParseException
	 */
	public void displayIncreasedAgri() throws ParseException
	{
		QueryProgram queryProgram1 = compileScript(AGRICULTURAL_LAND);
		queryProgram1.executeQuery("more_agriculture");
		QueryProgram queryProgram2 = compileScript(AGRI_10_YEAR);
		Result result = queryProgram2.executeQuery("increased_query");
		@SuppressWarnings("unchecked")
		Iterator<AxiomTermList> iterator = (Iterator<AxiomTermList>) result.getList("increased_list").iterator();
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
		IncreasedAgriculture increasedAgri = new IncreasedAgriculture();
		try 
		{
			increasedAgri.displayIncreasedAgri();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
