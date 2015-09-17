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
package au.com.cybersearch2.classy_logic.tutorial2;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * GreekConstruction
 * @author Andrew Bowley
 * 22 Feb 2015
 */
public class GreekConstruction 
{

	static final String GREEK_CONSTRUCTION =
			
		"axiom charge() \n" +
		"  {\"Athens\", 23 }\n" +
		"  {\"Sparta\", 13 }\n" +
		"  {\"Milos\", 17};\n" +
		
		"axiom customer()\n" +
		"  {\"Marathon Marble\", \"Sparta\"}\n" +
		"  {\"Acropolis Construction\", \"Athens\"}\n" +
		"  {\"Agora Imports\", \"Sparta\"}\n" +
		"  {\"Spiros Theodolites\", \"Milos\"};\n" +
		
		"template freight(city,  charge);\n" +
		"template customer_freight(name, city ? city == freight.city, charge);\n" +
		
	    "query customer_charge(charge:freight, customer:customer_freight);";


	/**
	 * Compiles the GREEK_CONSTRUCTION script and runs the "customer_charge" query, displaying the solution on the console.<br/>
	 * The query has 2 unification steps. The first unifies "charge" axiom with "freight" template.<br/>
	 * The second unifies "customer" axiom with "customer_freight" template.
	 * The expected "customer_freight" result:<br/>
	 * customer_freight(name = Acropolis Construction, city = Athens, charge = 23)<br/>
	 * customer_freight(name = Marathon Marble, city = Sparta, charge = 13)<br/>
	 * customer_freight(name = Agora Imports, city = Sparta, charge = 13)<br/>
	 * customer_freight(name = Spiros Theodolites, city = Milos, charge = 17)<br/>
	 */
	public void displayCustomerCharges()
	{
		QueryProgram queryProgram = new QueryProgram(GREEK_CONSTRUCTION);
		// The first unification fills in variables "city" and "charge".
		// Both templates here share variables "city" and "charge", so only the "name" term
		// empty in the second unification.
		queryProgram.executeQuery("customer_charge", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("freight").toString());
				System.out.println(solution.getAxiom("customer_freight").toString());
				return true;
			}});
	}

	public static void main(String[] args)
	{
		try 
		{
	        GreekConstruction greekConstruction = new GreekConstruction();
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
