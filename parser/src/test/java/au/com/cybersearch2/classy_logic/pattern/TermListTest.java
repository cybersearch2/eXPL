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

import org.junit.Ignore;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.pattern.TermList;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

/**
 * TermListTest
 * @author Andrew Bowley
 * 17 Nov 2014
 */
public class TermListTest 
{
    /*
	@SuppressWarnings("serial")
    static class TestTermList extends TermList
	{
		public TestTermList(String name)
		{
			super(name);
		}

		public TestTermList(String name, List<Term> terms) 
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
		TermList testTermList = new TermList(NAME, values);
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "(One, 2, true)");
	}

	@Test
	public void testNamedConstructorWithEmptyValues()
	{
		TermList testTermList = new TermList(NAME, new Object[0]);
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "()");
	}

	@Test
	public void testNamedConstructorWithNullObjectsArray()
	{
		try
		{
		    new TermList(NAME, (Object[])null);
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
		TermList testTermList = new TermList(NAME, terms);
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "(one=1, two=2, three=3)");
		assertThat(testTermList.getTermByName("one")).isEqualTo(terms[0]);
		assertThat(testTermList.getTermByName("two")).isEqualTo(terms[1]);
		assertThat(testTermList.getTermByName("three")).isEqualTo(terms[2]);
	}

	@Test
	public void testNamedConstructorWithEmptyTermsArray()
	{
		TermList testTermList = new TermList(NAME, new Parameter[0]);
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "()");
	}

	@Test
	public void testNamedConstructorWithObjectArray()
	{
		Object[] objectArray = new Object[]
		{
			new Integer(23), new Parameter("Test", "Value")
		};
		TermList testTermList = new TermList(NAME, objectArray);
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "(23, Test=Value)");
	}

	@Test
	public void testNamedConstructor()
	{
		try
		{
		    new TestTermList(null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null or empty");
		}
		try
		{
		    new TestTermList("");
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
		    new TermList(NAME, (Parameter[])null);
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
		TermList testTermList = new TermList(NAME, paramList);
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "(one=1, two=2, three=3)");
		assertThat(testTermList.getTermByName("one")).isEqualTo(paramList.get(0));
		assertThat(testTermList.getTermByName("two")).isEqualTo(paramList.get(1));
		assertThat(testTermList.getTermByName("three")).isEqualTo(paramList.get(2));
	}

	@Test
	public void testNamedConstructorWithEmptyTermsList()
	{
		TermList testTermList = new TermList(NAME, new ArrayList<Term>());
		assertThat(testTermList.getName()).isEqualTo(NAME);
		assertThat(testTermList.isFact()).isTrue();
		assertThat(testTermList.toString()).isEqualTo(NAME + "()");
	}

	@Test
	public void testNamedConstructorWithNullTermsList()
	{
		try
		{
		    new TermList(NAME, (List<Term>)null);
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
		TermList testTermList = new TermList(NAME, new ArrayList<Term>());
		assertThat(testTermList.getTermByName("")).isNull();
		assertThat(testTermList.getTermByName(null)).isNull();
		List<Term> paramList = getTermsList();
		testTermList = new TermList(NAME, paramList);
		assertThat(testTermList.getTermByName("one")).isEqualTo(paramList.get(0));
		assertThat(testTermList.getTermByName("two")).isEqualTo(paramList.get(1));
		assertThat(testTermList.getTermByName("three")).isEqualTo(paramList.get(2));
		assertThat(testTermList.getTermByName("ONE")).isEqualTo(paramList.get(0));
		assertThat(testTermList.getTermByName("Two")).isEqualTo(paramList.get(1));
		assertThat(testTermList.getTermByName("THRee")).isEqualTo(paramList.get(2));
		assertThat(testTermList.getTermByName("four")).isNull();
		try
		{
			testTermList.getTermByName(null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null");
		}
		TestTermList testTermList2 = new TestTermList(NAME);
		assertThat(testTermList2.getTermByName("")).isNull();
		assertThat(testTermList2.getTermByName(null)).isNull();
	}

	@Test
	public void test_getTermByIndex()
	{
		TermList testTermList = new TermList(NAME, new ArrayList<Term>());
		assertThat(testTermList.getTermByIndex(-1)).isNull();
		assertThat(testTermList.getTermByIndex(0)).isNull();
		List<Term> paramList = getTermsList();
		testTermList = new TermList(NAME, paramList);
		assertThat(testTermList.getTermCount()).isEqualTo(3);
		assertThat(testTermList.getTermByIndex(0)).isEqualTo(paramList.get(0));
		assertThat(testTermList.getTermByIndex(1)).isEqualTo(paramList.get(1));
		assertThat(testTermList.getTermByIndex(2)).isEqualTo(paramList.get(2));
		assertThat(testTermList.getTermByIndex(-1)).isNull();
		assertThat(testTermList.getTermByIndex(3)).isNull();
		TestTermList testTermList2 = new TestTermList(NAME);
		assertThat(testTermList2.getTermByIndex(0)).isNull();
		assertThat(testTermList2.getTermByIndex(-1)).isNull();
		assertThat(testTermList2.getTermCount()).isEqualTo(0);
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
		TermList testTermList = new TermList(NAME, paramList);
		assertThat(testTermList.isFact()).isFalse();
		when(parameter1.isEmpty()).thenReturn(false);
		when(parameter2.isEmpty()).thenReturn(true);
		assertThat(testTermList.isFact()).isFalse();
		when(parameter1.isEmpty()).thenReturn(false);
		when(parameter2.isEmpty()).thenReturn(false);
		assertThat(testTermList.isFact()).isTrue();
		TermList testTermList2 = new TermList(NAME, new ArrayList<Term>());
		assertThat(testTermList2.isFact()).isTrue();
	}
	
	@Test
	public void test_getName_Pattern()
	{
		TermList testTermList = new TermList(NAME, new ArrayList<Term>());
		Pattern namePattern = testTermList.getNamePattern();
		Matcher matcher = namePattern.matcher("one.two");
		assertThat(matcher.find()).isTrue();
		assertThat(matcher.group(1)).isEqualTo("one");
		assertThat(matcher.group(2)).isEqualTo("two");
	}
	
    @Ignore // Test fails if template add term does not do operand map
	@Test
	public void test_addTerm()
	{
		TestTermList testTermList = new TestTermList(NAME);
		Term term1 = mock(Term.class);
		when(term1.getName()).thenReturn(Term.ANONYMOUS);
		testTermList.addTerm(term1);
		assertThat(testTermList.getTermCount()).isEqualTo(1);
		assertThat(testTermList.termMap.size()).isEqualTo(0);
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
            public Operator getOperator()
            {
                return null;
            }

            @Override
            public void setArchetypeIndex(int index)
            {
            }

            @Override
            public int getArchetypeIndex()
            {
                return 0;
            }
		};
		testTermList.addTerm(term4);
		assertThat(testTermList.getTermCount()).isEqualTo(2);
		assertThat(testTermList.getTermByName("l")).isEqualTo(term2);
		assertThat(testTermList.getTermByName("r")).isEqualTo(term3);
		assertThat(testTermList.getTermByName(NAME.toLowerCase() + 1)).isEqualTo(term4);
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
            public Operator getOperator()
            {
                return null;
            }

            @Override
            public void setArchetypeIndex(int index)
            {
            }

            @Override
            public int getArchetypeIndex()
            {
                return 0;
            }

		};
		testTermList.addTerm(term6);
		assertThat(testTermList.getTermCount()).isEqualTo(3);
		assertThat(testTermList.getTermByName(NAME + 2)).isEqualTo(term6);
	}
	*/
}

