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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.pattern.Structure;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

/**
 * StructureTest
 * @author Andrew Bowley
 * 17 Nov 2014
 */
public class StructureTest 
{
	@SuppressWarnings("serial")
    static class TestStructure extends Structure
	{
		public TestStructure(String name)
		{
			super(name);
		}

		public TestStructure(String name, List<Term> terms) 
		{
			super(name, terms);
			
		}
		
	}
	
	final static String NAME = "myStruct";

	private List<Term> getTermsList()
	{
		List<Term> paramList = new ArrayList<Term>();
		paramList.add(new Parameter("one", 1));
		paramList.add(new Parameter("two", 2));
		paramList.add(new Parameter("three", 3));
		return paramList;
	}
	
	
	@Test
	public void testNamedConstructorWithTermsContainingValues()
	{
		Object[] values = new Object[] { "One", Integer.valueOf(2), Boolean.TRUE };
		Structure testStructure = new Structure(NAME, values);
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "(One, 2, true)");
	}

	@Test
	public void testNamedConstructorWithEmptyValues()
	{
		Structure testStructure = new Structure(NAME, new Object[0]);
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "()");
	}

	@Test
	public void testNamedConstructorWithNullObjectsArray()
	{
		try
		{
		    new Structure(NAME, (Object[])null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"terms\" is null");
		}
	}


	@Test
	public void testNamedConstructorWithTermsArray()
	{
		Parameter[] terms = getTermsList().toArray(new Parameter[3]); 
		Structure testStructure = new Structure(NAME, terms);
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "(one=1, two=2, three=3)");
		assertThat(testStructure.getTermByName("one")).isEqualTo(terms[0]);
		assertThat(testStructure.getTermByName("two")).isEqualTo(terms[1]);
		assertThat(testStructure.getTermByName("three")).isEqualTo(terms[2]);
	}

	@Test
	public void testNamedConstructorWithEmptyTermsArray()
	{
		Structure testStructure = new Structure(NAME, new Parameter[0]);
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "()");
	}

	@Test
	public void testNamedConstructorWithObjectArray()
	{
		Object[] objectArray = new Object[]
		{
			new Integer(23), new Parameter("Test", "Value")
		};
		Structure testStructure = new Structure(NAME, objectArray);
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "(23, Test=Value)");
	}

	@Test
	public void testNamedConstructor()
	{
		try
		{
		    new TestStructure(null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null or empty");
		}
		try
		{
		    new TestStructure("");
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null or empty");
		}
	}

	@Test
	public void testNamedConstructorWithNullTermsArray()
	{
		try
		{
		    new Structure(NAME, (Parameter[])null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"terms\" is null");
		}
	}

	@Test
	public void testNamedConstructorWithTermsList()
	{
		List<Term> paramList = getTermsList();
		Structure testStructure = new Structure(NAME, paramList);
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "(one=1, two=2, three=3)");
		assertThat(testStructure.getTermByName("one")).isEqualTo(paramList.get(0));
		assertThat(testStructure.getTermByName("two")).isEqualTo(paramList.get(1));
		assertThat(testStructure.getTermByName("three")).isEqualTo(paramList.get(2));
	}

	@Test
	public void testNamedConstructorWithEmptyTermsList()
	{
		Structure testStructure = new Structure(NAME, new ArrayList<Term>());
		assertThat(testStructure.getName()).isEqualTo(NAME);
		assertThat(testStructure.isFact()).isTrue();
		assertThat(testStructure.toString()).isEqualTo(NAME + "()");
	}

	@Test
	public void testNamedConstructorWithNullTermsList()
	{
		try
		{
		    new Structure(NAME, (List<Term>)null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"terms\" is null");
		}
	}

	@Test
	public void test_getTermByName()
	{
		Structure testStructure = new Structure(NAME, new ArrayList<Term>());
		assertThat(testStructure.getTermByName("")).isNull();
		assertThat(testStructure.getTermByName(null)).isNull();
		List<Term> paramList = getTermsList();
		testStructure = new Structure(NAME, paramList);
		assertThat(testStructure.getTermByName("one")).isEqualTo(paramList.get(0));
		assertThat(testStructure.getTermByName("two")).isEqualTo(paramList.get(1));
		assertThat(testStructure.getTermByName("three")).isEqualTo(paramList.get(2));
		assertThat(testStructure.getTermByName("ONE")).isEqualTo(paramList.get(0));
		assertThat(testStructure.getTermByName("Two")).isEqualTo(paramList.get(1));
		assertThat(testStructure.getTermByName("THRee")).isEqualTo(paramList.get(2));
		assertThat(testStructure.getTermByName("four")).isNull();
		try
		{
			testStructure.getTermByName(null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null");
		}
		TestStructure testStructure2 = new TestStructure(NAME);
		assertThat(testStructure2.getTermByName("")).isNull();
		assertThat(testStructure2.getTermByName(null)).isNull();
	}

	@Test
	public void test_getTermByIndex()
	{
		Structure testStructure = new Structure(NAME, new ArrayList<Term>());
		assertThat(testStructure.getTermByIndex(-1)).isNull();
		assertThat(testStructure.getTermByIndex(0)).isNull();
		List<Term> paramList = getTermsList();
		testStructure = new Structure(NAME, paramList);
		assertThat(testStructure.getTermCount()).isEqualTo(3);
		assertThat(testStructure.getTermByIndex(0)).isEqualTo(paramList.get(0));
		assertThat(testStructure.getTermByIndex(1)).isEqualTo(paramList.get(1));
		assertThat(testStructure.getTermByIndex(2)).isEqualTo(paramList.get(2));
		assertThat(testStructure.getTermByIndex(-1)).isNull();
		assertThat(testStructure.getTermByIndex(3)).isNull();
		TestStructure testStructure2 = new TestStructure(NAME);
		assertThat(testStructure2.getTermByIndex(0)).isNull();
		assertThat(testStructure2.getTermByIndex(-1)).isNull();
		assertThat(testStructure2.getTermCount()).isEqualTo(0);
	}
	
	@Test
	public void test_isFact()
	{
		List<Term> paramList = new ArrayList<Term>();
		Parameter parameter1 = mock(Parameter.class);
		when(parameter1.getName()).thenReturn("parameter1");
		Parameter parameter2 = mock(Parameter.class);
		when(parameter2.getName()).thenReturn("parameter2");
		paramList.add(parameter1);
		paramList.add(parameter2);
		when(parameter1.isEmpty()).thenReturn(true);
		Structure testStructure = new Structure(NAME, paramList);
		assertThat(testStructure.isFact()).isFalse();
		when(parameter1.isEmpty()).thenReturn(false);
		when(parameter2.isEmpty()).thenReturn(true);
		assertThat(testStructure.isFact()).isFalse();
		when(parameter1.isEmpty()).thenReturn(false);
		when(parameter2.isEmpty()).thenReturn(false);
		assertThat(testStructure.isFact()).isTrue();
		Structure testStructure2 = new Structure(NAME, new ArrayList<Term>());
		assertThat(testStructure2.isFact()).isTrue();
	}
/*	
	@Test
	public void test_getName_Pattern()
	{
		Structure testStructure = new Structure(NAME, new ArrayList<Term>());
		Pattern namePattern = testStructure.getNamePattern();
		Matcher matcher = namePattern.matcher("one.two");
		assertThat(matcher.find()).isTrue();
		assertThat(matcher.group(1)).isEqualTo("one");
		assertThat(matcher.group(2)).isEqualTo("two");
	}
*/	
	@Test
	public void test_addTerm()
	{
		TestStructure testStructure = new TestStructure(NAME);
		Term term1 = mock(Term.class);
		when(term1.getName()).thenReturn(Term.ANONYMOUS);
		testStructure.addTerm(term1);
		assertThat(testStructure.getTermCount()).isEqualTo(1);
		assertThat(testStructure.termMap.size()).isEqualTo(0);
		final Operand term2 = mock(Operand.class);
		when(term2.getName()).thenReturn("l");
		final Operand term3 = mock(Operand.class);
		when(term3.getName()).thenReturn("r");
		Operand term4 = new Operand()
		{

			@Override
			public Object getValue() {
				return null;
			}

			@Override
			public Class<?> getValueClass() {
				return null;
			}

			@Override
			public void setName(String name) {
			}

			@Override
			public String getName() {
				return NAME + 1;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean backup(int id) {
				return false;
			}

			@Override
			public int unifyTerm(Term otherTerm, int id) {
				return 0;
			}

			@Override
			public EvaluationStatus evaluate(int id) {
				return EvaluationStatus.COMPLETE;
			}

			@Override
			public OperatorEnum[] getRightOperandOps() {
				return null;
			}

			@Override
			public OperatorEnum[] getLeftOperandOps() {
				return null;
			}

			 @Override
		     public OperatorEnum[] getStringOperandOps()
		     {
			     return Operand.EMPTY_OPERAND_OPS;
		     }

			@Override
			public Number numberEvaluation(OperatorEnum operatorEnum2,
					Term rightTerm) {
				return null;
			}

			@Override
			public Number numberEvaluation(Term leftTerm,
					OperatorEnum operatorEnum2, Term rightTerm) {
				return null;
			}

			@Override
			public Boolean booleanEvaluation(Term leftTerm,
					OperatorEnum operatorEnum2, Term rightTerm) {
				return null;
			}

			@Override
			public void assign(Term term) {
			}

			@Override
			public Operand getLeftOperand() {
				return term2;
			}

			@Override
			public Operand getRightOperand() {
				return term3;
			}

            @Override
            public int getId()
            {
                return 0;
            }
			
            @Override
            public QualifiedName getQualifiedName()
            {
                return QualifiedName.parseName(getName());
            }

            @Override
            public void setValue(Object value)
            {
            }

            @Override
            public void clearValue()
            {
            }

            @Override
            public void setPrivate(boolean isPrivate)
            {
            }

            @Override
            public boolean isPrivate()
            {
                return false;
            }

            @Override
            public void setTrait(Trait trait)
            {
            }

            @Override
            public Trait getTrait()
            {
                return null;
            }
		};
		testStructure.addTerm(term4);
		assertThat(testStructure.getTermCount()).isEqualTo(2);
		assertThat(testStructure.termMap.size()).isEqualTo(3);
		assertThat(testStructure.getTermByName("l")).isEqualTo(term2);
		assertThat(testStructure.getTermByName("r")).isEqualTo(term3);
		assertThat(testStructure.getTermByName(NAME.toLowerCase() + 1)).isEqualTo(term4);
		final Operand term5 = mock(Operand.class);
		when(term5.getName()).thenReturn(Term.ANONYMOUS);
		Operand term6 = new Operand()
		{

			@Override
			public Object getValue() {
				return null;
			}

			@Override
			public Class<?> getValueClass() {
				return null;
			}

			@Override
			public void setName(String name) {
			}

			@Override
			public String getName() {
				return NAME + 2;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public boolean backup(int id) {
				return false;
			}

			@Override
			public int unifyTerm(Term otherTerm, int id) {
				return 0;
			}

			@Override
			public EvaluationStatus evaluate(int id) {
				return EvaluationStatus.COMPLETE;
			}

			@Override
			public OperatorEnum[] getRightOperandOps() {
				return null;
			}

			@Override
			public OperatorEnum[] getLeftOperandOps() {
				return null;
			}

			@Override
		    public OperatorEnum[] getStringOperandOps()
		    {
			    return Operand.EMPTY_OPERAND_OPS;
		    }
	
			@Override
			public Number numberEvaluation(OperatorEnum operatorEnum2,
					Term rightTerm) {
				return null;
			}

			@Override
			public Number numberEvaluation(Term leftTerm,
					OperatorEnum operatorEnum2, Term rightTerm) {
				return null;
			}

			@Override
			public Boolean booleanEvaluation(Term leftTerm,
					OperatorEnum operatorEnum2, Term rightTerm) {
				return null;
			}

			@Override
			public void assign(Term term) {
			}

			@Override
			public Operand getLeftOperand() {
				return term5;
			}

			@Override
			public Operand getRightOperand() {
				return null;
			}

            @Override
            public int getId()
            {
                return 0;
            }

            @Override
            public QualifiedName getQualifiedName()
            {
                return QualifiedName.parseName(getName());
            }

            @Override
            public void setValue(Object value)
            {
            }

            @Override
            public void clearValue()
            {
            }

            @Override
            public void setPrivate(boolean isPrivate)
            {
            }

            @Override
            public boolean isPrivate()
            {
                return false;
            }

            @Override
            public void setTrait(Trait trait)
            {
            }

            @Override
            public Trait getTrait()
            {
                return null;
            }
			
		};
		testStructure.addTerm(term6);
		assertThat(testStructure.getTermCount()).isEqualTo(3);
		assertThat(testStructure.termMap.size()).isEqualTo(4);
		assertThat(testStructure.getTermByName(NAME + 2)).isEqualTo(term6);
	}
}

