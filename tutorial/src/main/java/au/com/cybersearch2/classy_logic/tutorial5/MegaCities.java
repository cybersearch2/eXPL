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
package au.com.cybersearch2.classy_logic.tutorial5;

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
public class MegaCities 
{
	static final String MEGA_CITY = 
			"// axiom mega_city (Rank,Megacity,Country,Continent,Population):\n" + 
			"// 	(1,\"Tokyo\",\"Japan\",\"Asia\",37900000),\n" + 
			"// 	(2,\"Delhi\",\"India\",\"Asia\",26580000)...\n" + 
			"include \"mega_city.xpl\";\n" +
			"integer count = 0;\n" +
			"template asia_top_ten (Megacity ? Continent == \"Asia\" && count++ < 10, Country, Population);\n" + 
            "query asia_top_ten (mega_city : asia_top_ten);\n"; 

	public MegaCities()
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
		asia_top_ten(Megacity = Tokyo, Country = Japan, Population = 37900000)<br/>
		asia_top_ten(Megacity = Delhi, Country = India, Population = 26580000)<br/>
		asia_top_ten(Megacity = Seoul, Country = South,Korea, Population = 26100000)<br/>
		asia_top_ten(Megacity = Shanghai, Country = China, Population = 25400000)<br/>
		asia_top_ten(Megacity = Mumbai, Country = India, Population = 23920000)<br/>
		asia_top_ten(Megacity = Beijing, Country = China, Population = 21650000)<br/>
		asia_top_ten(Megacity = Jakarta, Country = Indonesia, Population = 20500000)<br/>
		asia_top_ten(Megacity = Karachi, Country = Pakistan, Population = 20290000)<br/>
		asia_top_ten(Megacity = Osaka, Country = Japan, Population = 20260000)<br/>
		asia_top_ten(Megacity = Manila, Country = Philippines, Population = 20040000)<br/>
	 */
	public void displayTopTenAsianCities()
	{
		QueryProgram queryProgram = new QueryProgram(MEGA_CITY);
		queryProgram.executeQuery("asia_top_ten", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				Axiom evaluateAxiom = solution.getAxiom("asia_top_ten");
					System.out.println(evaluateAxiom.toString());
				return true;
			}});
		/* Uncomment to run query a second time to check the count variable 
		 * is reset back to initial value of 0
		queryProgram.executeQuery("asia_top_ten", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				Axiom evaluateAxiom = solution.getAxiom("asia_top_ten");
					System.out.println(evaluateAxiom.toString());
				return true;
			}});
			*/
	}
	
	public static void main(String[] args)
	{
		try 
		{
	        MegaCities megaCities = new MegaCities();
			megaCities.displayTopTenAsianCities();
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
