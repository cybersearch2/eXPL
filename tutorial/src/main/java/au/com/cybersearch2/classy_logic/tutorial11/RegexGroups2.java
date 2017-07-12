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

import au.com.cybersearch2.classy_logic.DictionaryAxiomProvider;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;

/**
 * RegexGroups2
 * Like first RegexGroups application, shows regular expression grouping. However there are differences.
 * The source now supplies each entire line from definitions.txt file and this is split into 3 groups
 * by a single regular expression. Two of the group terms are exported. The part of speech term is
 * made private and is converted to an expanded version. The regex entry value is also made private
 * using the dot operator.
 * @author Andrew Bowley
 * 11Jul.,2017
 */
public class RegexGroups2 
{
/* regex-groups.xpl
// Use an external axiom source (class DictionarySource)
axiom dictionary (entry) : "dictionary";

// Convert single letter part of speech to word
axiom expand =
{ 
   n = "noun",
   v = "verb",
   a = "adv.",
   j = "adj." 
};

template in_words
( 
. regex entry == "(^in[^ ]+) - (.)\. (.*+)" { word, . pos, definition },
  part = expand[pos]
);

query<axiom> in_words(dictionary : in_words);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;

	/**
	 * Construct RegexGroups object
	 */
	public RegexGroups2()
	{
        //Archetype.CASE_INSENSITIVE_NAME_MATCH = true;
        File resourcePath = new File("src/main/resources/tutorial11");
        // Use an external axiom source which is bound in TestAxiomProvider dependency class
        // to AxiomSource class DictionarySource
        queryProgramParser = new QueryProgramParser(resourcePath, new DictionaryAxiomProvider());
	}
	
    /**
     * Compiles the dictionary-regex-groups.xpl script and runs the "in_words" query, displaying the solution on the console.<br/>
     * The first 3 lines of the expected result:<br/>
        in_words(word=inadequate, definition=not sufficient to meet a need, part=adj.)<br/>
        in_words(word=incentive, definition=a positive motivational influence, part=noun)<br/>
        in_words(word=incidence, definition=the relative frequency of occurrence of something, part=noun)<br/>
     * @return Axiom iterator containing the final "in" words solution
     */
	public Iterator<Axiom> getRegexGroups()
	{
		// Expected 54 results 
        QueryProgram queryProgram = queryProgramParser.loadScript("dictionary-regex-groups.xpl");
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
	        RegexGroups2 regexGroups = new RegexGroups2();
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
