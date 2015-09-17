/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * FactOperandTest
 * @author Andrew Bowley
 * 20 Aug 2015
 */
public class FactOperandTest
{
    static final String AXIOM_KEY = "key";
    static final String AXIOM_NAME = "axiom.key";
    static QualifiedName QNAME = QualifiedName.parseName(AXIOM_NAME);
    
    @Test 
    public void test_types()
    {
        BooleanOperand booleanOperand = new TestBooleanOperand("BooleanOperand");
        FactOperand factOperand = new FactOperand(booleanOperand);
        factOperand.evaluate(1);
        assertThat((Boolean)factOperand.getValue()).isFalse();
        assertThat(factOperand.getId()).isEqualTo(1);
        booleanOperand.assign(new Parameter(Term.ANONYMOUS, Boolean.FALSE));
        factOperand.evaluate(2);
        assertThat((Boolean)factOperand.getValue()).isTrue();
        assertThat(factOperand.getId()).isEqualTo(2);
        booleanOperand.assign(new Parameter(Term.ANONYMOUS, Boolean.TRUE));
        factOperand.evaluate(3);
        assertThat((Boolean)factOperand.getValue()).isTrue();
        assertThat(factOperand.getId()).isEqualTo(3);

        // Create AxiomTermList to contain query result. 
        AxiomTermList axiomTermList = new AxiomTermList(QNAME, AXIOM_KEY);
        // Create Variable to be axiomTermList container. Give it the same name as the inner Template 
        // so it is qualified by the name of the enclosing Template
        Variable listVariable = new Variable(QNAME);
        listVariable.assign(new Parameter(Term.ANONYMOUS, axiomTermList));
        factOperand = new FactOperand(listVariable);
        factOperand.evaluate(1);
        assertThat((Boolean)factOperand.getValue()).isFalse();
        Axiom axiom = new Axiom(AXIOM_NAME);
        Parameter x = new Parameter("x");
        axiom.addTerm(x);
        axiomTermList.setAxiom(axiom);
        factOperand.evaluate(2);
        assertThat((Boolean)factOperand.getValue()).isFalse();
        Parameter y = new Parameter("y");
        axiom.addTerm(y);
        factOperand.evaluate(3);
        assertThat((Boolean)factOperand.getValue()).isFalse();
        x.assign(new Parameter(Term.ANONYMOUS, Long.valueOf(1)));
        y.assign(new Parameter(Term.ANONYMOUS, Long.valueOf(2)));
        factOperand.evaluate(4);
        assertThat((Boolean)factOperand.getValue()).isTrue();
    }
}
