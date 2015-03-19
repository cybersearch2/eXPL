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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

/**
 * AxiomTest
 * @author Andrew Bowley
 * 30 Nov 2014
 */
public class AxiomTest 
{
	final static String NAME = "myAxiom";
	final static String TERM_NAME = "MyTerm";

	private List<Axiom> charges;
	private List<Axiom> customers;
	private Solution solution;
	
	@Before
	public void before() throws Exception
	{
		solution = new Solution();
		charges = new ArrayList<Axiom>();
		charges.add(new Axiom("charge", "athens", 23));
		charges.add(new Axiom("charge", "sparta", 13));
		charges.add(new Axiom("charge", "milos", 17));
		
		customers = new ArrayList<Axiom>();
		customers.add(new Axiom("customer", new Parameter("name", "Marathon Marble"), new Parameter("city", "sparta")));
		customers.add(new Axiom("customer", new Parameter("name", "Acropolis Construction"), new Parameter("city", "athens")));
		customers.add(new Axiom("customer", new Parameter("name", "Agora Imports"), new Parameter("city","sparta")));
	}

	protected Template createChargeUnificationTarget(String name)
	{
	    StringOperand city = new StringOperand("city");
	    StringOperand fee = new StringOperand("fee");
		return new Template(name, city, fee);
	}
	
	protected Template createCustomerUnificationTarget()
	{
	    StringOperand name = new StringOperand("name");
	    StringOperand city = new StringOperand("city");
		return new Template("customer", name, city);
	}

    @Test
	public void test_constructor_name()
	{
		try
		{
		    new Axiom((String)null);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null or empty");
		}
		try
		{
		    new Axiom("");
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		}
		catch(IllegalArgumentException e)
		{
			assertThat(e.getMessage()).isEqualTo("Parameter \"name\" is null or empty");
		}
	}
    
    @Test
	public void test_constructor_template()
	{
    	String TEMPLATE_NAME = "myTemplate";
    	String TEMPLATE_KEY = "templateKey";
    	Template template = new Template(TEMPLATE_KEY, TEMPLATE_NAME);
    	Parameter term = new Parameter("x", new Integer(2));
    	template.addTerm(term);
    	Axiom axiom = template.toAxiom();
    	assertThat(axiom.getName()).isEqualTo(TEMPLATE_NAME);
    	assertThat(axiom.getTermCount()).isEqualTo(1);
    	assertThat(axiom.getTermByName("x")).isEqualTo(term);
	}
    
