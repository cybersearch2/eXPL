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

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * StampDuty
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class StampDuty 
{
	static final String STAMP_DUTY =
			"choice bracket "
			+ "(amount,       threshold,  base,    percent) :\n" +
			"  (amount <  12000,      0,     0.00, 1.00),\n" +
			"  (amount <  30000,  12000,   120.00, 2.00),\n" +
			"  (amount <  50000,  30000,   480.00, 3.00),\n" +
			"  (amount < 100000,  50000,  1080.00, 3.50),\n" +
			"  (amount < 200000, 100000,  2830.00, 4.00),\n" +
			"  (amount < 250000, 200000,  6830.00, 4.25),\n" +
			"  (amount < 300000, 250000,  8955.00, 4.75),\n" +
			"  (amount < 500000, 300000, 11330.00, 5.00),\n" +
			"  (amount > 500000, 500000, 21330.00, 5.50);\n" +
			"\n" +
			"axiom transacton_amount (amount) : parameter;\n" +
			"calc payable(duty = base + (amount - threshold) * (percent / 100));\n" +
			"query stamp_duty_query calc(transacton_amount : bracket) >> calc(payable);\n";

	/**
	 * Compiles the STAMP_DUTY script and runs the "stamp_duty_query" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
	 * payable(duty = 3768.32)<br/>
	 */
	public Axiom getStampDuty()
	{
		QueryProgram queryProgram = new QueryProgram(STAMP_DUTY);
		// Create QueryParams object for Global scope and query "stamp_duty_query"
		QueryParams queryParams = new QueryParams(queryProgram, QueryProgram.GLOBAL_SCOPE, "stamp_duty_query");
		// Add a transacton_amount Axiom with a single 123,458 term
		// This axiom goes into the Global scope and is removed at the start of the next query.
		queryParams.addAxiom("transacton_amount", Integer.valueOf(123458));
		final Axiom[] payableHolder = new Axiom[1];
		// Add a solution handler to display the final Calculator solution
		queryParams.setSolutionHandler(new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
			    payableHolder[0] = solution.getAxiom("payable");
				return true;
			}});
		queryProgram.executeQuery(queryParams);
		return payableHolder[0];
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
            System.out.println(stampDuty.getStampDuty().toString());
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
