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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * GermanScope
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class GermanScope 
{
	static final String GERMAN_SCOPE =
			"axiom item (amount) : parameter;\n" +
			"axiom lexicon (language, Total):\n" +
	        "  (\"english\", \"Total\"),\n" +
	        "  (\"german\", \"Gesamtkosten\");\n" +
	        "local translate(lexicon);" +
			"template charge(currency amount);\n" +
			"calc charge_plus_gst(currency total = amount * 1.1);\n" +
			"calc format_total(string total_text = translate[Total] + \" + gst: \" + format(total));\n" +
			"scope german (language=\"de\", region=\"DE\")\n" +
			"{\n" +
			"  query item_query(item : charge) >> calc(charge_plus_gst) >> calc(format_total);\n" +
	        "}";

	/**
	 * Compiles the GERMAN_SCOPE script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * Demonstrates passing an axiom to the query using a QueryParams object;
	 * The expected result:<br/>
	 * format_total(total_text = Gesamtkosten + gst: 13.580,24 EUR)<br/>
	 * @throws ParseException
	 */
	public void displayTotalAmount() throws ParseException
	{
		QueryProgram queryProgram = compileScript(GERMAN_SCOPE);
		// Create QueryParams object for scope "german" and query "item_query"
		QueryParams queryParams = new QueryParams(queryProgram, "german","item_query");
		// Add an item Axiom with a single "2.345,67 EUR" term
		// This axiom goes into the Global scope and is removed at the start of the next query.
		queryParams.addAxiom("item", "12.345,67 €");
		// Add a solution handler to display the final Calculator solution
		queryParams.setSolutionHandler(new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("format_total").toString());
				return true;
			}});
		queryProgram.executeQuery(queryParams);
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
		GermanScope germanScope = new GermanScope();
		try 
		{
			germanScope.displayTotalAmount();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
