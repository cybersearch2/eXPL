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

import au.com.cybersearch2.classy_logic.LexiconAxiomProvider;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * InWords
 * Shows regular expression selecting from list of words terms starting with "in"
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class InWords 
{
	static final String LEXICAL_SEARCH = 
		// Use an external axiom source which is bound in TestAxiomProvider dependency class
	    // to AxiomSource class LexiconSource
		"axiom lexicon (word, definition) : resource \"lexicon\";\n" +
		"template in_words (word regex(\"^in[^ ]+\"), string definition);\n" +
		"query query_in_words(lexicon : in_words);";

	protected ProviderManager providerManager;
	
	public InWords()
	{
		providerManager = new ProviderManager(new File("src/main/resources"));
		providerManager.putAxiomProvider(new LexiconAxiomProvider());
	}
	
	public void displayInWords()
	{
		// Expected 54 results can be found in /src/test/resources/in_words.lst. 
		// Here is the first result: 
		// in_words(Word = inadequate, Definition = j. not sufficient to meet a need)
		QueryProgram queryProgram = new QueryProgram(providerManager);
		queryProgram.parseScript(LEXICAL_SEARCH);
		queryProgram.executeQuery("query_in_words", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				Axiom wordAxiom = solution.getAxiom("in_words");
					System.out.println(wordAxiom.toString());
				return true;
			}});
	}
	
	public static void main(String[] args)
	{
		try 
		{
	        InWords inWords = new InWords();
			inWords.displayInWords();
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
