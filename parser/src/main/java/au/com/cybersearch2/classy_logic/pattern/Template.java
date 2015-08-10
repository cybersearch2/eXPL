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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import au.com.cybersearch2.classy_logic.expression.AxiomOperand;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.NameParser;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;


/**
 * Template
 * Unifies with Axioms with same name. Each term is matched on name at each unification iteration.
 * Every Template has a unique ID to facilitate partial backup. 
 * @author Andrew Bowley
 * 30 Nov 2014
 */
public class Template extends Structure 
{
	public static final String ITERABLE = "iterable";
    public static final String TERMNAMES = "termnames";
    public static List<String>  EMPTY_NAMES_LIST;
    public static Iterable<AxiomTermList> EMPTY_ITERABLE;
  
    static
    {
        EMPTY_ITERABLE = new Iterable<AxiomTermList>(){

            @Override
            public Iterator<AxiomTermList> iterator()
            {
                return new Iterator<AxiomTermList>(){

                    @Override
                    public boolean hasNext()
                    {
                        return false;
                    }

                    @Override
                    public AxiomTermList next()
                    {
                        return null;
                    }

                    @Override
                    public void remove()
                    {
                    }};
            }};
        EMPTY_NAMES_LIST = Collections.emptyList();
    }
    /** Unique identity generator */
    static protected AtomicInteger referenceCount;
    /** Key to match with Axiom name for unification */
	protected String key;
    /** Identity used in backup to allow partial backup to last unifying agent */
    protected int id;
    /** Initialization data (optional) */
    protected Map<String, Object> initData;
    /** Link to next Template in chain. Used by Calculator. */
    protected Template next;
    /** Flag true if template declared a calculator */
    protected boolean isCalculator;
    /** Flag true if template used to make selection. Used by Calculator. */
    protected boolean choice;
    /** Calculator solution as single AxiomList (optional) */
    protected AxiomOperand solutionTerm;
    
	static
	{
		referenceCount = new AtomicInteger();
	}
	

	/**
	 * Construct Template object
	 * @param name Template name and also, by default, axiom key 
	 */
	public Template(String name) 
	{
		super(name);
		this.key = name;
		id = referenceCount.incrementAndGet();
	}

	/**
	 * Construct Template object
	 * @param key Axiom name to unify with 
	 * @param name Template name
	 */
	public Template(String key, String name) 
	{
		super(name);
		this.key = key;
		id = referenceCount.incrementAndGet();
	}

	/**
	 * Construct Template object
	 * @param name Template name
	 * @param termList One or more Variables
	 */
	public Template(String name, List<Term> termList) 
	{
		this(name);
		if (termList.size() == 0)
			throw new IllegalArgumentException("Parameter \"termList\" is empty");
		setTerms(termList);
	}

	/**
	 * Construct Template object
	 * @param key Axiom name to unify with 
	 * @param name Template name
	 * @param termList One or more Variables
	 */
	public Template(String key, String name, List<Term> termList) 
	{
		this(name, termList);
		this.key = key;
	}

	/**
	 * Construct Template object
	 * @param name Template name
	 * @param terms One or more Variables
	 */
	public Template(String name, Term... terms) 
	{
		this(name);
		if (terms.length== 0)
			throw new IllegalArgumentException("Parameter \"terms\" is empty");
		setTerms(terms);
	}

	/**
	 * Construct Template object with key different to name
	 * @param key Axiom name to unify with 
	 * @param name Template name
	 * @param terms One or more Variables
	 */
	public Template(String key, String name, Term... terms) 
	{
		this(name, terms);
		this.key = key;
	}

	/**
	 * Returns axiom key
	 * @return String
	 */
	public String getKey() 
	{
		return key;
	}

	/**
	 * Sets axiom key
	 * @param value
	 */
    public void setKey(String value)	
    {
    	key = value;
    }

	public boolean isCalculator()
    {
        return isCalculator;
    }

    public void setCalculator(boolean isCalculator)
    {
        this.isCalculator = isCalculator;
    }

    /**
	 * @return the choice
	 */
	public boolean isChoice() 
	{
		return choice;
	}

	/**
	 * @param choice the choice to set
	 */
	public void setChoice(boolean choice) 
	{
		this.choice = choice;
	}

