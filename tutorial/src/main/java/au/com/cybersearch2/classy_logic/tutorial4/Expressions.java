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
package au.com.cybersearch2.classy_logic.tutorial4;

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * Types
 * @author Andrew Bowley
 * 23 Feb 2015
 */
public class Expressions 
{
	/* expressions.xpl
       integer x = 1;\n" +
       integer y = 2;\n" +
       calc evaluate(\n" +
         boolean can_add = x + y == 3,
         boolean can_subtract = y - x == 1,
         boolean can_multiply = x * y == 2,
         boolean can_divide = 6 / y == 3,
         boolean can_override_precedence = (y + 1) * 2 > x * 5,
         boolean can_assign = (y *= 3) == 6 && y == 6,
         boolean can_evaluate = can_add && can_subtract && can_multiply && can_divide && can_override_precedence && can_assign
        );
       query<term> expressions (evaluate);
    */
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    public Expressions()
    {
        File resourcePath = new File("src/main/resources/tutorial4");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the expressions.xpl script and runs the "expressions" query
     */
    public Axiom checkExpressions() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("expressions.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("expressions");
        return result.getAxiom("expressions");
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    /**
     * Displays expressions success summary flag on the console.
     * Note this sample uses a calculator instead of a template, as it does not require an axiom source in order to do a unification+evaluation step.
     * <br/>
     * The expected result:<br/>
        can_evaluate = true<br/>
	 */
    public static void main(String[] args)
    {
        try 
        {
            Expressions expressions = new Expressions();
            Axiom axiom = expressions.checkExpressions();
            Term evaluateTerm = axiom.getTermByName("can_evaluate");
            System.out.println(evaluateTerm.toString());
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
