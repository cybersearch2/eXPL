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

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
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
	static final String FOREIGN_SCOPE =
			"axiom item (amount) : parameter;\n" +
	        "choice tax_rate\n" +
            "(country, percent)\n" +
            "    {\"DE\",18.0}\n" +
            "    {\"FR\",15.0}\n" +
            "    {\"BE\",11.0};\n" +
			"axiom lexicon (Total, tax);\n" +
            "axiom german.lexicon (Total, tax)\n" +
	        "  {\"Gesamtkosten\",\"Steuer\"};\n" +
            "axiom french.lexicon (Total, tax)\n" +
            "  {\"le total\",\"impôt\"};\n" +
            "axiom belgium_fr.lexicon (Total, tax)\n" +
            "  {\"le total\",\"impôt\"};\n" +
            "axiom belgium_nl.lexicon (Total, tax)\n" +
            "  {\"totale kosten\",\"belasting\"};\n" +
	        "local translate(lexicon);\n" +
			"calc charge_plus_gst(\n" +
	        "  currency amount,\n" +
            "  template tax_rate(percent) << tax_rate(scope[region]),\n" +
			"  currency total = amount * (1.0 + (percent / 100))\n" +
	        ");\n" +
			"calc format_total(\n" +
            "  string country = scope[region],\n" +
			"  string text = \" \" + translate[Total] +\n" +
			"  \" \" + translate[tax] + \": \" +\n" + 
			"  format(charge_plus_gst.total)\n" +
			");\n" +
			"scope german (language=\"de\", region=\"DE\"){}\n" +
            "scope french (language=\"fr\", region=\"FR\"){}\n" +
            "scope belgium_fr (language=\"fr\", region=\"BE\"){}\n" +
            "scope belgium_nl (language=\"nl\", region=\"BE\"){}\n" +
            " query item_query(item : german.charge_plus_gst) >> (german.format_total) >>\n" +
            "  (item : french.charge_plus_gst) >> (french.format_total) >>\n" +
            "  (item : belgium_fr.charge_plus_gst) >> (belgium_fr.format_total) >>\n" +
            "  (item : belgium_nl.charge_plus_gst) >> (belgium_nl.format_total);\n";

	/**
	 * Compiles the FOREIGN_SCOPE script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public List<Axiom> getFormatedTotalAmount()
	{
		QueryProgram queryProgram = new QueryProgram(FOREIGN_SCOPE);
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
