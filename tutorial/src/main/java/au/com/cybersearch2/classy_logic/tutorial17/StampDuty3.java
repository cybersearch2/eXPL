/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial17;

import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * StampDuty3
 * Choice as calulator term example
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class StampDuty3 
{
	static final String STAMP_DUTY =
            "choice bracket\n"
            +   "(amount,       threshold,  base,    percent)\n" +
            "    {amount <  12000,      0,     0.00, 1.00}\n" +
            "    {amount <  30000,  12000,   120.00, 2.00}\n" +
            "    {amount <  50000,  30000,   480.00, 3.00}\n" +
            "    {amount < 100000,  50000,  1080.00, 3.50}\n" +
            "    {amount < 200000, 100000,  2830.00, 4.00}\n" +
            "    {amount < 250000, 200000,  6830.00, 4.25}\n" +
            "    {amount < 300000, 250000,  8955.00, 4.75}\n" +
            "    {amount < 500000, 300000, 11330.00, 5.00}\n" +
            "    {amount > 500000, 500000, 21330.00, 5.50};\n" +
           "axiom transacton_amount (amount)\n" +
            "{123458.00}\n" +
            "{55876.33}\n" +
            "{1245890.00};\n" +
            "calc stamp_duty_payable(\n" +
            "  currency amount,\n" +
            "  template bracket(threshold, base, percent) << bracket(amount),\n" +
            "  currency duty = base + (amount - threshold) * (percent / 100),\n" +
            "  string display = format(duty)\n" +
            ");\n" +
            "list payable(stamp_duty_payable);\n" +
            "query stamp_duty_query (transacton_amount : stamp_duty_payable);\n";

	/**
	 * Compiles the STAMP_DUTY script and runs the "stamp_duty_query" query. 
	 * Choice named "bracket" here is a term of the "stamp_duty_payable"a calculator.
	 * Note how selection term "amount" is declared preceding the Choice so as to give it a specific type.<br/>
	 * The expected results:<br/>
        stamp_duty_payable(amount = 123458.0,duty = 3768.320, display = AUD3,768.32)<br/>
        stamp_duty_payable(amount = 55876.33, duty = 1285.67155, display = AUD1,285.67)<br/>
        stamp_duty_payable(amount = 1245890.0, duty = 62353.9500, display = AUD62,353.95)
     * @return Axiom iterator
	 */
	public Iterator<Axiom> getStampDuty()
	{
		QueryProgram queryProgram = new QueryProgram(STAMP_DUTY);
        Result result = queryProgram.executeQuery("stamp_duty_query");
        return result.getIterator(QualifiedName.parseGlobalName("payable"));
	}
	
    /**
     * Run tutorial
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        StampDuty3 stampDuty = new StampDuty3();
            Iterator<Axiom> iterator = stampDuty.getStampDuty();
            while(iterator.hasNext())
            {
                System.out.println(iterator.next().toString());
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
