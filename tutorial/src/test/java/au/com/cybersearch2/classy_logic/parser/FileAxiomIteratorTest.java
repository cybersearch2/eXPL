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
package au.com.cybersearch2.classy_logic.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * FileAxiomIteratorTest
 * @author Andrew Bowley
 * 9Jan.,2017
 */
public class FileAxiomIteratorTest
{
    final static String NAME = "myAxiom";
    final static String TERM_NAME = "MyTerm";

    @Test 
    public void test_serialization1() throws Exception
    {
        int termCount = 10;
        int id = 0;
        Parameter[] testTerms = new Parameter[termCount];
        testTerms[id] = new Parameter(TERM_NAME + id, BigDecimal.TEN);
        testTerms[id].setId(++id);
        Axiom axiom = new Axiom(NAME);
        axiom.addTerm(testTerms[0]);
        File serializeFile = File.createTempFile("axiom_test_serialization", null, null);
        System.out.println(serializeFile.toString());
        serializeFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(serializeFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(axiom);
        oos.close();
        File headerFile = writeHeader(serializeFile, 1);
        FileAxiomIterator underTest = new FileAxiomIterator(serializeFile);
        assertThat(underTest.hasNext()).isTrue();
        Axiom marshalled  = underTest.next();
        assertThat(marshalled.getName()).isEqualTo(NAME);
        assertThat(marshalled.getTermCount()).isEqualTo(1);
        Term term = marshalled.getTermByIndex(0);
        assertThat(term.getId()).isEqualTo(1);
        assertThat(term.getName()).isEqualTo(TERM_NAME + 0);
        assertThat(term.getValue()).isEqualTo(BigDecimal.TEN);
        assertThat(marshalled.getName()).isEqualTo(NAME);
        assertThat(marshalled.getTermCount()).isEqualTo(1);
        headerFile.delete();
        underTest.getOnCloseHandler().run();
        //assertThat(marshalled.pairByPosition).isFalse();
    }

    @Test 
    public void test_serialization2() throws Exception
    {
        int termCount = 10;
        int id = 0;
        Parameter[] testTerms = new Parameter[termCount];
        testTerms[id] = new Parameter(TERM_NAME + id, BigDecimal.TEN);
        testTerms[id].setId(++id);
        Axiom axiom = new Axiom(NAME);
        axiom.addTerm(testTerms[0]);
        testTerms[id] = new Parameter(TERM_NAME + id, BigDecimal.ONE);
        testTerms[id].setId(++id);
        Axiom axiom2 = new Axiom(NAME);
        axiom2.addTerm(testTerms[1]);
        File serializeFile = File.createTempFile("axiom_test_serialization", null, null);
        System.out.println(serializeFile.toString());
        serializeFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(serializeFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(axiom);
        oos.writeObject(axiom2);
        oos.close();
        File headerFile = writeHeader(serializeFile, 2);
        FileAxiomIterator underTest = new FileAxiomIterator(serializeFile);
        assertThat(underTest.hasNext()).isTrue();
        Axiom marshalled  = underTest.next();
        assertThat(underTest.hasNext()).isTrue();
        Axiom marshalled2  = underTest.next();
        assertThat(marshalled.getName()).isEqualTo(NAME);
        assertThat(marshalled.getTermCount()).isEqualTo(1);
        Term term = marshalled.getTermByIndex(0);
        assertThat(term.getId()).isEqualTo(1);
        assertThat(term.getName()).isEqualTo(TERM_NAME + 0);
        assertThat(term.getValue()).isEqualTo(BigDecimal.TEN);
        assertThat(marshalled.getName()).isEqualTo(NAME);
        assertThat(marshalled.getTermCount()).isEqualTo(1);
        Term term2 = marshalled2.getTermByIndex(0);
        assertThat(term2.getId()).isEqualTo(2);
        assertThat(term2.getName()).isEqualTo(TERM_NAME + 1);
        assertThat(term2.getValue()).isEqualTo(BigDecimal.ONE);
        headerFile.delete();
        underTest.getOnCloseHandler().run();
        //assertThat(marshalled.pairByPosition).isFalse();
    }


    private File writeHeader(File axiomFile, int count) throws IOException
    {
        AxiomHeader axiomHeader = new AxiomHeader();
        axiomHeader.setName(NAME);
        axiomHeader.setCreated(new Date());
        axiomHeader.setUser(System.getProperty("user.name"));
        axiomHeader.setCount(count);
        XStream xStream = new XStream();
        xStream.alias("axiomHeader", AxiomHeader.class);
        File headerFile = new File(axiomFile.getAbsolutePath() + ".xml");
        PrintWriter writer = null;
        writer = new PrintWriter(headerFile);
        xStream.toXML(axiomHeader, writer);
        writer.close();
        return headerFile;
}
}
