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
package au.com.cybersearch2.classy_logic.pattern;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomTest
 * @author Andrew Bowley
 * 30 Nov 2014
 */
public class AxiomTest 
{
    
    private static final String TERM_NAME = "term";
    private static final String NAME = "axiom";

    @Test 
    public void test_serialization() throws Exception
    {
        int termCount = 10;
        int id = 0;
        Parameter[] testTerms = new Parameter[termCount];
        testTerms[id] = new Parameter(TERM_NAME + id, BigDecimal.TEN);
        testTerms[id].setId(++id);
        Axiom axiom = new Axiom(NAME);
        axiom.addTerm(testTerms[0]);
        File serializeFile = File.createTempFile("axiom_test_serialization", null, null);
        serializeFile.deleteOnExit();
        writeAxiom(axiom, serializeFile);
        Axiom marshalled = readAxiom(serializeFile);
        assertThat(marshalled.getName()).isEqualTo(NAME);
        assertThat(marshalled.getTermCount()).isEqualTo(1);
        Term term = marshalled.getTermByIndex(0);
        assertThat(term.getId()).isEqualTo(1);
        assertThat(term.getName()).isEqualTo(TERM_NAME + 0);
        assertThat(term.getValue()).isEqualTo(BigDecimal.TEN);
    }
    
    private Axiom readAxiom(File serializeFile) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(serializeFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object marshalled = ois.readObject();
        ois.close();
        return (Axiom)marshalled;
    }

    private void writeAxiom(Axiom axiom, File serializeFile) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(serializeFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(axiom);
        oos.flush();
        oos.close();
    }

    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }
}
