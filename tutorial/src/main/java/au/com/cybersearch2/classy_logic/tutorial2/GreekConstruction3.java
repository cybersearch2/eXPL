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

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * GreekConstruction3
 * Shows query with 2 logic chains. Uses -> operator.
 * @author Andrew Bowley
 * 22 Feb 2015
 */
public class GreekConstruction3 
{

/* greek-construction3.xpl
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
template account(name ? name == customer.name, city = customer.city, fee);
template delivery(name = account.name, city ? city == account.city, freight);
        
query<axiom> greek_business(customer:customer) 
  -> (fee:account) -> (freight:delivery);
  
*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public GreekConstruction3()
    {
        File resourcePath = new File("src/main/resources/tutorial2");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
    /**
	 * Compiles the greek-construction3.xpl script and runs the "customer_charge" query, displaying the solution on the console.
	 * The expected result:<br/>
        delivery(name=Marathon Marble, city=Sparta, freight=16)
        delivery(name=Acropolis Construction, city=Athens, freight=5)
        delivery(name=Agora Imports, city=Sparta, freight=16)
        delivery(name=Spiros Theodolites, city=Milos, freight=22)
	 */
	public Iterator<Axiom> displayCustomerCharges()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("greek-construction3.xpl");
        parserContext = queryProgramParser.getContext();
 		Result result = queryProgram.executeQuery("greek_business");
 		return result.axiomIterator("greek_business");
	}

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		try 
		{
	        GreekConstruction3 greekConstruction = new GreekConstruction3();
	        Iterator<Axiom> iterator = greekConstruction.displayCustomerCharges();
	        while (iterator.hasNext())
	            System.out.println(iterator.next().toString());
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
