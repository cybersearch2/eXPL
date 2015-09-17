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
package au.com.cybersearch2.classy_logic.tutorial8;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * GreekConstruction2
 * @author Andrew Bowley
 * 22 Feb 2015
 */
public class GreekConstruction3 
{

	static final String GREEK_CONSTRUCTION =
			
		"axiom customer()\n" +
		"  {\"Marathon Marble\", \"Sparta\"}\n" +
		"  {\"Acropolis Construction\", \"Athens\"}\n" +
		"  {\"Agora Imports\", \"Sparta\"}\n" +
		"  {\"Spiros Theodolites\", \"Milos\"};\n" +
		
		"axiom fee (name, fee)\n" +
		"  {\"Marathon Marble\", 61}\n" +
		"  {\"Acropolis Construction\", 47}\n" +
		"  {\"Agora Imports\", 49}\n" +
		"  {\"Spiros Theodolites\", 57};\n" + 
		
		"axiom freight (city, freight) \n" +
		"  {\"Athens\", 5 }\n" +
		"  {\"Sparta\", 16 }\n" +
		"  {\"Milos\", 22};\n" +
		
		"template customer(name, city);\n" +
		"template account(name ? name == customer.name, fee);\n" +
		"template delivery(city ? city == customer.city, freight);\n" +
		
	    "  query greek_business(customer:customer)\n" + 
		"  >> (fee:account) >> (freight:delivery);";


	/**
	 * Compiles the GREEK_CONSTRUCTION script and runs the "customer_charge" query, displaying the solution on the console.<br/>
	 * The query has 2 unification steps. The first unifies "charge" axiom with "freight" template.<br/>
	 * The second unifies "customer" axiom with "customer_freight" template.
	 * The expected result:<br/>
		account(name = Marathon Marble, fee = 61)<br/>
		delivery(city = Sparta, freight = 16)<br/>
		account(name = Acropolis Construction, fee = 47)<br/>
		delivery(city = Athens, freight = 5)<br/>
		account(name = Agora Imports, fee = 49)<br/>
		delivery(city = Sparta, freight = 16)<br/>
		account(name = Spiros Theodolites, fee = 57)<br/>
		delivery(city = Milos, freight = 22)<br/>	 
	 */
	public void displayCustomerCharges()
	{
		QueryProgram queryProgram = new QueryProgram(GREEK_CONSTRUCTION);
		queryProgram.executeQuery("greek_business", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("account").toString());
				System.out.println(solution.getAxiom("delivery").toString());
				return true;
			}});
	}

	public static void main(String[] args)
	{
		try 
		{
	        GreekConstruction3 greekConstruction = new GreekConstruction3();
			greekConstruction.displayCustomerCharges();
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
