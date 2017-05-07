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

//import java.io.ObjectStreamField;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermPairList;
import au.com.cybersearch2.classy_logic.pattern.TermPair;
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
public class Template extends TermList<Operand> implements TermPairList
{
	//private static final long serialVersionUID = -3549624322416667887L;
    //private static final ObjectStreamField[] serialPersistentFields =
    //{   // Template inherits Serializable from Structure but
        //    should not be serialized. Two fields defined for tracing purposes.
   //     new ObjectStreamField("name", String.class),
   //     new ObjectStreamField("id", Integer.class)
   // };
    
    public static List<String>  EMPTY_NAMES_LIST;
    /** Unique identity generator */
    static protected AtomicInteger referenceCount;
    public static List<Operand> EMPTY_OPERAND_LIST;

    static
    {
        referenceCount = new AtomicInteger();
        EMPTY_OPERAND_LIST = Collections.emptyList();
        EMPTY_NAMES_LIST = Collections.emptyList();
    }
    /** Qualified name of operand */
    protected QualifiedName qname;
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
    protected boolean isChoice;
    /** Flag true if inner template */
    protected boolean isInnerTemplate;
    /** Flag true if replicate. Required because terms are re-cycled instead of preserved after evaluation. */
    protected boolean isReplicate;
    /** Head of call stack */
    protected CallContext headCallContext;
    /** Tail of call stack */
    protected CallContext tailCallContext;
    /** Pairs axiom terms in a Solution object with terms in a template */
    protected SolutionPairer solutionPairer;
    /** Pairs axiom terms in this axiom with terms in a template */
    protected AxiomPairer axiomPairer;
    /** Head of TermPair list */
    private TermPair termPairHead;
    /** List TermPair tail */
    private TermPair termPairTail;
    /** Head of free TermPair list */
    private TermPair freeTermPairHead;
   
    /**
     * Construct a replicate Template object. The new template has a unique id and specified qualified name 
     * @param master Template object to replicate
     * @param qname New Template qualified name. 
     */
    public Template(Template master, QualifiedName qname) 
    {
        this((TemplateArchetype) master.getArchetype());
        this.qname = qname;
        key = master.key;
        this.isCalculator = master.isCalculator;
        this.isChoice = master.isChoice;
        this.isInnerTemplate = master.isInnerTemplate;
        isReplicate = true;
        for (Operand operand: master.termList)
            termList.add(operand);
    }

	/**
	 * Construct Template object
	 * @param qname Template qualified name. The axiom key is set to the name part as a default.
	 */
	public Template(TemplateArchetype templateArchetype) 
	{
		super(templateArchetype);
		qname = templateArchetype.getQualifiedName();
		key = qname.getTemplate();
		id = referenceCount.incrementAndGet();
		axiomPairer = new AxiomPairer(this);
	}

	/**
	 * Construct Template object
	 * @param key Axiom name to unify with 
	 * @param qname Template qualified name
	 */
	public Template(String key, TemplateArchetype templateArchetype) 
	{
	    this(templateArchetype);
		this.key = key;
	}

	/**
	 * Construct Template object
	 * @param qname Template qualifed name
	 * @param termList One or more Variables
	 */
	public Template(TemplateArchetype templateArchetype, List<Operand> terms) 
	{
		this(templateArchetype);
		templateArchetype.clearMutable();
		if (terms.size() == 0)
			throw new IllegalArgumentException("Parameter \"terms\" is empty");
        if ((terms != null)&& (terms.size() > 0))
        {
            for (Operand term: terms)
                addTerm(term);
        }
	}

	/**
	 * Construct Template object
	 * @param key Axiom name to unify with 
     * @param qname Template qualified name
	 * @param termList One or more Variables
	 */
	public Template(String key, TemplateArchetype templateArchetype, List<Operand> termList) 
	{
		this(templateArchetype, termList);
		this.key = key;
	}

	/**
	 * Construct Template object
     * @param qname Template qualified name
	 * @param terms One or more Variables
	 */
	public Template(TemplateArchetype templateArchetype, Operand... terms) 
	{
		this(templateArchetype);
        if ((terms != null)&& (terms.length > 0))
        {
            if (archetype.isMutable())
                setTerms(terms);
            for (Operand term: terms)
                addTerm(term);
        }
        else
            throw new IllegalArgumentException("Parameter \"terms\" is empty");
	}

	/**
	 * Construct Template object with key different to name
	 * @param key Axiom name to unify with 
     * @param qname Template qualified name
	 * @param terms One or more Variables
	 */
	public Template(String key, TemplateArchetype templateArchetype, Operand... terms) 
	{
		this(templateArchetype, terms);
		this.key = key;
	}

    /**
     * Returns the name of the Structure    
     * @return String
     */
    public String getName() 
    {
        return qname.getTemplate();
    }

	/**
	 * Returns key to match with Axiom name for unification
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

    /**
     * Returns flag set true if this object is used as a Calculator
     * @return boolean
     */
	public boolean isCalculator()
    {
        return isCalculator;
    }

