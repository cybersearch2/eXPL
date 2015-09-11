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
package au.com.cybersearch2.classy_logic.tutorial11;

import java.util.Iterator;

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.LexiconAxiomProvider;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classyinject.DI;

/**
 * RegexGroups
 * Shows regular expression selecting from list of words terms starting with "in"
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class RegexGroups 
{
	static final String LEXICAL_SEARCH = 
		// Use an external axiom source which is bound in TestAxiomProvider dependency class
	    // to AxiomSource class LexiconSource
		"axiom lexicon (Word, Definition) : resource \"lexicon\";\n" +
		"string wordRegex = \"^in[^ ]+\";\n" +
	    "string defRegex = \"^(.)\\. (.*+)\";\n" +
	    "// Convert single letter part of speech to word\n" +
	    "axiom expand =  \n" +
		" { n = \"noun\",  \n" +
		"   v = \"verb\",  \n" +
        "   a = \"adv.\",  \n" +
		"   j = \"adj.\" };\n" +
		"// Collect words starting with 'in' along with other details\n" +
		"template in_words (\n" +
		"  word regex(wordRegex), definition regex(defRegex { part, def }),\n" +
        "  word = word + \", \" + expand[part] + \"- \" + def\n" +
		");\n" +
		"query query_in_words(lexicon : in_words);";
 
	/** ProviderManager object wihich is axiom source for the compiler */
	@Inject
	ProviderManager providerManager;

	/**
	 * Construct RegexGroups object
	 */
	public RegexGroups()
	{
		// Configure dependency injection to get resource "lexicon"
		new DI(new QueryParserModule()).validate();
		DI.inject(this);
		providerManager.putAxiomProvider(new LexiconAxiomProvider());
	}
	
    /**
     * Compiles the LEXICAL_SEARCH script and runs the "query_in_words" query, displaying the solution on the console.<br/>
     * The first 3 lines of the expected result:<br/>
        word_def(word = inadequate, part = adj., def = not sufficient to meet a need)<br/>
        word_def(word = incentive, part = noun, def = a positive motivational influence)<br/>
        word_def(word = incidence, part = noun, def = the relative frequency of occurrence of something)<br/><br/>
      To view full results, go to src/main/resources/"in-words-list.txt  
     * @return Axiom iterator containing the final "in" words solution
     */
	public Iterator<Axiom> getRegexGroups()
	{
		// Expected 54 results can be found in /src/test/resources/in_words.lst. 
		// Here is the first solution: 
		// word = inadequate, part = adj., def = not sufficient to meet a need
		QueryProgram queryProgram = new QueryProgram(LEXICAL_SEARCH);
		Result result = queryProgram.executeQuery("query_in_words", new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.keySet());
                System.out.println(solution.getAxiom("in_words").getTermByName("word").toString());
                return true;
            }});
		return result.getIterator(QualifiedName.parseGlobalName("word_definitions"));
 	}
	
    /**
     * Run tutorial
     * @param args
     */
	public static void main(String[] args)
	{
		try 
		{
	        RegexGroups regexGroups = new RegexGroups();
	        Iterator<Axiom> iterator = regexGroups.getRegexGroups();
	        while(iterator.hasNext())
	            System.out.println(iterator.next().toString());
		} 
        catch (ExpressionException e) 
        { // Display nested ParseException
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
