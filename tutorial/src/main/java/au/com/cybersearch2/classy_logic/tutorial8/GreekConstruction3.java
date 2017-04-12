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

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
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

/* greek-construction.xpl
axiom customer()
  {"Marathon Marble", "Sparta"}
  {"Acropolis Construction", "Athens"}
  {"Agora Imports", "Sparta"}
  {"Spiros Theodolites", "Milos"};
        
axiom fee (name, fee)
  {"Marathon Marble", 61}
  {"Acropolis Construction", 47}
  {"Agora Imports", 49}
  {"Spiros Theodolites", 57}; 
        
axiom freight (city, freight) 
  {"Athens", 5 }
  {"Sparta", 16 }
  {"Milos", 22};
        
template customer(name, city);
template account(name ? name == customer.name, fee);
template delivery(city ? city == customer.city, freight);
        
query greek_business(customer:customer) 
  >> (fee:account) >> (freight:delivery);
  
*/

    protected QueryProgramParser queryProgramParser;

    public GreekConstruction3()
    {
        File resourcePath = new File("src/main/resources/tutorial8");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
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
	public ParserContext displayCustomerCharges(SolutionHandler solutionHandler)
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("greek-construction.xpl");
 		queryProgram.executeQuery("greek_business", solutionHandler);
        return queryProgramParser.getContext();
	}

	public static void main(String[] args)
	{
		try 
		{
	        GreekConstruction3 greekConstruction = new GreekConstruction3();
			greekConstruction.displayCustomerCharges(new SolutionHandler(){
	            @Override
	            public boolean onSolution(Solution solution) {
	                System.out.println(solution.getAxiom("account").toString());
	                System.out.println(solution.getAxiom("delivery").toString());
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
