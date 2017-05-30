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
package au.com.cybersearch2.classy_logic.tutorial6;

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * Colors
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class Colors 
{
    /* colors.xpl
    list<term> color(swatch);
    axiom swatch (name, red, green, blue)
    {"aqua", 0, 255, 255}
    {"black", 0, 0, 0}
    {"blue", 0, 0, 255};
    template shade(name, color^red, color^green, color^blue);
    query<axiom> colors(swatch : shade);

    */

    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public Colors()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
        shade(name=aqua, red=0, green=255, blue=255)<br/>
        shade(name=black, red=0, green=0, blue=0)<br/>
        shade(name=blue, red=0, green=0, blue=255)<br/>   	 
     */
	public Iterator<Axiom> displayShades()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("colors.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("colors");
        return result.getIterator("colors");
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	public static void main(String[] args)
	{
		try 
		{
	        Colors colorsDemo = new Colors();
			Iterator<Axiom> iterator = colorsDemo.displayShades();
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
