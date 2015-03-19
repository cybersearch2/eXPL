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
package au.com.cybersearch2.classy_logic.tutorial2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * GreekConstruction2
 * @author Andrew Bowley
 * 22 Feb 2015
 */
public class GreekConstruction2 
{

	static final String GREEK_CONSTRUCTION =
			
		"axiom charge : \n" +
		"  (\"Athens\", 23 ),\n" +
		"  (\"Sparta\", 13 ),\n" +
		"  (\"Milos\", 17);\n" +
		
		"axiom customer :\n" +
		"  (\"Marathon Marble\", \"Sparta\"),\n" +
		"  (\"Acropolis Construction\", \"Athens\"),\n" +
		"  (\"Agora Imports\", \"Sparta\"),\n" +
		"  (\"Spiros Theodolites\", \"Milos\");\n" +
		
		"template freight(city,  charge);\n" +
		"template customer_freight(name, freight.city, freight.charge);\n" +
		
	    "query customer_charge(charge:freight, customer:customer_freight);";


	/**
	 * Compiles the GREEK_CONSTRUCTION script and runs the "customer_charge" query, displaying the solution on the console.<br/>
	 * The query has 2 unification steps. The first unifies "charge" axiom with "freight" template.<br/>
	 * The second unifies "customer" axiom with "customer_freight" template.
	 * The expected  "customer_freight" result:<br/>
	 * customer_freight(name = Acropolis Construction, freight.city = Athens, freight.charge = 23)<br/>
	 * customer_freight(name = Marathon Marble, freight.city = Sparta, freight.charge = 13)<br/>
	 * customer_freight(name = Agora Imports, freight.city = Sparta, freight.charge = 13)<br/>
	 * customer_freight(name = Spiros Theodolites, freight.city = Milos, freight.charge = 17)<br/>
	 * @throws ParseException
	 */
	public void displayCustomerCharges() throws ParseException
	{
		QueryProgram queryProgram = compileScript(GREEK_CONSTRUCTION);
		ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
		// The first unification fills in variables "city" and "charge".
		// These are read in the second unification as solution terms using "freight.city" and "freight.charge" notations.
		parserAssembler.registerAxiomListener("freight", new AxiomListener(){

			@Override
			public void onNextAxiom(Axiom axiom) {
				System.out.println("^ " + axiom.toString());
			}});
		queryProgram.executeQuery("customer_charge", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("customer_freight").toString());
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
		GreekConstruction2 greekConstruction = new GreekConstruction2();
		try 
		{
			greekConstruction.displayCustomerCharges();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
