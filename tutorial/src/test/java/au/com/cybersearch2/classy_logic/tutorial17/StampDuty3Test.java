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
 * StampDuty3Test
 * @author Andrew Bowley
 * 23Apr.,2017
 */
public class StampDuty3Test
{
    @Test
    public void testStampDuty3() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial17", "stamp-duty3.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        StampDuty3 stampDuty3 = new StampDuty3();
        Iterator<Axiom> dutyIterator = stampDuty3.getStampDuty();
        while(dutyIterator.hasNext())
        {
            checkSolution(reader, dutyIterator.next().toString());
        }
        ParserContext context = stampDuty3.getParserContext();
        Iterator<SourceMarker> iterator = context.getSourceMarkerSet().iterator();
        //assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        System.out.println(sourceMarker.toString());
        //assertThat(sourceMarker.toString()).isEqualTo("choice bracket (1,1)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        System.out.println(sourceItem.toString());
        //assertThat(sourceItem.toString()).isEqualTo("choice bracket(amount,threshold,base,percent) (1,1) (2,47)");
    }

    protected void checkSolution(BufferedReader reader, String duty)
    {
        try
        {
            String line = reader.readLine();
            assertThat(duty).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
