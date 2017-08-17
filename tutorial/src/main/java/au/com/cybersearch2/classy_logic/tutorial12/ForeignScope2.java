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
 * ForeignScope
 * Demonstrates handling an amount espressed in Euros within a french scope and 
 * translating a term using a context list. This time the query is declared in the global 
 * scope and the calculator is referenced by a 2-part name.
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class ForeignScope2 
{
/* foreign-scope.xpl
axiom item (amount) : parameter;

axiom german.lexicon 
  ( Total)
  {"Gesamtkosten"};
  
axiom french.lexicon 
  ( Total)
  {"le total"};
  
calc total
(
  currency amount,
  amount *= 1.1
);

calc format_total
+ list<term> lexicon@scope;
(
  string total_text = 
    lexicon->Total + 
    " + gst: " + 
    total.amount.format
);

scope german (language="de", region="DE")
{
  query<term> item_query(item : total) -> (format_total);
}

scope french (language="fr", region="FR")
{
}

query<term> french_item_query(item : french.total) -> (french.format_total);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public ForeignScope2()
    {
        File resourcePath = new File("src/main/resources/tutorial12");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the foreign-scope.xpl script and runs the global scope "french_item_query, displaying the solution on the console.<br/>
     * The expected result:<br/>
     * french_item_query(total_text=le total + gst: 13 580,24 EUR)<br/>
     * @return AxiomTermList iterator containing the final Calculator solution
     */
    public Axiom getFrenchTotalAmount()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("foreign-scope.xpl");
        parserContext = queryProgramParser.getContext();
        // Create QueryParams object for scope "global" and query "item_query"
        QueryParams queryParams = queryProgram.getQueryParams("french_item_query");
        // Add an item Axiom with a single "12.345,67 EUR" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("item", new Axiom("item", new Parameter("amount", "12.345,67 €")));
        Result result = queryProgram.executeQuery(queryParams);
        return result.getAxiom("french_item_query");
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
            ForeignScope2 foreignScope = new ForeignScope2();
            System.out.println(foreignScope.getFrenchTotalAmount().toString());
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
