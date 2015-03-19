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
		shade(name = aqua, color.red = 0, color.green = 255, color.blue = 255)<br/>
		shade(name = black, color.red = 0, color.green = 0, color.blue = 0)<br/>
		shade(name = blue, color.red = 0, color.green = 0, color.blue = 255)<br/>
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
		Colors colorsDemo = new Colors();
		try 
		{
			colorsDemo.displayShades();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
