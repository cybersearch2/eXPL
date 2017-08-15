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
package au.com.cybersearch2.classy_logic.tutorial12;

import java.io.File;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * ShowLocale
 * @author Andrew Bowley
 * 11 Mar 2015
 */
public class ShowLocale 
{
/* 

*/

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public ShowLocale()
    {
        queryProgramParser = new QueryProgramParser(new File("src/main/resources/tutorial12"));
    }
    
	/**
	 * Compiles the script and runs the "locale_query" query, displaying the solution on the console.<br/>
	 */
	public String getLocale()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("show-locale.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("locale_query");
	    return result.getAxiom("locale_query").toString();
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
	        ShowLocale showLocale = new ShowLocale();
            System.out.println(showLocale.getLocale());

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
