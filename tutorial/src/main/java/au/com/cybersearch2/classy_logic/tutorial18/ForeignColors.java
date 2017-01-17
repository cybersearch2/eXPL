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
package au.com.cybersearch2.classy_logic.tutorial18;

import java.io.File;

import au.com.cybersearch2.classy_logic.JavaTestResourceEnvironment;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.FileAxiomProvider;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ForeignColors
 * Demonstrates Choice selection terms consisting of local axiom terms for locale-sensitive matching of values.
 * The Choice selects a color swatch by name in the language of the locale.
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class ForeignColors 
{
    static final String FOREIGN_LEXICON =
            "scope french (language=\"fr\", region=\"FR\"){}\n" +
            "scope german (language=\"de\", region=\"DE\"){}\n" +
            "axiom french.colors (aqua, black, blue, white)\n" +
            "  {\"bleu vert\", \"noir\", \"bleu\", \"blanc\"};\n" +
            "axiom german.colors (aqua, black, blue, white)\n" +
            "  {\"Wasser\", \"schwarz\", \"blau\", \"weiß\"};\n" +
            "list<term> german_list(german.colors : resource);\n" +
            "list<term> french_list(french.colors : resource);\n" +
            "query color_query (german.colors:german.colors) >> (french.colors:french.colors);\n";

    static final String FOREIGN_COLORS =
            "axiom colors (aqua, black, blue, white);\n" +
            "axiom german.colors (aqua, black, blue, white) : resource;\n" +
            "axiom french.colors (aqua, black, blue, white) : resource;\n" +
            "local select(colors);\n" +
            "choice swatch (name, red, green, blue)\n" +
            "{select[aqua], 0, 255, 255}\n" +
            "{select[black], 0, 0, 0}\n" +
            "{select[blue], 0, 0, 255}\n" +
            "{select[white], 255, 255, 255};\n" +
            "axiom shade (name) : parameter;\n" +
            "scope french (language=\"fr\", region=\"FR\")\n" +
            "{\n" +
            "  query color_query (shade : swatch);\n" +
            "}" +
            "scope german (language=\"de\", region=\"DE\")\n" +
            "{\n" +
            "  query color_query (shade : swatch);\n" +
            "}";

    /** ProviderManager is Axiom source for eXPL compiler */
    private ProviderManager providerManager;
    private static FileAxiomProvider[] fileAxiomProviders;

    public ForeignColors()
    {
        providerManager = new ProviderManager();
        File testPath = new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION);
        if (!testPath.exists())
            testPath.mkdir();
        fileAxiomProviders = new FileAxiomProvider[2];
        fileAxiomProviders[0] = new FileAxiomProvider("german.colors", testPath);
        fileAxiomProviders[1] = new FileAxiomProvider("french.colors", testPath);
        for (FileAxiomProvider provider: fileAxiomProviders)
            providerManager.putAxiomProvider(provider);
    }

    public void createForeignLexicon()
    {
        QueryProgram queryProgram = new QueryProgram(providerManager);
        queryProgram.parseScript(FOREIGN_LEXICON);
        try
        {
            queryProgram.executeQuery("color_query", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                System.out.println(solution.getAxiom("german.colors").toString());
                System.out.println(solution.getAxiom("french.colors").toString());
                return true;
            }});
        }
        finally
        {
            fileAxiomProviders[0].close();
            fileAxiomProviders[1].close();
        }
    }
    
    /**
	 * Compiles the GERMAN_COLORS script and runs the "color_query" query, displaying the solution on the console.
	 * @return AxiomTermList iterator containing the final Calculator solution
	 */
    public String getColorSwatch(String language, String name)
	{
        QueryProgram queryProgram = new QueryProgram(providerManager);
        queryProgram.parseScript(FOREIGN_COLORS);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams(language, "color_query");
        // Add a shade Axiom with a specified color term
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
        colors(aqua = Wasser, black = schwarz, blue = blau, white = weiß)<br/>
        colors(aqua = bleu vert, black = noir, blue = bleu, white = blanc)<br/>
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
	        ForeignColors foreignColors = new ForeignColors();
	        foreignColors.createForeignLexicon();
            System.out.println(foreignColors.getColorSwatch("french", "bleu vert"));
            System.out.println(foreignColors.getColorSwatch("french", "noir"));
            System.out.println(foreignColors.getColorSwatch("french", "blanc"));
            System.out.println(foreignColors.getColorSwatch("french", "bleu"));
            System.out.println(foreignColors.getColorSwatch("german", "Wasser"));
            System.out.println(foreignColors.getColorSwatch("german", "schwarz"));
            System.out.println(foreignColors.getColorSwatch("german", "weiß"));
            System.out.println(foreignColors.getColorSwatch("german", "blau"));
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
		finally
		{
		    fileAxiomProviders[0].close();
            fileAxiomProviders[1].close();
		}
		System.exit(0);
	}
}
