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
package au.com.cybersearch2.classy_logic.tutorial12;

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
 * MultiCurrency
 * Demonstrates use of currency type with world currencies.
 * The country is dynamically qualified each round of unification.
 * Like the SingleCurrency example, this performs a Goods and Services Tax calculation
 * and formats the resulting amount with currency code. The item price is represented
 * this time as a double literal with correct number of decimal places for the indicated for the currency.
 * The currency type applies the rounding recommended for financial transactions.
 * @author Andrew Bowley
 * 11 Mar 2015
 */
public class MultiCurrency 
{
/* multi-currency.xpl
include "world_currency.xpl";

calc total
(
  currency $ country amount,
  amount *= 1.1
);

calc format_total
(
  string total_text = total.country + " Total + gst: " + total.amount.format
);

query<axiom> price_query(price : total) -> (format_total);

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    /**
	 * Construct MultiCurrency object
	 */
	public MultiCurrency() 
	{
        File resourcePath = new File("src/main/resources");
        queryProgramParser = new QueryProgramParser(resourcePath);
	}

	/**
	 * Compiles the WORLD_CURRENCY script and runs the "price_query" query.<br/>
	 * The first 3 of 104 expected results:<br/>
	 MY Total + gst: MYR10,682.12<br/>
     QA Total + gst: QAR 545.81<br/>
     IS Total + gst: 510, ISK<br/>
	 * To view full expected result, see src/main/resource/multi-currency-list.txt
	 * @return Axiom iterator
	 */
	public Iterator<Axiom> getFormatedAmounts()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("tutorial12/multi-currency.xpl");
        parserContext = queryProgramParser.getContext();
		// Use this query to see the total amount before it is formatted
		// Note adjustment of decimal places to suite currency.
		Result result = queryProgram.executeQuery("price_query");
		return result.axiomIterator("price_query");
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
	        MultiCurrency multiCurrency = new MultiCurrency();
	        Iterator<Axiom> iterator = multiCurrency.getFormatedAmounts();
	        while(iterator.hasNext())
	        {
	            System.out.println(iterator.next().getTermByName("total_text").getValue().toString());
	        }
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
