/**
    Copyright (C) 2016  www.cybersearch2.com.au

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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.compile.SourceMarker;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * InWordsTest
 * @author Andrew Bowley
 * 5Feb.,2017
 */
public class InWordsTest
{
    @Test
    public void testInWords() throws Exception
    {
        InWords inWords = new InWords();
        File testFile = new File("src/main/resources/tutorial4", "query_in_words.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        Iterator<Axiom> axiomIterator = inWords.findInWords();
        while (axiomIterator.hasNext()) 
              checkSolution(reader, axiomIterator.next().toString());
        Iterator<SourceMarker> iterator = inWords.getParserContext().getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom lexicon (1,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("lexicon(word,definition):resource (1,1) (1,43)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query query_in_words (3,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("lexicon:in_words (3,29) (3,46)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("template in_words (2,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("word \\^in[^ ]+\\ (2,20) (2,41)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("string definition (2,44) (2,60)");
    }
    
    protected void checkSolution(BufferedReader reader, String word)
    {
        try
        {
            String line = reader.readLine();
            assertThat(word).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
