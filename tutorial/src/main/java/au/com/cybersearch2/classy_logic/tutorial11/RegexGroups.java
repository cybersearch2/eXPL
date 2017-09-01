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

import java.io.File;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.LexiconAxiomProvider;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * RegexGroups
 * Shows regular expression grouping. The application selects from a list, words starting with "in".
 * Each word has a second term consisting of part of speech (n, v, a or j) and a word definition.
 * A regular expression splits this term which using groups. Both group terms are made private
 * using a dot operator as a single text string is exported by the query.
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class RegexGroups 
{
    /* definitions.txt - converted to axioms with terms "word" and "definition"
    abbey - n. a monastery ruled by an abbot
    abide - v. dwell; inhabit or live in
    abound - v. be abundant or plentiful; exist in large quantities
    absence - n. the state of being absent
    absorb - v. assimilate or take in
    abstinence - n. practice of refraining from indulging an appetite especially alcohol
    absurd - j. inconsistent with reason or logic or common sense
    ...
    */
/* regex-groups.xpl
// Use an external axiom source (class LexiconSource)
resource lexicon axiom(word, definition);

string wordRegex = "^in[^ ]+";
string defRegex = "^(.)\\. (.*+)";

// Convert single letter part of speech to word
list<axiom> expand
{ 
   n = "noun",
   v = "verb",
   a = "adv.",
   j = "adj." 
};
   
template in_words 
(
  regex word ? wordRegex, 
. regex definition ? defRegex { part, def },
  expand[part],
  def
);

query<axiom> in_words(lexicon : in_words);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

	/**
	 * Construct RegexGroups object
	 */
	public RegexGroups()
	{
        File resourcePath = new File("src/main/resources/tutorial11");
        // Use an external axiom source which is bound in TestAxiomProvider dependency class
        // to AxiomSource class LexiconSource
        queryProgramParser = new QueryProgramParser(resourcePath, new LexiconAxiomProvider());
	}
	
    /**
     * Compiles the LEXICAL_SEARCH script and runs the "query_in_words" query, displaying the solution on the console.<br/>
     * The first 3 lines of the expected result:<br/>
        inadequate (adj.) not sufficient to meet a need<br/>
        incentive (noun) a positive motivational influence<br/>
        incidence (noun) the relative frequency of occurrence of something<br/>
     * @return Axiom iterator containing the final "in" words solution
     */
	public Iterator<Axiom> getRegexGroups()
	{
		// Expected 54 results 
        QueryProgram queryProgram = queryProgramParser.loadScript("regex-groups.xpl");
        parserContext = queryProgramParser.getContext();
		Result result = queryProgram.executeQuery("in_words");
		return result.axiomIterator("in_words");
 	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
    }
 
    protected static String get(Axiom axiom, String key)
    {
        return axiom.getTermByName(key).getValue().toString();
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
	        {
	            Axiom axiom = iterator.next();
	            String word = get(axiom, "word");
                String part = get(axiom, "part");
                String def = get(axiom, "def");
	            System.out.println(word + " (" + part + ") " + def);
	        }
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
