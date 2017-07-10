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
package au.com.cybersearch2.classy_logic.tutorial17;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.compile.SourceMarker;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AgeDiscrimination2Test
 * @author Andrew Bowley
 * 18Apr.,2017
 */
public class AgeDiscrimination2Test
{
    @Test
    public void testAgeDiscrimination2() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial17", "age-discrimination2.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        AgeDiscrimination2 ageDiscrimination2 = new AgeDiscrimination2();
        Iterator<Axiom> personIterator = ageDiscrimination2.getAgeRating();
        while (personIterator.hasNext())
            checkSolution(reader, personIterator.next().toString());
        ParserContext context = ageDiscrimination2.getParserContext();
        Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("choice age_rating (18,2)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("choice age_rating(age,age_weight) (18,2) (19,24)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{age>29,0.3} (20,5) (20,17)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{age>25,0.6} (21,5) (21,17)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("{age>20,1.0} (22,5) (22,17)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom person (1,2)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("person(name,sex,age,starsign)[15] (1,2) (16,40)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query star_people (32,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("person:perfect_match (32,26) (32,47)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("calc perfect_match (24,2)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("age_rating(age) (26,4) (26,37)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("?is_age_weight_fact=age_weight (27,4) (27,20)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("name (28,4) (28,7)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("sex (28,10) (28,12)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("starsign (28,15) (28,22)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("double rating=age_weight+0.2*starsign==gemini (29,4) (30,1)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query perfect_match.age_rating (26,4)");
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("<- age_rating(age) (26,4) (26,20)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("-> perfect_match2.age_rating (26,23) (26,25)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("age_weight (26,27) (26,36)");
   }

    protected void checkSolution(BufferedReader reader, String person)
    {
        try
        {
            String line = reader.readLine();
            assertThat(person).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
