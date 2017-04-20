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

public class RegexGroups 
{
/* regex-groups.xpl
// Use an external axiom source (class LexiconSource)
axiom lexicon (Word, Definition) : "lexicon";

string wordRegex = "^in[^ ]+";
string defRegex = "^(.)\. (.*+)";

// Convert single letter part of speech to word
axiom expand =
{ 
   n = "noun",
   v = "verb",
   a = "adv.",
   j = "adj." 
};
   
calc in_words 
(
. word regex(wordRegex), 
. definition regex(defRegex { part, def }),
  string in_word = word + ", " + expand[part] + "- " + def
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
        QueryProgram queryProgram = queryProgramParser.loadScript("regex-groups.xpl");
        parserContext = queryProgramParser.getContext();
		Result result = queryProgram.executeQuery("in_words");
		return result.getIterator("in_words");
 	}
	
    public ParserContext getParserContext()
    {
        return parserContext;
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
	            System.out.println(iterator.next().getTermByName("in_word").toString().substring(10));
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
