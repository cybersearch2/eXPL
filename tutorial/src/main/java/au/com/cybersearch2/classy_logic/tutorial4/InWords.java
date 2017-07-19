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
package au.com.cybersearch2.classy_logic.tutorial4;

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
 * InWords
 * Shows regular expression selecting from a list, words starting with "in"
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class InWords 
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
/* query_in_words.xpl
axiom lexicon (word, definition) : resource;

template in_words (regex word == "^in[^ ]+", definition);

query<axiom> query_in_words(lexicon : in_words);

*/
    protected QueryProgramParser queryProgramParser;
    ParserContext parserContext;
	
	public InWords()
	{
        File resourcePath = new File("src/main/resources/tutorial4");
        // Use an external axiom source which is bound in TestAxiomProvider dependency class
        // to AxiomSource class LexiconSource
        queryProgramParser = new QueryProgramParser(resourcePath, new LexiconAxiomProvider());
	}
	
    /**
     * Compiles the query_in_words.xpl script and runs the "query_in_words" query
     */
    public Iterator<Axiom> findInWords() 
    {
        QueryProgram queryProgram = queryProgramParser.loadScript("query_in_words.xpl");
        parserContext = queryProgramParser.getContext();
        Result result = queryProgram.executeQuery("query_in_words");
        return result.axiomIterator("query_in_words");
    }

    public ParserContext getParserContext()
    {
        return parserContext;
    }
    
	/*
     * Expected 54 results can be found in /src/test/resources/in_words.lst. 
     * Here is the first result: </br>
     * in_words(word=inadequate, definition=j. not sufficient to meet a need)</br>
	 */
    public static void main(String[] args)
    {
        try 
        {
            InWords inWords = new InWords();
            Iterator<Axiom> iterator = inWords.findInWords();
            while(iterator.hasNext()) 
                System.out.println(iterator.next().toString());
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
