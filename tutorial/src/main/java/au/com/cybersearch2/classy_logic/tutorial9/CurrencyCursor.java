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
package au.com.cybersearch2.classy_logic.tutorial9;

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
 * Demonstrates cursor used to iterate through a string list containing amounts in Euros, 
 * and currency list used to receive the amounts as decimal values. The cursor is
 * created by a function invoked as a list attribute. The cursor points initially to
 * the first item in the list and advances when the increment ++ postfix operator
 * is applied. When the end of the list is reached, the cursor becomes empty, which
 * is detected using the "fact" attribute of the cursor. The currency list grows
 * using concatenation and the list variable retains the last item appended to 
 * the list, which in this case, allows a total amount to be calculated using
 * a simple expression.
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class CurrencyCursor
{
/* currency-cursor.xpl
list<string> euro_amounts = 
{
  "14.567,89",
  "14 197,52",
  "590,00"
};

calc all_amounts
+ list<currency> amount_list;
(
  currency total = 0,
  currency $ "DE" euro_amount = euro_amounts.cursor,
  {
    ? euro_amount.fact,
    amount_list += euro_amount++,
    total += amount_list
  }
);

query<term> parse_amounts(all_amounts);

*/
    static class Amounts
    {
        public BigDecimal[] items;
        public String total;
    }
    
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public CurrencyCursor()
    {
        File resourcePath = new File("src/main/resources/tutorial9");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the currency-cursor.xpl script and runs the "parse_amounts" query.<br/>
     * The expected results:<br/>
        14567.89<br/>
        14197.52<br/>
        590<br/>
        total=29355.41<br/>
     * @return Amounts object
     */
    public Amounts  amounts()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("currency-cursor.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("parse_amounts");
        Axiom axiom = result.getAxiom(new QualifiedName("global", "all_amounts", "amount_list"));
        Amounts amounts = new Amounts();
        amounts.items = (BigDecimal[])axiom.getTermByIndex(0).getValue();
        axiom = result.getAxiom("parse_amounts");
        amounts.total = axiom.getTermByIndex(0).toString();
        return amounts;
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
            CurrencyCursor currencyCursor = new CurrencyCursor();
            Amounts amounts = currencyCursor.amounts();
            for (int i = 0; i < amounts.items.length; ++i)
                System.out.println(amounts.items[i].toString());
            System.out.println(amounts.total);
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
