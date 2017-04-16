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
package au.com.cybersearch2.classy_logic.tutorial10;

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
 * StampDuty
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class StampDuty 
{
/* stamp-duty.xpl
choice bracket
  (amount,       threshold,  base,    percent)
  {amount <  12000,      0,     0.00, 1.00}
  {amount <  30000,  12000,   120.00, 2.00}
  {amount <  50000,  30000,   480.00, 3.00}
  {amount < 100000,  50000,  1080.00, 3.50}
  {amount < 200000, 100000,  2830.00, 4.00}
  {amount < 250000, 200000,  6830.00, 4.25}
  {amount < 300000, 250000,  8955.00, 4.75}
  {amount < 500000, 300000, 11330.00, 5.00}
  {amount > 500000, 500000, 21330.00, 5.50};

axiom transacton_amount (amount) : parameter;

calc payable(duty = base + (amount - threshold) * (percent / 100));

query<term> stamp_duty(transaction_amount : bracket) >> (payable);

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public StampDuty()
    {
        File resourcePath = new File("src/main/resources/tutorial10");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the STAMP_DUTY script and runs the "stamp_duty_query" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * stamp_duty(duty = 3768.32)<br/>
	 */
	public String getStampDuty()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("stamp-duty.xpl");
        parserContext = queryProgramParser.getContext();
		// Create QueryParams object for Global scope and query "stamp_duty"
		QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "stamp_duty");
		// Add a transacton_amount Axiom with a single 123,458 term
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("transaction_amount", new Axiom("transaction_amount", new Parameter("amount", 123458))); 
		Result result = queryProgram.executeQuery(queryParams);
        return result.getAxiom("stamp_duty").toString();
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
	        StampDuty stampDuty = new StampDuty();
            System.out.println(stampDuty.getStampDuty());
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