	/**
	 * Sets flag to indicate this object is used as a Calculator
	 * @param isCalculator
	 */
    public void setCalculator(boolean isCalculator)
    {
        this.isCalculator = isCalculator;
    }

    /**
     * Returns flag set true if this object is used as a Choice
	 * @return boolean
	 */
	public boolean isChoice() 
	{
		return isChoice;
	}

	/**
	 * Set flag to indicate this object is used as a Choice
	 * @param isChoice Choice flag
	 */
	public void setChoice(boolean isChoice) 
	{
		this.isChoice = isChoice;
	}

	/**
	 * Returns flag set true if this is an inner template
	 * @return boolean
	 */
	public boolean isInnerTemplate()
    {
        return isInnerTemplate;
    }

	/**
	 * Set flag to indicate this is an inner template
	 * @param isInnerTemplate
	 */
    public void setInnerTemplate(boolean isInnerTemplate)
    {
        this.isInnerTemplate = isInnerTemplate;
    }

    
    public boolean isReplicate()
    {
        return isReplicate;
    }

    /**
	 * Returns identity of this structure
	 * @return int
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Returns template qualified name
	 * @return QualifiedName object
	 */
    public QualifiedName getQualifiedName()
    {
        return qname;
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
	 * Evaluate Terms of this Template until status COMPLETE is returned 
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
     * Add Operand term. Parameters not allowed.
     * @param operand Operand object
     */
    public void addTerm(Operand operand)
    {
        super.addTerm(operand);
    }

    /**
     * Unify given Axiom with this Template, pairing Terms of the Axiom with those
     * of this Template. Terms are matched by name except when all terms are anonymous, 
     * in which case, terms are paired in list order. 
     * When both terms of a pairing have a value and values do not match, unification is skipped. 
     * Unification of all terms is also skipped if this Axiom and the other Template have different names.
     * @param axiom Axiom with which to unify as TermList object 
     * @param solution Contains result of previous unify-evaluation steps
     * @return Flag set true if unification completed successfully
     */
    public boolean unify(TermList<Term> axiom, Solution solution)
    {
        OperandWalker walker = new OperandWalker(termList);
        clearTermPairList();
        // If term list is empty, unification will be restricted to solution only.
        if (!termList.isEmpty())
        {
            axiomPairer.setAxiom(axiom);
            if (axiom.getArchetype().isAnonymousTerms() && !matchTermsByPosition(axiom))
                return false;
            // Match by position results in axiom term names being set in the archetype.
            // Therefore a second pair matching takes place to apply named term logic.
            // Note that this is likey to create duplicate pairing which the TermPairList implementation
            // filters out.
            if (!walker.visitAllNodes(axiomPairer))
                return false;
        }
        // Solution has contents indicated by key set size non-zero
        if (solution.keySet().size() > 0)
        {
            setSolutionPairer(axiom, solution);
            if (!walker.visitAllNodes(solutionPairer))
                return false;
        }
        // Proceed with unification term by term
        TermPair termPair = termPairHead;
        while (termPair != null)
        {
            termPair.getTerm1().unifyTerm(termPair.getTerm2(), id);
            termPair = termPair.getNext();
        }
        return true;
    }

    /**
     * Prepare class solution handler for next term pairing operation
     * @param axiom Axiom with which to unify as TermList object 
     * @param solution Contains result of previous unify-evaluation steps
     */
    protected void setSolutionPairer(TermList<Term> axiom, Solution solution)
    {
        if (solutionPairer == null)
            solutionPairer = new SolutionPairer(this, solution);
        else
            solutionPairer.setSolution(solution);
        solutionPairer.setSolution(solution, qname, axiom);
    }

    /**
     * Match terms by position
     * @param axiom Axiom with which to unify as TermList object 
     * @return Flag set true if unification completed successfully
     */
    protected boolean matchTermsByPosition(TermList<Term> axiom)
    {
        int index = 0;
        for (Operand templateTerm: termList)
        {   // Match by position
            Term axiomTerm = axiom.getTermByIndex(index);
            // Do not go beyond last term in axiom
            if (index >= axiom.getTermCount())
                break;
            // Skip alreay-name terms
            if (!axiomTerm.getName().isEmpty())
            {
                index++;
                continue;
            }
            // Perform pairing
            if (!axiomPairer.pairTerms(templateTerm, axiomTerm))
                return false;
            // Name anonymous term as long as it is in the same name space as this template
            if (!templateTerm.getName().isEmpty())
            {
                boolean isLocalTerm = qname.inSameSpace(templateTerm.getQualifiedName());
                if (isLocalTerm)
                {   // Name axiom term for Operand navigation
                    axiomTerm.setName(templateTerm.getName());
                    axiom.getArchetype().changeName(index, templateTerm.getName());
                }
            }
            index++;
        }
        return true;
    }

    /**
	 * Returns an axiom containing the values of this template and 
	 * having same key and name as this template's name.
	 * @return Axiom
	 */
	public Axiom toAxiom()
	{
        Axiom axiom = new Axiom(qname.getTemplate());
		for (Term term: termList)
		{
		    Operand operand = (Operand)term;
            if (!operand.isEmpty() && !operand.isPrivate()  && 
			    (isReplicate || qname.inSameSpace(operand.getQualifiedName())))
			{
				Parameter param = new Parameter(operand.getName(), operand.getValue());
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
	    return new OperandWalker(EMPTY_OPERAND_LIST);
	}

	/**
	 * For each term, clear the value and set as empty
	 */
	public void reset() 
	{
	    Template template = this;
	    while (template != null)
	    {
    		if (!template.termList.isEmpty())
    			for (Term term: template.termList)
    			    term.backup(0);
    				//((Parameter)term).clearValue();
    		template = template.getNext();
	    }
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
			    /*
			    // TODO - Analyse qualified name code for validity
			    QualifiedName qualifiedTermName = QualifiedName.parseName(name, qname);
                Term term = getTermByName(qualifiedTermName.toString());
                if ((term == null) && (!qualifiedTermName.getTemplate().isEmpty()))
                {
                    qualifiedTermName.clearTemplate();
                    term = getTermByName(qualifiedTermName.toString());
                }
                if ((term == null) && (!qualifiedTermName.getScope().isEmpty()))
                {
                    qualifiedTermName.clearScope();
                    term = getTermByName(qualifiedTermName.toString());
                }
                */
                Term term = getTermByName(name);
				if (term != null)
				{
				    if (term instanceof Evaluator)
				    {
				        Operand left = ((Evaluator)term).getLeftOperand();
				        if ((left != null) && name.equals(left.getName()))
				            term = left;
				        else
				            term = null;
				    }
				}
				if (term == null)
				    throw new QueryExecutionException("Template \"" + getName() + "\" does not have term \"" + name + "\"");
				term.assign(new Parameter(Term.ANONYMOUS, initData.get(name)));
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
	 * Returns next template in chain
	 * @return Template object or null
	 */
	public Template getNext() 
	{
		return next;
	}

	/**
	 * Set new template object in chain
	 * @param nextTemplate Template object
	 */
	public void setNext(Template nextTemplate) 
	{
	    Template template = this;
        // Add inner template to outer template so it will be included in unification
        while (template.getNext() != null)
            template = template.getNext();
        template.next = nextTemplate;
	}

	/**
	 * Returns properties
	 * @return Map container or null if no properties
	 */
    public Map<String, Object> getProperties()
    {
        return initData;
    }

    /**
     * Push template terms on stack
     */
    public void push()
    {
        CallContext newCallContext = new CallContext(this);
        if (headCallContext == null)
        {
            headCallContext = newCallContext;
            tailCallContext = newCallContext;
        }
        else
        {
            tailCallContext.setNext(newCallContext);
            tailCallContext = newCallContext;
        }
        Template template = getNext();
        while (template != null)
        {
            template.push();
            template = template.getNext();
        }
        
    }
 
    /**
     * Pop template terms ooff stack
     */
    public void pop()
    {
        if (tailCallContext != null)
        {
            tailCallContext.restoreContext();
            if (headCallContext == tailCallContext)
            {
                headCallContext = null;
                tailCallContext = null;
            }
            else
            {
                CallContext newTail = headCallContext;
                while (newTail.getNext() != tailCallContext)
                    newTail = newTail.getNext();
                tailCallContext = newTail;
            }
        }
        Template template = getNext();
        while (template != null)
        {
            template.pop();
            template = template.getNext();
        }
    }

    /**
     * add term pair
     * @see au.com.cybersearch2.classy_logic.interfaces.TermPairList#add(au.com.cybersearch2.classy_logic.interfaces.Operand, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public void add(Operand term1, Term term2)
    {
        if (termPairHead != null)
        {   // Skip duplicates
            TermPair termPair = termPairHead;
            while(termPair != null)
            {
                if (term1 == termPair.getTerm1())
                    return;
                termPair = termPair.getNext(); 
            }
        }
        // Get next free TermPair or create instance if no free items available
        TermPair nextTermPair = null;
        if (freeTermPairHead == null)
            nextTermPair = new TermPair(term1, term2);
        else
        {
            nextTermPair = freeTermPairHead;
            nextTermPair.setTerm1(term1);
            nextTermPair.setTerm2(term2);
            nextTermPair.setNext(null);
            freeTermPairHead = freeTermPairHead.getNext();
        }
        // Append TermPair item to list
        if (termPairHead == null)
        {
            termPairHead = nextTermPair;
            termPairTail = nextTermPair;
        }
        else
        {
            termPairTail.setNext(nextTermPair);
            termPairTail = nextTermPair;
        }
    }

    /**
     * Return TermPair list to initial state
     */
    public void clearTermPairList()
    {   // Recycle items
        freeTermPairHead = termPairHead;
        // Clear list accessors
        termPairHead = termPairTail = null;
    }
}
