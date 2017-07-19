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
 * BlackIsWhite
 * Demonstrates term list used to change axiom terms. In this case, an axiom named
 * "patch" defines a color by it's rgb values and is modified by inverting these
 * values. The color name is also updated. There are 2 term lists which access
 * patch axiom, one writes to it and the other reads from it to ensure the
 * axiom has been updated, not just the writer term list. A third term list
 * maps color names to their inverse names, eg. "aqua" to "red". All term lists
 * are attached to the template that uses them, which means they share the 
 * template name space. Note that the term lists reference axioms using 2
 * part names as this is a requirement for attachments.
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class BlackIsWhite 
{
/* black-is-white.xpl
axiom patch (name, red, green, blue)
           {"blank", 0,     0,    0};

axiom swatch (name, red, green, blue)
            {"aqua",  0, 255,   255}
            {"black", 0,   0,     0}
            {"blue",  0,   0,   255};

axiom inverse (aqua, black, blue)  
            {"red", "white",  "yellow"};
                      
            
template shade
+ list<term> color1(global.patch);
+ list<term> color2(global.patch);
+ list<term> inverse_name(global.inverse);
(
  before = color2->name,
  // Change name
. color1->name = inverse_name[name], 
  // Invert colors
. color1->red = red ^ 255, 
. color1->green = green ^ 255, 
. color1->blue = blue ^ 255, 
  // Check colors have expected values
  color2->name,
  color2->red, 
  color2->green, 
  color2->blue
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
	 * Compiles the black-is-white.xpl script and runs the "colors" query, displaying the solution on the console.
	 * Each color color solution starts with a "before" term to show the patch color name before it is reversed.
	 * This turns black into white etc.
	 * A separate Axiom Term list for the same axiom proves the swatch terms have been modified, not just the color list variables.
	 * The expected result:<br/>
        shade(before=blank, name=red, red=255, green=0, blue=0)<br/>
        shade(before=red, name=white, red=255, green=255, blue=255)<br/>
        shade(before=white, name=yellow, red=255, green=255, blue=0)<br/>   
     */
	public Iterator<Axiom> displayShades()
	{
        QueryProgram queryProgram = queryProgramParser.loadScript("black-is-white.xpl");
        //queryProgram.setExecutionContext(new ExecutionContext());
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("colors");
        return result.axiomIterator("colors");
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
