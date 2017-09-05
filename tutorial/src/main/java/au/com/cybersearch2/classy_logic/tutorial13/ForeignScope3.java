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
import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
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
public class ForeignScope3 
{
/* foreign-scope3.xpl
axiom catalog_no (catalog_no) : parameter;

string country = scope->region;
list<currency $ country> item_list =
{
  "12.345,67 €",
  "500,00 €"
};

choice tax_rate
 (country, percent)
     {"DE",18.0}
     {"FR",15.0}
     {"BE",11.0};
     
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
  currency amount = item_list[catalog_no],
  choice tax_rate(country),
  currency total = amount * (1.0 + percent/100)
);

calc format_total
+ list<term> lexicon@scope; 
(
  catalog_no,
  country,
  string text = " " + lexicon->Total + " " + lexicon->tax + ": " + 
    charge_plus_gst.total.format
);

scope german (language="de", region="DE"){}
scope french (language="fr", region="FR"){}
scope belgium_fr (language="fr", region="BE"){}
scope belgium_nl (language="nl", region="BE"){}

query item_query(catalog_no : german.charge_plus_gst) -> (catalog_no : german.format_total) ->
   (catalog_no : french.charge_plus_gst) -> (catalog_no : french.format_total) ->
   (catalog_no : belgium_fr.charge_plus_gst) -> (catalog_no : belgium_fr.format_total) ->
   (catalog_no : belgium_nl.charge_plus_gst) -> (catalog_no : belgium_nl.format_total);

*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public ForeignScope3()
    {
        File resourcePath = new File("src/main/resources/tutorial13");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the foreign-scope3.xpl script and runs the "item_query" query, displaying the solution on the console.<br/>
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public List<Axiom> getFormatedTotalAmount()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("foreign-scope3.xpl");
        parserContext = queryProgramParser.getContext();
		// Create QueryParams object for  query "item_query"
		QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "item_query");
        Solution initialSolution = queryParams.getInitialSolution();
        Axiom parameter = new Axiom("catalog_no", new Parameter("catalog_no", 0));
        initialSolution.put("catalog_no", parameter);
        List<Axiom> solutionList = new ArrayList<Axiom>();
        Result result = queryProgram.executeQuery(queryParams);
        solutionList.add(result.getAxiom("item_query"));
        parameter.getTermByName("catalog_no").setValue(1L);
        initialSolution.put("catalog_no", parameter);
        result = queryProgram.executeQuery(queryParams);
        solutionList.add(result.getAxiom("item_query"));
        return solutionList;
	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Run tutorial
     * The expected result:<br/>
        format_total(catalog_no=0, country=DE, text= Gesamtkosten Steuer: 14.567,89 EUR)<br/>
        format_total(catalog_no=0, country=FR, text= le total impôt: 14 197,52 EUR)<br/>
        format_total(catalog_no=0, country=BE, text= le total impôt: 13.703,69 EUR)<br/>
        format_total(catalog_no=0, country=BE, text= totale kosten belasting: 13.703,69 EUR)<br/>
        format_total(catalog_no=1, country=DE, text= Gesamtkosten Steuer: 590,00 EUR)<br/>
        format_total(catalog_no=1, country=FR, text= le total impôt: 575,00 EUR)<br/>
        format_total(catalog_no=1, country=BE, text= le total impôt: 555,00 EUR)<br/>
        format_total(catalog_no=1, country=BE, text= totale kosten belasting: 555,00 EUR)<br/>
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        ForeignScope3 foreignScope = new ForeignScope3();
	        List<Axiom> solutionList = foreignScope.getFormatedTotalAmount();
	        for (Axiom formatedTotal: solutionList)
	        {
	            QualifiedName key = formatedTotal.getArchetype().getQualifiedName();
	            AxiomTermList axiomList = new AxiomTermList(key, key);
	            axiomList.setAxiom(formatedTotal);
	            for (int i = 0; i < formatedTotal.getTermCount(); ++i)
	                System.out.println(axiomList.getItem(i).toString());
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
