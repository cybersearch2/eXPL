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
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import dagger.Module;
import dagger.Provides;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.compile.Group;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryExecuter;
import au.com.cybersearch2.classy_logic.query.QueryExecuterAdapter;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;

/**
 * RegExTest
 * @author Andrew Bowley
 * 21 Dec 2014
 */
public class RegExTest 
{
	@Module(injects = ParserAssembler.ExternalAxiomSource.class)
	static class RegExModule implements ApplicationModule
	{
	    @Provides @Singleton ProviderManager provideProviderManagerr()
	    {
	    	return new ProviderManager();
	    }
	}

	@Before
	public void setUp()
	{
		new DI(new RegExModule());
	}
	

	@Test
	public void test_RegEx_query() throws IOException 
	{
		LexiconSource lexiconSource = new LexiconSource();
		Template inWordsTemplate = new Template(new QualifiedName(QualifiedName.EMPTY, "in_words", QualifiedName.EMPTY));
		inWordsTemplate.setKey("Lexicon");
		RegExOperand regExOperand = new RegExOperand(QualifiedName.parseName("Word"), "^in[^ ]+", 0, null);
		inWordsTemplate.addTerm(regExOperand);
		inWordsTemplate.addTerm(new TestStringOperand("Definition"));
		assertThat(inWordsTemplate.toString()).isEqualTo("in_words(\"^in[^ ]+\", Definition)");
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
		Template dictionaryTemplate = new Template(new QualifiedName(QualifiedName.EMPTY, "dictionary", QualifiedName.EMPTY));
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
			verify(g1).assign("n");
			verify(g2).assign("a monastery ruled by an abbot");
			assertThat(regExOperand.getValue().toString()).isEqualTo("n. a monastery ruled by an abbot");
		}
		else
			fail("Query execute returned false");
	}
}
