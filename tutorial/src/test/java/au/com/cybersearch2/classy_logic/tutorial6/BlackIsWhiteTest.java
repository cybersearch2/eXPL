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
 * BlackIsWhiteTest
 * @author Andrew Bowley
 * 8Apr.,2017
 */
public class BlackIsWhiteTest
{

    @Test
    public void testBlackIsWhite() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial6", "black-is-white.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        BlackIsWhite blackIsWhite = new BlackIsWhite();
        Iterator<Axiom> colorIterator = blackIsWhite.displayShades();
        while (colorIterator.hasNext())
            checkSolution(reader, colorIterator.next().toString());
        Iterator<SourceMarker> iterator = blackIsWhite.getParserContext().getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query colors (32,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("swatch:shade (32,21) (32,34)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom inverse (9,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("inverse(aqua,black,blue)[1] (9,1) (10,39)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom patch (1,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("patch(name,red,green,blue)[1] (1,1) (2,36)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom swatch (4,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("swatch(name,red,green,blue)[3] (4,1) (7,36)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("template shade (13,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("before=color2.name (18,3) (18,23)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("color1.name=inverse_name.name (20,3) (20,35)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("color1.red=red^255 (22,3) (22,25)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("color1.green=green^255 (23,3) (23,29)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("list shade.color1 (14,3)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("list<term> shade.color1(patch){4} (14,3) (14,33)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("list shade.color2 (15,3)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("list<term> shade.color2(patch){4} (15,3) (15,33)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("list shade.inverse_name (16,3)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("list<term> shade.inverse_name(inverse){3} (16,3) (16,41)");
        assertThat(iterator.hasNext()).isFalse();
    }

    protected void checkSolution(BufferedReader reader, String shade)
    {
        try
        {
            String line = reader.readLine();
            assertThat(shade).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
