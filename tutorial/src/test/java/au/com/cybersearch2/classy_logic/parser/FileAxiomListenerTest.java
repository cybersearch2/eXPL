package au.com.cybersearch2.classy_logic.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigDecimal;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

public class FileAxiomListenerTest
{
    final static String NAME = "myAxiom";
    final static String TERM_NAME = "MyTerm";

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
        testTerms[id] = new Parameter(TERM_NAME + id, BigDecimal.ONE);
        testTerms[id].setId(++id);
        Axiom axiom2 = new Axiom(NAME);
        axiom2.addTerm(testTerms[1]);
        File serializeFile = File.createTempFile("axiom_test_serialization", null, null);
        System.out.println(serializeFile.toString());
        serializeFile.deleteOnExit();
        FileAxiomListener underTest = new FileAxiomListener(NAME, serializeFile);
        Runnable onCloseHandler = underTest.getOnCloseHandler();
        underTest.onNextAxiom(axiom);
        underTest.onNextAxiom(axiom2);
        onCloseHandler.run();
        FileInputStream fis = new FileInputStream(serializeFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Axiom marshalled  = (Axiom) ois.readObject();
        Axiom marshalled2  = (Axiom) ois.readObject();
        ois.close();
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
        File headerFile = new File(serializeFile.getAbsolutePath() + ".xml");
        headerFile.delete();
        //assertThat(marshalled.pairByPosition).isFalse();
    }

}