   @Test
	public void test_unification_by_position()
	{
	   assertThat(charges.get(0).pairByPosition).isTrue();
	    Template s1 = createChargeUnificationTarget("charge");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(0);
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(0);
        
	    charges.get(0).unifyTemplate(s1, mock(Solution.class));
        assertThat(s1.toString()).isEqualTo("charge(city = athens, fee = 23)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
        
        s1 = createChargeUnificationTarget("charge");
	    charges.get(1).unifyTemplate(s1, mock(Solution.class));
        assertThat(s1.toString()).isEqualTo("charge(city = sparta, fee = 13)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
        
        s1.backup(false);
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(0);
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(0);
        
	    charges.get(1).unifyTemplate(s1, mock(Solution.class));
        assertThat(s1.toString()).isEqualTo("charge(city = sparta, fee = 13)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
        
        s1 = createChargeUnificationTarget("fee");
	    charges.get(0).unifyTemplate(s1, mock(Solution.class));
        assertThat(s1.toString()).isEqualTo("fee(city = athens, fee = 23)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());

        s1 = createChargeUnificationTarget("charge");
        Parameter athens = new Parameter("city", "athens");
        s1.termList.get(0).unifyTerm(athens, 1);
	    charges.get(1).unifyTemplate(s1, mock(Solution.class));
        assertThat(s1.toString()).isEqualTo("charge(city = athens, fee)");

        Template s2 = createCustomerUnificationTarget();
        customers.get(0).unifyTemplate(s2, mock(Solution.class));
        assertThat(s2.toString()).isEqualTo("customer(name = Marathon Marble, city = sparta)");
        assertThat(((Parameter)s2.termList.get(0)).getId()).isEqualTo(s2.getId());
        assertThat(((Parameter)s2.termList.get(1)).getId()).isEqualTo(s2.getId());
        // Test unmatched axiom terms are ignored
        Object[] terms = new Object[]
		{
        	"athens",  23, new Integer(65467)
		};
        Axiom tooBig = new Axiom("charge", terms);
        s1 = createChargeUnificationTarget("fee");
        tooBig.unifyTemplate(s1, mock(Solution.class));
        assertThat(s1.toString()).isEqualTo("fee(city = athens, fee = 23)");
	}
    
    @Test 
    public void test_unification_by_parameter_name()
    {
    	List<Axiom> namedCharges = new ArrayList<Axiom>();
		namedCharges.add(new Axiom("charge", new Parameter("city","athens"), new Parameter("fee", 23)));
		namedCharges.add(new Axiom("charge", new Parameter("city","sparta"), new Parameter("fee", 13)));
		namedCharges.add(new Axiom("charge", new Parameter("city","milos"), new Parameter("fee", 17)));
		assertThat(namedCharges.get(0).pairByPosition).isFalse();
	    Template s1 = createChargeUnificationTarget("charge");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(0);
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(0);
        
        namedCharges.get(0).unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("charge(city = athens, fee = 23)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
        
        s1 = createChargeUnificationTarget("charge");
        namedCharges.get(1).unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("charge(city = sparta, fee = 13)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
        
        s1.backup(false);
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(0);
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(0);
        
        namedCharges.get(1).unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("charge(city = sparta, fee = 13)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
        
        s1 = createChargeUnificationTarget("fee");
        namedCharges.get(0).unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("fee(city = athens, fee = 23)");
        assertThat(((Parameter)s1.termList.get(0)).getId()).isEqualTo(s1.getId());
        assertThat(((Parameter)s1.termList.get(1)).getId()).isEqualTo(s1.getId());
    	
        Object[] terms = new Object[]
		{
        	new Parameter("city","athens"), new Parameter("fee", 23), new Integer(65467)
		};
        Axiom tooBig = new Axiom("charge", terms);
        s1 = createChargeUnificationTarget("charge");
        tooBig.unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("charge(city = athens, fee = 23)");
        terms = new Object[]
		{
        	new Parameter("fee", 23), new Integer(65467), new Parameter("city","athens")
		};
        tooBig = new Axiom("charge", terms);
        s1 = createChargeUnificationTarget("charge");
        tooBig.unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("charge(city = athens, fee = 23)");
        terms = new Object[]
		{
        	new Parameter("fee", 23), new Integer(65467), "athens"
		};
        tooBig = new Axiom("charge", terms);
        s1 = createChargeUnificationTarget("charge");
        tooBig.unifyTemplate(s1, solution);
        assertThat(s1.toString()).isEqualTo("charge(city, fee = 23)");
    }

    @Test 
    public void test_unification_AxiomPairer()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("term1");
        Term term2 = mock(Term.class);
        when(term2.getName()).thenReturn("term2");   
        testAxiom.addTerm(term1);
		assertThat(testAxiom.pairByPosition).isFalse();
        testAxiom.addTerm(term2);
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(true);
        List<TermPair> axiomPairs = new ArrayList<TermPair>(2);
        axiomPairs.add(new TermPair(term1, operand1));
        axiomPairs.add(new TermPair(term2, operand2));
		when(axiomPairer.getPairList()).thenReturn(axiomPairs );
        assertThat(testAxiom.unifyTemplate(template, new Solution())).isTrue();
        verify(term1).unifyTerm(operand1, template.getId());
        verify(term2).unifyTerm(operand2, template.getId());
        verify(axiomPairer).reset();
    }

    @Test 
    public void test_unification_AxiomPairer_by_position()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("");
        Term term2 = mock(Term.class);
        when(term2.getName()).thenReturn("");   
        testAxiom.addTerm(term1);
        testAxiom.addTerm(term2);
		assertThat(testAxiom.pairByPosition).isTrue();
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(true);
        when(axiomPairer.pairTerms(isA(Term.class), isA(Term.class))).thenReturn(true);
        when(axiomPairer.parseKeyName(isA(String.class))).thenReturn(new KeyName("", "term1"), new KeyName("", "term2"));
        List<TermPair> axiomPairs = new ArrayList<TermPair>(2);
        axiomPairs.add(new TermPair(term1, operand1));
        axiomPairs.add(new TermPair(term2, operand2));
		when(axiomPairer.getPairList()).thenReturn(axiomPairs );
        assertThat(testAxiom.unifyTemplate(template, new Solution())).isTrue();
        verify(term1).unifyTerm(operand1, template.getId());
        verify(term2).unifyTerm(operand2, template.getId());
        ArgumentCaptor<String> termName = ArgumentCaptor.forClass(String.class);
        verify(axiomPairer, times(2)).parseKeyName(termName.capture());
        assertThat(termName.getAllValues()).containsSequence("term1", "term2");
    }

    @Test 
    public void test_unification_AxiomPairer_by_position_overlap()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("");
         testAxiom.addTerm(term1);
		assertThat(testAxiom.pairByPosition).isTrue();
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(true);
        when(axiomPairer.pairTerms(isA(Term.class), isA(Term.class))).thenReturn(true);
        when(axiomPairer.parseKeyName(isA(String.class))).thenReturn(new KeyName("", "term1"), new KeyName("", "term2"));
        List<TermPair> axiomPairs = new ArrayList<TermPair>(2);
        axiomPairs.add(new TermPair(term1, operand1));
 		when(axiomPairer.getPairList()).thenReturn(axiomPairs );
        assertThat(testAxiom.unifyTemplate(template, new Solution())).isTrue();
        verify(term1).unifyTerm(operand1, template.getId());
    }

    @Test 
    public void test_unification_AxiomPairer_by_position_exit1()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("");
        Term term2 = mock(Term.class);
        when(term2.getName()).thenReturn("");   
        testAxiom.addTerm(term1);
        testAxiom.addTerm(term2);
		assertThat(testAxiom.pairByPosition).isTrue();
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(false);
         assertThat(testAxiom.unifyTemplate(template, new Solution())).isFalse();
        verify(term1, never()).unifyTerm(operand1, template.getId());
        verify(term2, never()).unifyTerm(operand2, template.getId());
    }

    @Test 
    public void test_unification_AxiomPairer_by_position_exit2()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("");
        Term term2 = mock(Term.class);
        when(term2.getName()).thenReturn("");   
        testAxiom.addTerm(term1);
        testAxiom.addTerm(term2);
		assertThat(testAxiom.pairByPosition).isTrue();
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(true);
        when(axiomPairer.pairTerms(isA(Term.class), isA(Term.class))).thenReturn(false);
        assertThat(testAxiom.unifyTemplate(template, new Solution())).isFalse();
        verify(term1, never()).unifyTerm(operand1, template.getId());
        verify(term2, never()).unifyTerm(operand2, template.getId());
    }

   @Test 
    public void test_unification_AxiomPairer_exit()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("term1");
        Term term2 = mock(Term.class);
        when(term2.getName()).thenReturn("term2");   
        testAxiom.addTerm(term1);
        testAxiom.addTerm(term2);
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(false);
        assertThat(testAxiom.unifyTemplate(template, new Solution())).isFalse();
        verify(term1, never()).unifyTerm(operand1, template.getId());
        verify(term2, never()).unifyTerm(operand2, template.getId());
    }

    @Test 
    public void test_unification_SolutionPairer()
    {
        Axiom testAxiom = new Axiom(NAME);
		AxiomPairer axiomPairer = mock(AxiomPairer.class);
		testAxiom.axiomPairer = axiomPairer;
		SolutionPairer solutionPairer = mock(SolutionPairer.class);
		testAxiom.solutionPairer = solutionPairer;
        Term term0 = mock(Term.class);
        when(term0.getName()).thenReturn("term0");  
        testAxiom.addTerm(term0);
        Term term1 = mock(Term.class);
        when(term1.getName()).thenReturn("term1");
        Term term2 = mock(Term.class);
        when(term2.getName()).thenReturn("term2");   
        Template template = new Template(NAME, "Template");
        Operand operand1 = mock(Operand.class);
        when(operand1.getName()).thenReturn("term1");
        when(operand1.getLeftOperand()).thenReturn(null);
        when(operand1.getRightOperand()).thenReturn(null);
        Operand operand2 = mock(Operand.class);
        when(operand2.getName()).thenReturn("term2");
        when(operand2.getLeftOperand()).thenReturn(null);
        when(operand2.getRightOperand()).thenReturn(null);
        template.addTerm(operand1);
        template.addTerm(operand2);
        when(axiomPairer.next(isA(Operand.class), eq(1))).thenReturn(true);
		when(axiomPairer.getPairList()).thenReturn(new ArrayList<TermPair>());
        when(solutionPairer.next(isA(Operand.class), eq(1))).thenReturn(true);
        List<TermPair> axiomPairs = new ArrayList<TermPair>(2);
        axiomPairs.add(new TermPair(term1, operand1));
        axiomPairs.add(new TermPair(term2, operand2));
		when(solutionPairer.getPairList()).thenReturn(axiomPairs );
		Solution solution = new Solution();
		solution.put(NAME + 1, new Axiom(NAME + 1));
        testAxiom.unifyTemplate(template, solution);
        verify(term1).unifyTerm(operand1, template.getId());
        verify(term2).unifyTerm(operand2, template.getId());
        when(solutionPairer.next(isA(Operand.class), eq(1))).thenReturn(false);
        assertThat(testAxiom.unifyTemplate(template, solution)).isFalse();
    }
    
    @Test 
    public void test_addTerm_namelist()
    {
    	Parameter term1 = new Parameter(Term.ANONYMOUS, new Integer(2));
    	Parameter term2 = new Parameter(Term.ANONYMOUS, new Integer(99));
    	List<String> nameList = new ArrayList<String>();
    	nameList.add("x");
    	nameList.add("y");
    	Axiom axiom = new Axiom(NAME);
    	axiom.addTerm(term1, nameList);
       	axiom.addTerm(term2, nameList);
       	assertThat(axiom.getTermCount()).isEqualTo(2);
       	assertThat(axiom.getTermByName("x")).isEqualTo(term1);
       	assertThat(axiom.getTermByName("y")).isEqualTo(term2);
    	Parameter term3 = new Parameter(Term.ANONYMOUS, new Integer(41));
       	axiom.addTerm(term3, nameList);
       	assertThat(axiom.getTermCount()).isEqualTo(3);
   }
}
