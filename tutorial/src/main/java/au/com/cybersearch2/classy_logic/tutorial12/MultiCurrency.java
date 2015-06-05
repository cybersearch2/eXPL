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

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classyinject.DI;

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
	static final String WORLD_CURRENCY =
			"include \"world_currency.xpl\";\n" +
			"template charge(currency(country) amount);\n" +
	        "calc charge_plus_gst(currency(country) total = amount * 1.1);\n" +
	        "calc format_total(string total_text = country + \" Total + gst: \" + format(total));\n" +
	        "list world_list(format_total);\n" +
			"query price_query(price : charge) >> calc(charge_plus_gst) >> calc(format_total);";

	/**
	 * Construct MultiCurrency object
	 */
	public MultiCurrency() 
	{
		new DI(new QueryParserModule()).validate();
	}

	/**
	 * Compiles the WORLD_CURRENCY script and runs the "price_query" query, displaying the solution on the console.<br/>
	 * The first of 104 expected results:<br/>
	 * format_total(total_text = MY Total + gst: MYR10,682.12)<br/><br/>
	 * To view full expected result, see src/main/resource/multi-currency-list.txt
	 * @return AxiomTermList iterator
	 */
    @SuppressWarnings("unchecked")
	public Iterator<AxiomTermList> getFormatedAmounts()
	{
		QueryProgram queryProgram = new QueryProgram(WORLD_CURRENCY);
		// Use this query to see the total amount before it is formatted
		// Note adjustment of decimal places to suite currency.
		//queryProgram.executeQuery("price_query", new SolutionHandler(){
		//	@Override
		//	public boolean onSolution(Solution solution) {
		//		System.out.println(solution.getAxiom("format_total").toString());
		//		return true;
		//	}});
		Result result = queryProgram.executeQuery("price_query");
		return (Iterator<AxiomTermList>) result.getList("world_list").iterator();
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
	        Iterator<AxiomTermList> iterator = multiCurrency.getFormatedAmounts();
	        while(iterator.hasNext())
	        {
	            System.out.println(iterator.next().toString());
	        }
		} 
		catch (ExpressionException e) 
		{ // Display nested ParseException
			e.getCause().printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
