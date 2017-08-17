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
public class SingleCurrency 
{
/* single-currency.xpl
template charge_plus_gst
(
  currency total = amount * 1.1,
  string total_text = "Total + gst: " + total.format
);

template french_charge_plus_gst
(
  currency total = french.item->amount * 1.33,
  string total_text = "le total + gst: " + total.format
);

scope french (language="fr", region="FR")
{
  axiom item( amount ) { currency ("500,00 €") };
  
  template charge_plus_gst
  (
    currency total = amount * 1.33,
    string total_text = "le total + gst: " + total.format
  );
}

scope german (language="de", region="DE")
{
  axiom item( amount ) { currency ("12.345,67 €") };
  query<term> french_item_query(item : french.charge_plus_gst);
}

query<term> item_query(german.item : german.charge_plus_gst);
query<term> french_item_query(french_charge_plus_gst);

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public SingleCurrency()
    {
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/tutorial13"));
    }
    
	/**
	 * Compiles the CURRENCY script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * Total + gst: 13.580,24 EUR<br/>
	 */
	public String getFormatedTotalAmount()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("single-currency.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("item_query");
	    return result.getAxiom("item_query").getTermByName("total_text").getValue().toString();
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
	        SingleCurrency singleCurrency = new SingleCurrency();
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
