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
package au.com.cybersearch2.classy_logic.tutorial18;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ForeignScope
 * Demonstrates handling an amount espressed in Euros within a German scope and 
 * translating a term using a local axiom. 
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class ForeignScope 
{
/* foreign-scope.xpl
axiom item (amount) : parameter;

choice tax_rate
 (country, percent)
     {"DE",18.0}
     {"FR",15.0}
     {"BE",11.0};
     
axiom lexicon (Total, tax);
axiom german.lexicon (Total, tax)
  {"Gesamtkosten","Steuer"};
axiom french.lexicon (Total, tax)
  {"le total","impôt"};
axiom belgium_fr.lexicon (Total, tax)
  {"le total","impôt"};
axiom belgium_nl.lexicon (Total, tax)
  {"totale kosten","belasting"};
  
calc charge_plus_gst
(
  currency amount,
  choice tax_rate(country = scope->region),
  currency total = amount * (1.0 + percent/100)
);

calc format_total
+ list<term> lexicon@scope; 
(
  string country = scope->region,
  string text = " " + lexicon->Total + " " + lexicon->tax + ": " + 
    charge_plus_gst.total.format
);

scope german (language="de", region="DE"){}
scope french (language="fr", region="FR"){}
scope belgium_fr (language="fr", region="BE"){}
scope belgium_nl (language="nl", region="BE"){}

query item_query(item : german.charge_plus_gst) -> (german.format_total) ->
   (item : french.charge_plus_gst) -> (french.format_total) ->
   (item : belgium_fr.charge_plus_gst) -> (belgium_fr.format_total) ->
   (item : belgium_nl.charge_plus_gst) -> (belgium_nl.format_total);
   
*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public ForeignScope()
    {
        File resourcePath = new File("src/main/resources/tutorial18");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the foreign-scope.xpl script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public List<Axiom> getFormatedTotalAmount()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("foreign-scope.xpl");
        parserContext = queryProgramParser.getContext();
		// Create QueryParams object for  query "item_query"
		QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "item_query");
		// Add an item Axiom with a single "2.345,67 EUR" term
		// This axiom goes into the Global scope and is removed at the start of the query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("item", new Axiom("item", new Parameter("amount", "12.345,67 €")));
        // Add a solution handler to display the final Calculator solution
        final List<Axiom> solutionList = new ArrayList<Axiom>(4);
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                solutionList.add(solution.getAxiom("german.format_total"));
                solutionList.add(solution.getAxiom("french.format_total"));
                solutionList.add(solution.getAxiom("belgium_fr.format_total"));
                solutionList.add(solution.getAxiom("belgium_nl.format_total"));
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        return solutionList;
	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * The expected result:<br/>
        format_total(country = DE, text =  Gesamtkosten Steuer: 14.567,89 EUR)<br/>
        format_total(country = FR, text =  le total impôt: 14 197,52 EUR)<br/>
        format_total(country = BE, text =  le total impôt: 13.703,69 EUR)<br/>
        format_total(country = BE, text =  totale kosten belasting: 13.703,69 EUR)<br/>
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        ForeignScope foreignScope = new ForeignScope();
	        List<Axiom> solutionList = foreignScope.getFormatedTotalAmount();
	        for (Axiom formatedTotal: solutionList)
	            System.out.println(formatedTotal.toString());
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