	/**
	 * Returns identity of this structure
	 * @return int
	 */
	public int getId()
	{
		return id;
	}

	public boolean hasSolution()
	{
	    return solutionTerm != null;
	}
	
	/**
	 * Evaluate Terms of this Template
	 * @return EvaluationStatus
	 */
	public EvaluationStatus evaluate()
	{
		if (!termList.isEmpty())
		{
			for (Term term: termList)
			{
				EvaluationStatus evaluationStatus = term.evaluate(id);
				if (evaluationStatus != EvaluationStatus.COMPLETE)
					return evaluationStatus;
			}
		}
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * Evaluate Terms of this Template until status COMPLET is returned 
	 * @return Position of first term to return status COMPLETE
	 */
	public int select()
	{
		if (!termList.isEmpty())
		{
			int position = 0;
			for (Term term: termList)
			{
				if (term.evaluate(id) == EvaluationStatus.COMPLETE)
					return position;
				++position;
			}
		}
		return Choice.NO_MATCH;
	}

	/**
	 * Backup from last unification.
	 * @param partial Flag to indicate backup to before previous unification or backup to start
	 * @return Flag to indicate if this Structure is ready to continue unification
	 * @see Axiom#unifyTemplate(Template other, Solution solution)
	 */
	public boolean backup(boolean partial)
	{
		boolean isMutable = false;
		if (!termList.isEmpty())
			for (Term term: termList)
			{
				boolean backupPerformed = (partial ? term.backup(id) : term.backup(0));
				//System.out.println("Backup " + id + " " + backupPerformed);
				if (backupPerformed)
					isMutable = true;
			}
		return isMutable;
	}

    /**
     * Exposes super addTerm()
     * @param term Term object
     * @param isSolution 
     */
    public void addTerm(Term term, boolean isSolution)
    {
        super.addTerm(term);
        if (isSolution)
            solutionTerm = (AxiomOperand)term;
    }

    public AxiomList getSolution()
    {
        if ((solutionTerm != null) && !solutionTerm.isEmpty())
            return solutionTerm.getValue();
        throw new ExpressionException("Template \"" + name + "\" cannot provide solution");
    }
    
	/**
	 * Returns an axiom containing the values of this template and 
	 * having same key and name as this template's name.
	 * @return Axiom
	 */
	public Axiom toAxiom()
	{
        Axiom axiom = new Axiom(name);
		for (Term term: termList)
		{
			if (!term.isEmpty() && !term.getName().isEmpty())
			{
				String termName = NameParser.getNamePart(term.getName());
				Parameter param = new Parameter(termName, term.getValue());
				axiom.addTerm(param);
			}
		}
		return axiom;
	}

	/**
	 * Returns an OperandWalker object for navigating this template
	 * @return OperandWalker 
	 */
	public OperandWalker getOperandWalker()
	{
	    if (!termList.isEmpty())
		    return new OperandWalker(termList);
	    return new OperandWalker(EMPTY_TERM_LIST);
	}

	/**
	 * For each term, clear the value and set as empty
	 */
	public void reset() 
	{
		if (!termList.isEmpty())
			for (Term term: termList)
				((Parameter)term).clearValue();
	}

	/**
	 * Set initialization data, used for seeding calculations
	 * @param name Property name
	 * @param value Property value
	 */
	public void putInitData(String name, Object value)
	{
		if (initData == null)
			initData = new HashMap<String, Object> ();
		initData.put(name, value);
	}

	/**
	 * Set term initial data. 
	 */
	public void initialize()
	{
		if (initData != null)
		{
			for (String name: initData.keySet())
			{
				Term term = getTermByName(name);
				if (term == null)
					throw new QueryExecutionException("Template \"" + getName() + "\" does not have term \"" + name + "\"");
				term.assign(initData.get(name));
			}
		}
	}

	/**
	 * Set initialization data, used for seeding calculations
	 * @param properties Initialization properties
	 */
	public void addProperties(Map<String, Object> properties) 
	{
		if (initData == null)
			initData = properties;
		else
			initData.putAll(properties);
	}

	/**
	 * @return the next
	 */
	public Template getNext() 
	{
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(Template next) 
	{
		this.next = next;
	}

	/**
	 * Returns properties
	 * @return Map container or null if no properties
	 */
    public Map<String, Object> getProperties()
    {
        return initData;
    }
}
