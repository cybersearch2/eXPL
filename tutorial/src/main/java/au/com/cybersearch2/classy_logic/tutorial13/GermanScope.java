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

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * GermanScope
 * Demonstrates handling an amount espressed in Euros within a German scope and 
 * translating a term using a local axiom. 
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class GermanScope 
{
/* german-scope.xpl
axiom item (amount) : parameter;

axiom german.lexicon 
  ( Total)
  {"Gesamtkosten"};
  
template charge(currency amount);

calc charge_plus_gst(currency total = charge.amount * 1.1);

calc format_total(string total_text = translate^Total + " + gst: " + format(charge_plus_gst.total));

local translate(lexicon);

scope german (language="de", region="DE")
{
  query<term> item_query(item : charge) >> (charge_plus_gst) >> (format_total);
}

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public GermanScope()
    {
        File resourcePath = new File("src/main/resources/tutorial13");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }


	/**
	 * Compiles the GERMAN_SCOPE script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public Axiom getFormatedTotalAmount()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("german-scope.xpl");
        parserContext = queryProgramParser.getContext();
		// Create QueryParams object for scope "german" and query "item_query"
		QueryParams queryParams = queryProgram.getQueryParams("german", "item_query");
		// Add an item Axiom with a single "2.345,67 EUR" term
		// This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("item", new Axiom("item", new Parameter("amount", "12.345,67 €")));
        Result result = queryProgram.executeQuery(queryParams);
        return result.getAxiom("german", "item_query");
	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * The expected result:<br/>
     * format_total(total_text = Gesamtkosten + gst: 13.580,24 EUR)<br/>
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        GermanScope germanScope = new GermanScope();
            System.out.println(germanScope.getFormatedTotalAmount().toString());
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
