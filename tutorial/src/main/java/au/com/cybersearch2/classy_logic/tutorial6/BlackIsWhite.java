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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * Colors
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class BlackIsWhite 
{
/* colors.xpl
list<term> color(swatch);
list<term> rgb(swatch);
axiom swatch (name, red, green, blue)
            {"aqua",  0, 255,   255}
            {"black", 0,   0,     0}
            {"blue",  0,   0,   255};
template shade
(
  name, 
  // Invert colors
  color[red] ^= 255, 
  color[green] ^= 255, 
  color[blue] ^= 255, 
  // Check colors have expected values
  integer r = rgb[red], 
  integer g = rgb[green], 
  integer b = rgb[blue]
);
query<axiom> colors(swatch : shade);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

    public BlackIsWhite()
    {
        File resourcePath = new File("src/main/resources/tutorial6");
        queryProgramParser = new QueryProgramParser(resourcePath);
    }
    
	/**
	 * Compiles the AXIOM_COLORS script and runs the "colors" query, displaying the solution on the console.<br/>
	 * Here each color is reversed by xor with 255, which turns black into white etc.
	 * A separate Axiom Term list for the same axiom proves the swatch terms have been modified, not just the color list variables.
	 * The expected result:<br/>
		shade(name=aqua, shade.red=255, shade.green=0, shade.blue=0,<br/> 
		      r=255, g=0, b=0)<br/>
		shade(name=black, shade.red=255, shade.green=255, shade.blue=255,<br/> 
		      r=255, g=255, b=255)<br/>
		shade(name=blue, shade.red=255, shade.green=255, shade.blue=0,<br/> 
		      r=255, g=255, b=0)<br/>	 
	 */
	public Iterator<Axiom> displayShades()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("black-is-white.xpl");
        //queryProgram.setExecutionContext(new ExecutionContext());
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
	        BlackIsWhite blackIsWhite = new BlackIsWhite();
			Iterator<Axiom> iterator = blackIsWhite.displayShades();
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
