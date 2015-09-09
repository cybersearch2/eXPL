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

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Colors
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class Colors {

    static final String AXIOM_COLORS =
    		"axiom swatch (name, red, green, blue) :\n" +
    		"(\"aqua\", 0, 255, 255),\n" +
    		"(\"black\", 0, 0, 0),\n" +
    		"(\"blue\", 0, 0, 255);\n" +
            "list<term> color(swatch);\n" +
    		"template shade(name, color[red], color[green], color[blue]);\n" +
    		"query colors(swatch : shade);"
    		;

	/**
	 * Compiles the CITY_EVELATIONS script and runs the "high_city" query, displaying the solution on the console.<br/>
	 * The expected result:<br/>
		shade(name = aqua, color_red = 0, color_green = 255, color_blue = 255)<br/>
		shade(name = black, color_red = 0, color_green = 0, color_blue = 0)<br/>
		shade(name = blue, color_red = 0, color_green = 0, color_blue = 255)<br/>
	 */
	public void displayShades()
	{
		QueryProgram queryProgram = new QueryProgram(AXIOM_COLORS);
		queryProgram.executeQuery("colors", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("shade").toString());
				return true;
			}});
	}

	public static void main(String[] args)
	{
		try 
		{
	        Colors colorsDemo = new Colors();
			colorsDemo.displayShades();
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
