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
package au.com.cybersearch2.classy_logic.tutorial3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classyinject.DI;

/**
 * EuropeanMegaCities
 * @author Andrew Bowley
 * 24 Feb 2015
 */
public class EuropeanMegaCities 
{
	static final String MEGA_CITY = 
			// mega_city.xpl is in project folder /src/test/resources
			"// axiom mega_city (Rank,Megacity,Country,Continent,Population):\n" + 
			"// 	(1,\"Tokyo\",\"Japan\",\"Asia\",37900000),\n" + 
			"// 	(2,\"Delhi\",\"India\",\"Asia\",26580000)...\n" + 
			"include \"mega_city.xpl\";\n" +
			"integer count = 0;\n" +
			"template euro_megacities (Megacity, Country, Population, Continent = \"Europe\" );\n" + 
            "query euro_megacities (mega_city : euro_megacities);\n"; 

	public EuropeanMegaCities()
	{   // Set up dependency injection so file mega_city.xpl can be located in project folder src/test/resources
        new DI(new QueryParserModule()).validate();
	}

	/**
	 * Compiles the MEGA_CITY script and runs the "asia_top_ten" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
		euro_megacities(Megacity = Moscow, Country = Russia, Population = 16900000, Continent = Europe)<br/>
		euro_megacities(Megacity = London, Country = United,Kingdom, Population = 15800000, Continent = Europe)<br/>
		euro_megacities(Megacity = Istanbul, Country = Turkey, Population = 14800000, Continent = Europe)<br/>
		euro_megacities(Megacity = Rhine-Ruhr, Country = Germany, Population = 11350000, Continent = Europe)<br/>
		euro_megacities(Megacity = Paris, Country = France, Population = 10770000, Continent = Europe)<br/>
	 * @throws ParseException
	 */
	public void displayEuropeanCities() throws ParseException
	{
		QueryProgram queryProgram = compileScript(MEGA_CITY);
		queryProgram.executeQuery("euro_megacities", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				Axiom evaluateAxiom = solution.getAxiom("euro_megacities");
					System.out.println(evaluateAxiom.toString());
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
		EuropeanMegaCities europeanMegaCities = new EuropeanMegaCities();
		try 
		{
			europeanMegaCities.displayEuropeanCities();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
