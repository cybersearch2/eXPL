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
package au.com.cybersearch2.classy_logic.tutorial10;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ColorSwatch
 * Demonstrates Choice using literal selection terms and default selection.
 * @author Andrew Bowley
 * 16 Mar 2015
 */
public class ColorSwatch 
{
    static final String CHOICE_COLORS =
            "// Uninitialized variable matches to any value\n" +
            "double unknown_rgb;\n" +
            "// Choice selects on hex color value to get color name and rgb values\n" +
            "choice swatch (rgb, color, red, green, blue) :\n" +
            "(0x00FFFF, \"aqua\", 0, 255, 255),\n" +
            "(0x000000, \"black\", 0, 0, 0),\n" +
            "(0x0000FF, \"blue\", 0, 0, 255),\n" +
            "(0xFFFFFF, \"white\", 255, 255, 255),\n" +
            "(unknown_rgb,  \"unknown\", 0, 0, 0);\n" +
            "axiom shade (rgb) : parameter;\n" +
            "query color_query (shade : swatch);\n";
            ;

	/**
	 * Compiles the CHOICE_COLORS script and runs the "color_query" query, displaying the solution on the console.<br/>
	 */
	public String getColorSwatch(int hexColor)
	{
        QueryProgram queryProgram = new QueryProgram(CHOICE_COLORS);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.valueOf(hexColor)))); // aqua
        final StringBuilder builder = new StringBuilder();
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                builder.append(solution.getAxiom("swatch").toString());
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        return builder.toString();
	}
	
    /**
     * Run tutorial
     * The expected result:<br/>
        swatch(rgb = 65535, color = aqua, red = 0, green = 255, blue = 255)<br/>
        swatch(rgb = 255, color = blue, red = 0, green = 0, blue = 255)<br/>
        swatch(rgb = 7864319, color = unknown, red = 0, green = 0, blue = 0)<br/>
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        ColorSwatch ColorSwatch = new ColorSwatch();
            System.out.println(ColorSwatch.getColorSwatch(0x00ffff).toString());
            System.out.println(ColorSwatch.getColorSwatch(0x0000ff).toString());
            System.out.println(ColorSwatch.getColorSwatch(0x77ffff).toString());
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
