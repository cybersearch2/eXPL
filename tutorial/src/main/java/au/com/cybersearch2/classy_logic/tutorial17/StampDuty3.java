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

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
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
/* stamp-duty3.xpl
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
    
axiom transacton_amount 
  (  amount  )
  {123458.00}
  {55876.33}
  {1245890.00};

calc stamp_duty_payable(
. currency amount,
. << bracket(amount) >> (bracket, threshold, base, percent),
. currency duty = base + (amount - threshold) * (percent / 100),
  bracket,
  string property_value = format(amount),
  string payable = format(duty)
);

query<axiom> stamp_duty(transacton_amount : stamp_duty_payable);
 
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public StampDuty3()
    {
        File resourcePath = new File("src/main/resources/tutorial17");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the STAMP_DUTY script and runs the "stamp_duty_query" query. 
	 * Choice named "bracket" here is a term of the "stamp_duty_payable"a calculator.
	 * Note how selection term "amount" is declared preceding the Choice so as to give it a specific type.<br/>
	 * The expected results:<br/>
    stamp_duty_payable(bracket = 4, property_value = AUD123,458.00, payable = AUD3,768.32)<br/>
    stamp_duty_payable(bracket = 3, property_value = AUD55,876.33, payable = AUD1,285.67)<br/>
    stamp_duty_payable(bracket = 8, property_value = AUD1,245,890.00, payable = AUD62,353.95)<br/>
     * @return Axiom iterator
	 */
	public Iterator<Axiom> getStampDuty()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("stamp-duty3.xpl");
        //queryProgram.setExecutionContext(new ExecutionContext());
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("stamp_duty");
        return result.getIterator("stamp_duty");
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
