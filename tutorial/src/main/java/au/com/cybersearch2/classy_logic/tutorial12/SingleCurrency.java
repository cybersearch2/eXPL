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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
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
	    "calc charge_plus_gst(currency(\"AU\") total = amount * 1.1);\n" +
	    "calc format_total(string total_text = \"Total + gst: \" + format(total));\n" +
		"query item_query(item : charge) >> calc(charge_plus_gst) >> calc(format_total);";

	/**
	 * Compiles the CURRENCY script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * format_total(total_text = Total + gst: AUD1,358.02)<br/>
	 * @throws ParseException
	 */
	public void displayTotalAmount() throws ParseException
	{
		QueryProgram queryProgram = compileScript(CURRENCY);
		queryProgram.executeQuery("item_query", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("format_total").toString());
				return true;
			}});
	}
	
	protected QueryProgram compileScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
		return queryProgram;
	}
	
	public static void main(String[] args)
	{
		SingleCurrency singleCurrency = new SingleCurrency();
		try 
		{
			singleCurrency.displayTotalAmount();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
