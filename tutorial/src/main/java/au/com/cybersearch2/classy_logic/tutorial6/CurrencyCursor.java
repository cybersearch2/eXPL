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

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * CurrencyCursor
 * Demonstrates 
 * @author Andrew Bowley
 * 14 Sep 2015
 */
public class CurrencyCursor
{
/* currency-cursor.xpl
*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public CurrencyCursor()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }

    /**
     * Compiles the currency-cursor.xpl script and runs the "print_all_amounts" query.<br/>
     * The expected results:<br/>
     * @return Axiom iterator
     */
    public void  amounts()
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("currency-cursor.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("parse_amounts");
        Axiom axiom = result.getAxiom(new QualifiedName(QualifiedName.EMPTY, "all_amounts", "amount_list"));
        for (int i = 0; i < axiom.getTermCount(); ++i)
            System.out.println(axiom.getTermByIndex(i).toString());
        axiom = result.getAxiom("parse_amounts");
        System.out.println(axiom.getTermByIndex(0).toString());
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
            currencyCursor.amounts();
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
