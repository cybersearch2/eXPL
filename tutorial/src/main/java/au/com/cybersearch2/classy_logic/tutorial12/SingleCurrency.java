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

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

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
	static final String CURRENCY =
		"axiom item: (\"$1234.56\");\n" +
		"template charge(currency(\"AU\") amount);\n" +
	    "calc charge_plus_gst(\n" +
	    "  currency(\"AU\") total = amount * 1.1,\n" +
	    "  string total_text = \"Total + gst: \" + format(total));\n" +
		"query item_query(item : charge) >> (charge_plus_gst);";

	/**
	 * Compiles the CURRENCY script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * format_total(total_text = Total + gst: AUD1,358.02)<br/>
	 */
	public String getFormatedTotalAmount()
	{
		QueryProgram queryProgram = new QueryProgram(CURRENCY);
		final String[] formatedTotalAmountHolder = new String[1];
		queryProgram.executeQuery("item_query", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
			    formatedTotalAmountHolder[0] = solution.getString("charge_plus_gst", "total_text");
				return true;
			}});
		return formatedTotalAmountHolder[0];
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
