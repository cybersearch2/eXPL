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
package au.com.cybersearch2.classy_logic.tutorial13;

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * SingleCurrency
 * Demonstrates use of currency type to perform a Goods and Services Tax calculation
 * and format the resulting amount with currency code. The item price is represented
 * as a string literal showing that currency type automatically performs conversion of text to
 * decimal.
 * @author Andrew Bowley
 * 11 Mar 2015
 */
public class SingleCurrency2 
{
/* single-currency.xpl
axiom item() {"$1234.56"};

template charge(currency $ "AU" amount);

calc charge_plus_gst
(
  currency $ "AU" total = amount * 1.1,
  string total_text = "Total + gst: " + total.format
);

query<term> item_query(item : charge) -> (charge_plus_gst);

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public SingleCurrency2()
    {
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/tutorial13"));
    }
    
	/**
	 * Compiles the CURRENCY script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * Total + gst: AUD1,358.02<br/>
	 */
	public String getFormatedTotalAmount()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("single-currency.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("german", "french_item_query");
	    return result.getAxiom("german", "french_item_query").getTermByName("total_text").getValue().toString();
	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        SingleCurrency2 singleCurrency = new SingleCurrency2();
            System.out.println(singleCurrency.getFormatedTotalAmount());

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
