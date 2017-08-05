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
package au.com.cybersearch2.classy_logic.tutorial7;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * Gaming
 * Demonstrates dyanamic axiom list initialization with parameters
 * Class AxiomMarks provides another example of parentheses.<\br>
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class Gaming 
{

/* gaming.xpl
list<axiom> spin  
  { c1=3^r1, c2=2^r1, c3=0^r1, c4=1^r1 }
  { c1=0^r2, c2=1^r2, c3=2^r2, c4=3^r2 }
  { c1=2^r3, c2=1^r3, c3=3^r3, c4=0^r3 }
  (
    integer r1 = system.random(4),
    integer r2 = system.random(4),
    integer r3 = system.random(4)
  );

axiom fruit() {"apple ", "orange", "banana", "lemon "};

template play
+ list<term> combo(fruit);
(combo[c1], combo[c2], combo[c3], combo[c4]);

query<axiom> gamble(spin : play);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public Gaming()
    {
        File resourcePath = new File("src/main/resources/tutorial7");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
	 * Compiles the gaming.xpl script and runs the "spin" query, displaying the solution on the console.<br/>
	 * The result will be an unpredicable three rows and four columns containing lemon, banana, apple and orange:<br/>
	 */
	public Iterator<Axiom> displayFruit()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("gaming.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("gamble");
        return result.axiomIterator("gamble");
	}

    FunctionManager provideFunctionManager()
    {
        FunctionManager functionManager = new FunctionManager();
        SystemFunctionProvider systemFunctionProvider = new SystemFunctionProvider();
        functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
        return functionManager;
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		try 
		{
	        Gaming gaming = new Gaming();
	        Iterator<Axiom> iterator = gaming.displayFruit();
	        while (iterator.hasNext())
	        {
	            Axiom axiom = iterator.next();
	            System.out.print(axiom.getTermByIndex(0).getValue().toString() + ", ");
                System.out.print(axiom.getTermByIndex(1).getValue().toString() + ", ");
                System.out.print(axiom.getTermByIndex(2).getValue().toString() + ", ");
                System.out.println(axiom.getTermByIndex(3).getValue().toString());
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
