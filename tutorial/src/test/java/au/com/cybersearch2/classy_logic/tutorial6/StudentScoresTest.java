/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.tutorial6;

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
 * StudentScoresTest
 * @author Andrew Bowley
 * 10Apr.,2017
 */
public class StudentScoresTest
{
    static String[] marks =
    {
        "f-", "f", "f+", "e-", "e", "e+", "d-", "d", "d+", "c-", "c", "c+", "b-", "b", "b+", "a-", "a", "a+"
    };
    
    @Test
    public void testStudentScores() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial6", "student-scores.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        StudentScores scores = new StudentScores();
        Iterator<Axiom> scoreIterator = scores.displayLists();
        while (scoreIterator.hasNext())
            checkSolution(reader, scoreIterator.next().toString());
        Iterator<SourceMarker> iterator = scores.getParserContext().getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom grades (1,1)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("list mark (5,2)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("variable mark_var0 (6,2)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("mark_var0 = mark_1 = f- (6,2) (6,16)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("variable mark_var1 (7,2)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("mark_var1 = mark_2 = f (7,2) (7,15)");
        for (int index = 10; index < marks.length; ++index)
        {
            assertThat(iterator.hasNext()).isTrue();
            sourceMarker = iterator.next();
            //System.out.println(sourceMarker.toString());
            assertThat(sourceMarker.toString()).isEqualTo("variable mark_var" + (index) + " (" + (index + 6) + ",2)");
            sourceItem = sourceMarker.getHeadSourceItem();
            assertThat(sourceItem).isNotNull();
            //System.out.println(sourceItem.toString());
            assertThat(sourceItem.toString()).isEqualTo("mark_var" + (index) + " = mark_" + (index + 1) + " = " + marks[index] + " (" + (index + 6) + ",2) (" + (index + 6) + "," + (14 + marks[index].length()) + ")");
        }
        for (int index = 2; index < 10; ++index)
        {
            assertThat(iterator.hasNext()).isTrue();
            sourceMarker = iterator.next();
            //System.out.println(sourceMarker.toString());
            assertThat(sourceMarker.toString()).isEqualTo("variable mark_var" + (index) + " (" + (index + 6) + ",2)");
            sourceItem = sourceMarker.getHeadSourceItem();
            assertThat(sourceItem).isNotNull();
            //System.out.println(sourceItem.toString());
            assertThat(sourceItem.toString()).isEqualTo("mark_var" + (index) + " = mark_" + (index + 1) + " = " + marks[index] + " (" + (index + 6) + ",2) (" + (index + 6) + "," + (14 + marks[index].length()) + ")");
        }
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query marks (25,2)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("template score (24,2)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("student (24,17) (24,23)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("mark_var18 = mark_english = <empty> (24,26) (24,38)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("mark_var19 = mark_maths = <empty> (24,41) (24,51)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("mark_var20 = mark_history = <empty> (24,54) (24,66)");
        assertThat(iterator.hasNext()).isFalse();
     }

    protected void checkSolution(BufferedReader reader, String scores)
    {
        try
        {
            String line = reader.readLine();
            assertThat(scores).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
