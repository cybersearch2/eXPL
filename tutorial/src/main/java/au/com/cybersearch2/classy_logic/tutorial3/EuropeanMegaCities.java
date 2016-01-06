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

import au.com.cybersearch2.classy_logic.DaggerTestComponent;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.TestComponent;
import au.com.cybersearch2.classy_logic.TestModule;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
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
			"template euro_megacities (Megacity, Country, Continent { \"Europe\" } );\n" + 
            "query euro_megacities (mega_city : euro_megacities);\n"; 

	public EuropeanMegaCities()
	{   // Set up dependency injection so file mega_city.xpl can be located in project folder src/test/resources
        TestComponent component = 
                DaggerTestComponent.builder()
                .testModule(new TestModule())
                .build();
        DI.getInstance(component);
	}

	/**
	 * Compiles the MEGA_CITY script and runs the "asia_top_ten" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
		euro_megacities(Megacity = Moscow, Country = Russia, Continent = Europe)<br/>
		euro_megacities(Megacity = London, Country = UK, Continent = Europe)<br/>
		euro_megacities(Megacity = Istanbul, Country = Turkey, Continent = Europe)<br/>
		euro_megacities(Megacity = Rhine-Ruhr, Country = Germany, Continent = Europe)<br/>
		euro_megacities(Megacity = Paris, Country = France, Continent = Europe)<br/>
	 */
	public void displayEuropeanCities()
	{
		QueryProgram queryProgram = new QueryProgram(MEGA_CITY);
		queryProgram.executeQuery("euro_megacities", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				Axiom evaluateAxiom = solution.getAxiom("euro_megacities");
					System.out.println(evaluateAxiom.toString());
				return true;
			}});
	}
	
	public static void main(String[] args)
	{
		try 
		{
	        EuropeanMegaCities europeanMegaCities = new EuropeanMegaCities();
			europeanMegaCities.displayEuropeanCities();
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
