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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.LexiconAxiomProvider;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParserModule;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
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
	    "axiom part(letter, word): \n" +
		" (\"n\", \"noun\"),\n" +
		" (\"v\", \"verb\"),\n" +
		" (\"j\", \"adj.\"),\n" +
		" (\"v\", \"adv.\");\n" +
		"// Collect words starting with 'in' along with other details\n" +
		"template in_words (Word regex(wordRegex), Definition regex(defRegex { part, def }));\n" +
		"// Expand part letter to word in chain query\n" +
		"template part_expand(letter ? letter == in_words.part, word);" +
		"// Use calculator to create list items\n" +
		"calc word_def(\n" +
		"  word = in_words.Word, \n" +
		"  part = part_expand.word,\n" +
		"  def = in_words.def);\n" +
		"// Send result to a list\n" +
	    "list word_definitions(word_def);\n" +
		"query query_in_words(lexicon : in_words) >> (part : part_expand) >> calc(word_def);";

	@Inject
	ProviderManager providerManager;

	public RegexGroups()
	{
		// Configure dependency injection to get resource "lexicon"
		new DI(new QueryParserModule()).validate();
		DI.inject(this);
		providerManager.putAxiomProvider(new LexiconAxiomProvider());
	}
	
	public void displayRegexGroups() throws ParseException
	{
		// Expected 54 results can be found in /src/test/resources/in_words.lst. 
		// Here is the first solution: 
		// word = inadequate, part = adj., def = not sufficient to meet a need
		QueryProgram queryProgram = compileScript(LEXICAL_SEARCH);
		Result result = queryProgram.executeQuery("query_in_words");
		@SuppressWarnings("unchecked")
		Iterator<AxiomTermList> iterator = (Iterator<AxiomTermList>) result.getList("word_definitions").iterator();
        while(iterator.hasNext())
		    System.out.println(iterator.next().toString());
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
		RegexGroups regexGroups = new RegexGroups();
		try 
		{
			regexGroups.displayRegexGroups();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
