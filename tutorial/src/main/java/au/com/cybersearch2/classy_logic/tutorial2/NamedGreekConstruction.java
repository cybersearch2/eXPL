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
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.ResourceAxiomProvider;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * GreekConstruction
 * @author Andrew Bowley
 * 22 Feb 2015
 */
public class NamedGreekConstruction implements SolutionHandler
{

/* customer_charge2.xpl
axiom charge() : "greek_construction";
axiom customer() : "greek_construction"; 

template freight(charge, city);
template customer_freight(name, city ? city == freight.city, charge = freight.charge);
        
query customer_charge(charge:freight, customer:customer_freight);

*/
    
    protected QueryProgramParser queryProgramParser;


    public NamedGreekConstruction()
    {
        ResourceAxiomProvider resourceAxiomProvider = new ResourceAxiomProvider("greek_construction", "named_greek_construction.xpl", 2);
        queryProgramParser = new QueryProgramParser(resourceAxiomProvider);
     }

	/**
	 * Compiles the GREEK_CONSTRUCTION script and runs the "customer_charge" query, displaying the solution on the console.<br/>
	 * The query has 2 unification steps. The first unifies "charge" axiom with "freight" template.<br/>
	 * The second unifies "customer" axiom with "customer_freight" template.
	 * Unlike the first version, this sample declares the names of the axiom terms so they can be matched to template terms by name.
	 * The customer_freight template also explicitly assigns a value to it's "charge" term, rather than relying on unification to do the trick.
	 * The expected "customer_freight" result:<br/>
	 * customer_freight(name = Acropolis Construction, city = Athens, charge = 23)<br/>
	 * customer_freight(name = Marathon Marble, city = Sparta, charge = 13)<br/>
	 * customer_freight(name = Agora Imports, city = Sparta, charge = 13)<br/>
	 * customer_freight(name = Spiros Theodolites, city = Milos, charge = 17)<br/>
	 */
	public void displayCustomerCharges(SolutionHandler solutionHandler)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("customer_charge2.xpl");
		// The first unification fills in variables "city" and "charge".
		// Both templates here share variables "city" and "charge", so only the "name" term
		// empty in the second unification.
		queryProgram.executeQuery("customer_charge", solutionHandler);
	}

    /**
     * onSolution - Print solution of both templates
     * @see au.com.cybersearch2.classy_logic.interfaces.SolutionHandler#onSolution(au.com.cybersearch2.classy_logic.query.Solution)
     */
    @Override
    public boolean onSolution(Solution solution) 
    {
        System.out.println(solution.getAxiom("freight").toString());
        System.out.println(solution.getAxiom("customer_freight").toString());
        return true;
    }

	public static void main(String[] args)
	{
		try 
		{
		    NamedGreekConstruction greekConstruction = new NamedGreekConstruction();
			greekConstruction.displayCustomerCharges(greekConstruction);
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
