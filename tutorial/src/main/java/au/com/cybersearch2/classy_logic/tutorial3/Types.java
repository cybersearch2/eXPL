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
package au.com.cybersearch2.classy_logic.tutorial3;

import java.io.File;

import au.com.cybersearch2.classy_logic.FunctionManager;
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
 * Demonstrates term types applied to both axiom and template terms. 
 * Also shows macros used set literal axiom values:
 * "decimal(1234.56)" Creates decimal literal instead of double 1234.56
 * "currency $ "DE" ("12.345,67 €")" Translates an amount in German Euros to decimal
 * "system.timestamp()" Calls system library function timestamp with no parameters to return a Date object
 * @see TimestampProvider
 * @author Andrew Bowley
 * 4 Jul 2017
 */
public class Types 
{
/* types.xpl
axiom literals
( 
  Boolean, 
  String, 
  Integer, 
  Double, 
  Decimal, 
  Currency, 
  Timestamp 
)
{    
  true, 
  "penguins",   
  12345, 
  1234e2, 
  decimal(1234.56), 
  currency $ "DE" ("12.345,67 €"),
  system.timestamp()
};

template variables
( 
  Boolean, 
  String, 
  Integer, 
  Double, 
  Decimal, 
  Currency, 
  timestamp = Timestamp
);
query<term> types(literals:variables);
   
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    public Types()
    {
        File resourcePath = new File("src/main/resources/tutorial3");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
     * Compiles the types.xpl script and runs the "types" query
     */
    public Axiom checkTypes() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("types.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("types");
        return result.getAxiom("types");
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
    FunctionManager provideFunctionManager()
    {
        FunctionManager functionManager = new FunctionManager();
        TimestampProvider timestampProvider = new TimestampProvider();
        functionManager.putFunctionProvider(timestampProvider.getName(), timestampProvider);
        return functionManager;
    }

    /**
     * Displays types solution on the console. Note timestamp details will vary.
     * <br/>
     * The expected result:<br/>
        Boolean=true(Boolean)<br/>
        String=penguins(String)<br/>
        Integer=12345(Long)<br/>
        Double=123400.0(Double)<br/>
        Decimal=1234.56(BigDecimal)<br/>
        Currency=12345.67(BigDecimal)<br/>
        amount=12345.67(BigDecimal)<br/>
        timestamp=[time, date, timezone](Date)<br/>	 
      */
    public static void main(String[] args)
    {
        try 
        {
            Types types = new Types();
            Axiom axiom = types.checkTypes();
            for (int i = 0; i < axiom.getTermCount(); ++i)
            {
                Term term = axiom.getTermByIndex(i);
                System.out.println(term.toString() + "(" + term.getValue().getClass().getSimpleName() + ")");
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
