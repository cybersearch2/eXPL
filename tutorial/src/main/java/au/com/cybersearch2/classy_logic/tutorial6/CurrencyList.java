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
package au.com.cybersearch2.classy_logic.tutorial6;

import java.io.File;
import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * CurrencyList
 * Demonstrates converting a string list containing formatted amount string literals to a
 * and currency list holding decimal amounts. The list is included in the query result by
 * using the "export" modifier.
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class CurrencyList
{
/* currency-list.xpl
list<string> euro_amounts = 
{
  "14.567,89",
  "14 197,52",
  "590,00"
};

template all_amounts
+ export list<currency> amount_list;
(
. amount_list[0] = euro_amount[0],
. amount_list[1] = euro_amount[1],
. amount_list[2] = euro_amount[2],
);

query parse_amounts(all_amounts);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public CurrencyList()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the currency-listr.xpl script and runs the "parse_amounts" query.<br/>
     * The expected results:<br/>
        14567.89<br/>
        14197.52<br/>
        590<br/>
        total=29355.41<br/>
     * @return Axiom iterator
     */
    public void  amounts()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("currency-list.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("parse_amounts");
        Axiom axiom = result.getAxiom(new QualifiedName("global", "decimal_amounts", "amount_list"));
        BigDecimal[] amounts = (BigDecimal[])axiom.getTermByIndex(0).getValue();
        for (int i = 0; i < amounts.length; ++i)
            System.out.println(amounts[i].toString());
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
            CurrencyList currencyList = new CurrencyList();
            currencyList.amounts();
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