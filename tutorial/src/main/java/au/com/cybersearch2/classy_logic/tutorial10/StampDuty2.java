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
package au.com.cybersearch2.classy_logic.tutorial10;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * StampDuty3
 * Choice as calulator term example
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class StampDuty2 
{
/* stamp-duty2.xpl
choice bracket
    (    amount,     threshold, base, percent)
    {amount > 500000, 500000, 21330.00, 5.50}
    {amount > 300000, 300000, 11330.00, 5.00}
    {amount > 250000, 250000,  8955.00, 4.75}
    {amount > 200000, 200000,  6830.00, 4.25}
    {amount > 100000, 100000,  2830.00, 4.00}
    {amount >  50000,  50000,  1080.00, 3.50}
    {amount >  30000,  30000,   480.00, 3.00}
    {amount >  12000,  12000,   120.00, 2.00}
    {amount >   5000,      0,     0.00, 1.00};
 
axiom transaction_amount 
  ( id,     amount     )
  { 100077,    3789.00 }
  { 100078,  123458.00 }
  { 100079,   55876.33 }
  { 100080, 1245890.00 };
  
calc stamp_duty_payable(
. currency $ "US" amount,
. percent = 0.0,
. currency $ "US" duty = 20.00,
. choice bracket(),
  ? percent > 0.0
  {
    duty = base + (amount - threshold) 
           * (percent / 100)
  },
  id,
  Amount = amount.format,
  string Duty = duty.format,
  Bracket = bracket
);

query<axiom> stamp_duty_query (transaction_amount : stamp_duty_payable);

*/
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public StampDuty2()
    {
        File resourcePath = new File("src/main/resources/tutorial10");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the stamp-duty2.xpl script and runs the "stamp_duty_query" query. 
	 * Choice named "bracket" here is a term of the "stamp_duty_payable"a calculator.
	 * Note how selection term "amount" is declared preceding the Choice so as to give it a specific type.<br/>
	 * The expected results (with locale currency code, here "AUD":<br/>
        stamp_duty_payable(amount = 123458.0, bracket = 4, duty = 3768.320, display = AUD3,768.32)<br/>
        stamp_duty_payable(amount = 55876.33, bracket = 3, duty = 1285.67155, display = AUD1,285.67)<br/>
        stamp_duty_payable(amount = 1245890.0, bracket = 8, duty = 62353.9500, display = AUD62,353.95)
     * @return Axiom iterator
	 */
	public Iterator<Axiom> getStampDuty()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("stamp-duty2.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("stamp_duty_query");
        return result.axiomIterator("stamp_duty_query");
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
	        StampDuty2 stampDuty = new StampDuty2();
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
