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
public class GreekConstruction 
{

/*
axiom charge() 
  {"Athens", 23 }
  {"Sparta", 13 }
  {"Milos", 17};
        
axiom customer()
  {"Marathon Marble", "Sparta"}
  {"Acropolis Construction", "Athens"}
  {"Agora Imports", "Sparta"}
  {"Spiros Theodolites", "Milos"};
*/
/* customer_charge.xpl
axiom charge() : "greek_construction";
axiom customer() : "greek_construction"; 
template freight(city, charge);
template customer_freight(name, city ? city == freight.city, charge);
        
query customer_charge(charge:freight, customer:customer_freight);
*/
    protected QueryProgramParser queryProgramParser;
    
    public GreekConstruction()
    {
        ResourceAxiomProvider resourceAxiomProvider = new ResourceAxiomProvider("greek_construction", "greek_construction.xpl", 2);
        queryProgramParser = new QueryProgramParser(resourceAxiomProvider);
     }

    /**
     * Compiles the "customer_charge.xpl" script and runs the "customer_charge" query, displaying the solution on the console.
     */
    public void findCustomerCharges(SolutionHandler solutionHandler) 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("customer_charge.xpl");
        // The first unification fills in variables "city" and "charge".
        // Both templates here share variables "city" and "charge", so only the "name" term
        // empty in the second unification.
        queryProgram.executeQuery("customer_charge", solutionHandler);
    }


	/**
	 * The query has 2 unification steps. The first unifies "charge" axiom with "freight" template.<br/>
	 * The second unifies "customer" axiom with "customer_freight" template.
	 * The expected "customer_freight" result:<br/>
freight(city = Athens, charge = 23)<br/>
customer_freight(name = Acropolis Construction, city = Athens, charge = 23)<br/>
freight(city = Sparta, charge = 13)<br/>
customer_freight(name = Marathon Marble, city = Sparta, charge = 13)<br/>
freight(city = Sparta, charge = 13)<br/>
customer_freight(name = Agora Imports, city = Sparta, charge = 13)<br/>
freight(city = Milos, charge = 17)<br/>
customer_freight(name = Spiros Theodolites, city = Milos, charge = 17)<br/>
	 */
    public static void main(String[] args)
    {
        try 
        {
            GreekConstruction greekConstruction = new GreekConstruction();
            greekConstruction.findCustomerCharges(new SolutionHandler(){
                @Override
                public boolean onSolution(Solution solution) {
                    System.out.println(solution.getAxiom("freight").toString());
                    System.out.println(solution.getAxiom("customer_freight").toString());
                    return true;
                }});
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
