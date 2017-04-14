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
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
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
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public StampDuty2()
    {
        File resourcePath = new File("src/main/resources/tutorial10");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

	/**
	 * Compiles the STAMP_DUTY script and runs the "stamp_duty_query" query. 
	 * Choice named "bracket" here is a term of the "stamp_duty_payable"a calculator.
	 * Note how selection term "amount" is declared preceding the Choice so as to give it a specific type.<br/>
	 * The expected results:<br/>
        stamp_duty_payable(amount = 123458.0, bracket = true, duty = 3768.320, display = AUD3,768.32)<br/>
        stamp_duty_payable(amount = 55876.33, bracket = true, duty = 1285.67155, display = AUD1,285.67)<br/>
        stamp_duty_payable(amount = 1245890.0, bracket = true, duty = 62353.9500, display = AUD62,353.95)
     * @return Axiom iterator
	 */
	public Iterator<Axiom> getStampDuty()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("stamp-duty2.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("stamp_duty_query");
        return result.getIterator(QualifiedName.parseGlobalName("payable"));
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
