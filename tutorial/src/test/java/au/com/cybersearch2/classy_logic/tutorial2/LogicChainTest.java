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
package au.com.cybersearch2.classy_logic.tutorial2;

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
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.tutorial2.LogicChain;

/**
 * LogicChainTest
 * @author Andrew Bowley
 * 11Apr.,2017
 */
public class LogicChainTest
{
    @Test
    public void testGreekConstruction() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial2", "logic-chain.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        LogicChain greekConstruction = new LogicChain();
        Iterator<Axiom> deliveryIterator = greekConstruction.displayCustomerCharges();
        while (deliveryIterator.hasNext())
            checkSolution(reader, deliveryIterator.next().toString());
        reader.close();
        /*
        Iterator<SourceMarker> iterator = greekConstruction.getParserContext().getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom customer (1,1)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom fee (7,1)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom freight (13,1)");
        assertThat(iterator.hasNext()).isTrue();
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query greek_business (22,1)");
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("customer:customer (22,22) (22,38)");
        sourceItem = sourceItem.getNext();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("fee:account (23,3) (23,17)");
        sourceItem = sourceItem.getNext();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("freight:delivery (23,20) (23,39)");
        */
    }

    protected void checkSolution(BufferedReader reader, String delivery)
    {
        try
        {
            String line = reader.readLine();
            assertThat(delivery).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
