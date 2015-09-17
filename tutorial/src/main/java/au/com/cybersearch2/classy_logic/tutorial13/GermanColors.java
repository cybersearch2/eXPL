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
package au.com.cybersearch2.classy_logic.tutorial13;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * GermanColors
 * Demonstrates Choice selection terms consisting of local axiom terms for locale-sensitive matching of values.
 * The Choice selects a color swatch by name in the language of the locale.
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class GermanColors 
{
    static final String GERMAN_COLORS =
            "axiom lexicon (language, aqua, black, blue, white)\n" +
            "  {\"english\", \"aqua\", \"black\", \"blue\", \"white\"}\n" +
            "  {\"german\", \"Wasser\", \"schwarz\", \"blau\", \"weiß\"};\n" +
            "local colors(lexicon);\n" +
            "choice swatch (name, red, green, blue)\n" +
            "{colors[aqua], 0, 255, 255}\n" +
            "{colors[black], 0, 0, 0}\n" +
            "{colors[blue], 0, 0, 255}\n" +
            "{colors[white], 255, 255, 255};\n" +
            "axiom shade (name) : parameter;\n" +
            "scope german (language=\"de\", region=\"DE\")\n" +
            "{\n" +
            "  query color_query (shade : swatch);\n" +
            "}";

	/**
	 * Compiles the GERMAN_COLORS script and runs the "color_query" query, displaying the solution on the console.
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public String getColorSwatch(String name)
	{
        QueryProgram queryProgram = new QueryProgram(GERMAN_COLORS);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams("german", "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", name)));
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
        swatch(name = Wasser, red = 0, green = 255, blue = 255)<br/>
        swatch(name = schwarz, red = 0, green = 0, blue = 0)<br/>
        swatch(name = weiß, red = 255, green = 255, blue = 255)<br/>
        swatch(name = blau, red = 0, green = 0, blue = 255)<br/>
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        GermanColors germanColors = new GermanColors();
            System.out.println(germanColors.getColorSwatch("Wasser"));
            System.out.println(germanColors.getColorSwatch("schwarz"));
            System.out.println(germanColors.getColorSwatch("weiß"));
            System.out.println(germanColors.getColorSwatch("blau"));
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
