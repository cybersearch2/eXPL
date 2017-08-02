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
 * Types
 * Demonstrates both term and axiom choices 
 * @author Andrew Bowley
 * 4 Jul 2017
 */
public class Choices 
{
/* choices.xpl
choice bank_by_prefix 
  ( prefix,  bank,                       bsb )
  { "456448", "Bank of Queensland",      "124-001" }
  { "456443", "Bendigo Bank LTD",        "633-000" }
  { "456445", "Commonwealth Bank Aust.", "527-146" };

axiom prefix_account 
  (prefix, account)
  { "456448", 2 }
  { "456445", 1 }
  { "456443", 3 };
  
template account_bank
(
  choice Account
  {
    "sav" ? account == 1,
    "cre" ? account == 2,
    "chq" ? account == 3
  },
. choice bank_by_prefix,
  Bank = bank,
  BSB = bsb
);

query<axiom> bank_details( prefix_account: account_bank ); 
   
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
    
    public Choices()
    {
        File resourcePath = new File("src/main/resources/tutorial3");
        queryProgramParser = new QueryProgramParser(resourcePath, provideFunctionManager());
    }

    /**
     * Compiles the types.xpl script and runs the "types" query
     */
    public Iterator<Axiom> checkChoices() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("choices.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("bank_details");
        return result.axiomIterator("bank_details");
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
     * Displays choices solution on the console. 
     * <br/>
     * The expected result:<br/>
        account_bank(Account=cre, Bank=Bank of Queensland, BSB=124-001)<br/>
        account_bank(Account=sav, Bank=Commonwealth Bank Aust., BSB=527-146)<br/>
        account_bank(Account=chq, Bank=Bendigo Bank LTD, BSB=633-000)<br/>      
     */
    public static void main(String[] args)
    {
        try 
        {
            Choices choices = new Choices();
            Iterator<Axiom> iterator = choices.checkChoices();
            while (iterator.hasNext())
                System.out.println(iterator.next().toString());
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
