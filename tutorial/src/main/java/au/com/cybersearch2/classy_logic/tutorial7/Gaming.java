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
 * Demonstrates term list arrow notation to reterence term by name.
 * Class AxiomMarks provides another example of parentheses.<\br>
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class Gaming 
{

/* gaming.xpl
axiom spin (r1, r2, r3, r4) {3,2,0,1};

axiom fruit() {"apple", "orange", "banana", "lemon"};

list<term> combo(fruit);
template play(combo[r1], combo[r2], combo[r3], combo[r4]);

query<term> spin(spin : play);

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
	 * The expected result:<br/>
	 * play(r1=lemon, r2=banana, r3=apple, r4=orange)<br/>
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
