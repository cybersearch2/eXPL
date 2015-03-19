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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * Colors
 * @author Andrew Bowley
 * 27 Feb 2015
 */
public class BlackIsWhite {

    static final String AXIOM_COLORS =
    		"axiom swatch (name, red, green, blue) :\n" +
    		"(\"aqua\", 0, 255, 255),\n" +
    		"(\"black\", 0, 0, 0),\n" +
    		"(\"blue\", 0, 0, 255);\n" +
            "list<term> color(swatch);\n" +
            "list<term> rgb(swatch);\n" +
    		"template shade(name, color[red] ^= 255, color[green] ^= 255, color[blue] ^= 255," +
            "               integer r = rgb[red], integer g = rgb[green], integer b = rgb[blue]);\n" +
    		"query colors(swatch : shade);"
    		;

	/**
	 * Compiles the AXIOM_COLORS script and runs the "colors" query, displaying the solution on the console.<br/>
	 * Here each color is reversed by xor with 255, which turns black into white etc.
	 * A separate Axiom Term list for the same axiom proves the swatch terms have been modified, not just the color list variables.
	 * The expected result:<br/>
		shade(name = aqua, color.red = 255, color.green = 0, color.blue = 0,<br/> 
		      r = 255, g = 0, b = 0)<br/>
		shade(name = black, color.red = 255, color.green = 255, color.blue = 255,<br/> 
		      r = 255, g = 255, b = 255)<br/>
		shade(name = blue, color.red = 255, color.green = 255, color.blue = 0,<br/> 
		      r = 255, g = 255, b = 0)<br/>	 
     * @throws ParseException
	 */
	public void displayShades() throws ParseException
	{
		QueryProgram queryProgram = compileScript(AXIOM_COLORS);
		queryProgram.executeQuery("colors", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				System.out.println(solution.getAxiom("shade").toString());
				return true;
			}});
	}

	protected QueryProgram compileScript(String script) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
		return queryProgram;
	}
	
	public static void main(String[] args)
	{
		BlackIsWhite blackIsWhite = new BlackIsWhite();
		try 
		{
			blackIsWhite.displayShades();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
