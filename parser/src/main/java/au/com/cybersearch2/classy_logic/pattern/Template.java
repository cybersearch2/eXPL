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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
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
public class Template extends TermList<Operand>
{
	private static final long serialVersionUID = -3549624322416667887L;
	
    private static final ObjectStreamField[] serialPersistentFields =
    {   // Template inherits Serializable from TermList but
        //    should not be serialized. Three fields defined for tracing purposes.
        new ObjectStreamField("qname", QualifiedName.class),
        new ObjectStreamField("key", String.class),
        new ObjectStreamField("id", Integer.class)
    };

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
    /** Qualified name of template */
    protected QualifiedName qname;
    /** Qualified name of context - different from qname for inner templates and replicates */
    protected QualifiedName contextName;
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
    /** Operand archive index fix ups */
    private List<ArchiveIndexHelper.FixUp> fixUpList;
   
    /**
     * Construct a replicate Template object. The new template has a unique id and specified qualified name 
     * @param master Template object to replicate
     * @param qname New Template qualified name. 
     */
    public Template(Template master, QualifiedName qname) 
    {
        this((TemplateArchetype) master.getArchetype(), master.getQualifiedName());
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
    private Template(TemplateArchetype templateArchetype, QualifiedName contextName) 
    {
        super(templateArchetype);
        this.contextName = contextName;
        qname = templateArchetype.getQualifiedName();
        key = qname.getTemplate();
        id = referenceCount.incrementAndGet();
    }

	/**
	 * Construct Template object
	 * @param qname Template qualified name. The axiom key is set to the name part as a default.
	 */
	public Template(TemplateArchetype templateArchetype) 
	{
		this(templateArchetype, templateArchetype.getQualifiedName());
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
    public boolean unify(final TermList<Term> axiom, Solution solution)
    {
        // The archetype meta data needs to include all operands found by walking the operand trees 
        // The archetype is mutable until this is completed
        if (archetype.isMutable())
        {
            // Complete archetype initialization
            ArchiveIndexHelper archiveIndexHelper = 
                (contextName == qname) ? 
                new ArchiveIndexHelper(archetype, this, contextName) :
                new ArchiveIndexHelper(archetype, this, contextName, qname);
            fixUpList = archiveIndexHelper.setOperandTree();
        }
        if (fixUpList != null)
            for (ArchiveIndexHelper.FixUp fixUp: fixUpList)
                fixUp.operand.setArchetypeIndex(fixUp.postFixIndex);
        final int[] termMapping = ((TemplateArchetype)archetype).createTermMapping(axiom.getArchetype());
        //for (int map = 0; map < termMapping.length; map++)
        //    System.out.print(Integer.toString(termMapping[map]) + ",");
        OperandWalker walker = new OperandWalker(termList);
        boolean unificationComplete = false;
        // Create list of term pairs to unify
        unificationComplete = walker.visitAllNodes(new Unifier(this, axiom, termMapping, solution));
        if (fixUpList != null)
            for (ArchiveIndexHelper.FixUp fixUp: fixUpList)
                fixUp.operand.setArchetypeIndex(fixUp.preFixIndex);
        return unificationComplete;
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

    public Template innerTemplateInstance(String name)
    {
        QualifiedName innerTemplateName = new QualifiedTemplateName(
                qname.getScope(), 
                qname.getTemplate() + 
                Integer.toString(qname.incrementReferenceCount() + 1));
        if (!name.isEmpty())
            innerTemplateName = new QualifiedName(name, innerTemplateName);
        TemplateArchetype newTemplateArchetype = new TemplateArchetype(innerTemplateName);
        Template newTemplate = new Template(newTemplateArchetype, qname);
        newTemplate.isInnerTemplate = true;
        setNext(newTemplate);
        return newTemplate;
    }

    public Template choiceInstance(Template master)
    {
        Template choiceTemplate = innerTemplateInstance("");
        choiceTemplate.contextName = master.getQualifiedName();
        for (Operand operand: master.termList)
            choiceTemplate.addTerm(operand);
        choiceTemplate.setChoice(true);
        return choiceTemplate;
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
     * Pop template terms off stack
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

    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException  
    {
        archetype = new TemplateArchetype(qname);
    }
}
