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
package au.com.cybersearch2.classy_logic.tutorial19;

import java.io.File;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * PetNames
 * Demonstrates library function call
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class TowersOfHanoi
{
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public TowersOfHanoi()
    {
        File resourcePath = new File("src/main/resources/tutorial19");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
     * Compiles the towers-of-hanoi.xpl script and runs the "" queries.<br/>
     * The expected results:<br/>
     * @return Axiom iterator
     */
    public void  calculateTowers()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("towers-of-hanoi.xpl");
        parserContext = queryProgramParser.getContext();
        System.out.println(" n=1");
        queryProgram.executeQuery("towers_of_hanoi1");
        System.out.println("\n n=2");
        queryProgram.executeQuery("towers_of_hanoi2");
        System.out.println("\n n=3");
        queryProgram.executeQuery("towers_of_hanoi3");
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
    
    /**
     * Run tutorial
     * @param args
     */
    public static void main(String[] args)
    {
        try 
        {
            TowersOfHanoi towersOfHanoi = new TowersOfHanoi();
            towersOfHanoi.calculateTowers();
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
