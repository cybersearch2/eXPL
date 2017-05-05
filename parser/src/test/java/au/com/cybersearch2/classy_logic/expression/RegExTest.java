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
package au.com.cybersearch2.classy_logic.expression;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.LexiconSource;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.compile.Group;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryExecuter;
import au.com.cybersearch2.classy_logic.query.QueryExecuterAdapter;

/**
 * RegExTest
 * @author Andrew Bowley
 * 21 Dec 2014
 */
public class RegExTest 
{
	@Before
	public void setUp()
	{
        Archetype.CASE_INSENSITIVE_NAME_MATCH = true;
	}
	

	@Test
	public void test_RegEx_query() throws IOException 
	{
		LexiconSource lexiconSource = new LexiconSource();
		Template inWordsTemplate = new Template(new QualifiedTemplateName(QualifiedName.EMPTY, "in_words"));
		inWordsTemplate.setKey("Lexicon");
		RegExOperand regExOperand = new RegExOperand(QualifiedName.parseName("Word"), "^in[^ ]+", 0, null);
		inWordsTemplate.addTerm(regExOperand);
		inWordsTemplate.addTerm(new TestStringOperand("Definition"));
		assertThat(inWordsTemplate.toString()).isEqualTo("in_words(Word \\^in[^ ]+\\, Definition)");
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(lexiconSource, Collections.singletonList(inWordsTemplate));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter inWordsQuery = new QueryExecuter(queryParams);
    	File inWordList = new File("src/test/resources", "in_words.lst");
     	LineNumberReader reader = new LineNumberReader(new FileReader(inWordList));
		while (inWordsQuery.execute())
		{
 	 	    String line = reader.readLine();
			assertThat(inWordsQuery.toString()).isEqualTo(line);
		}
		reader.close();
	}

	@Test
	public void test_groups()
	{
		LexiconSource lexiconSource = new LexiconSource();
		Template dictionaryTemplate = new Template(new QualifiedTemplateName(QualifiedName.EMPTY, "dictionary"));
		dictionaryTemplate.setKey("Lexicon");
		Group group = new Group("dictionary");
		Operand g1 = mock(Operand.class);
		Operand g2 = mock(Operand.class);
		group.addGroup(g1);
		group.addGroup(g2);
		RegExOperand regExOperand = new RegExOperand(QualifiedName.parseName("Definition"), "^(.)\\. (.*+)", 0, group);
		dictionaryTemplate.addTerm(new TestStringOperand("Word"));
		dictionaryTemplate.addTerm(regExOperand);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(lexiconSource, Collections.singletonList(dictionaryTemplate));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter dictionaryQuery = new QueryExecuter(queryParams);
		//while(dictionaryQuery.execute())
        //    System.out.println(dictionaryQuery.toString());
		if (dictionaryQuery.execute())
		{
		    // TODO - capture to verify
			//verify(g1).assign(new Parameter(Term.ANONYMOUS, "n"));
			//verify(g2).assign(new Parameter(Term.ANONYMOUS, "a monastery ruled by an abbot"));
			assertThat(regExOperand.getValue().toString()).isEqualTo("n. a monastery ruled by an abbot");
		}
		else
			fail("Query execute returned false");
	}
}
