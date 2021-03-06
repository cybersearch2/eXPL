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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import au.com.cybersearch2.classy_logic.compile.TemplateType;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.OperatorTerm;
import au.com.cybersearch2.classy_logic.query.Solution;


/**
 * Template
 * A collection of operands which evaulate and produce a solution axiom. 
 * A template performs unification which it supports by analysing the archetypes of axioms with which it is paired.
 * Template variants are inner template, choice and replicate which support particular language features 
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
    public static List<Operand> EMPTY_OPERAND_LIST;
    public static List<Term> EMPTY_TERM_LIST;
    
    /** Unique identity generator */
    static protected AtomicInteger referenceCount;
 
    static
    {
        referenceCount = new AtomicInteger();
        EMPTY_OPERAND_LIST = Collections.emptyList();
        EMPTY_NAMES_LIST = Collections.emptyList();
        EMPTY_TERM_LIST = Collections.emptyList();
    }
    
    /** Qualified name of template */
    protected QualifiedName qname;
    /** Key to match with Axiom name for unification */
	protected String key;
    /** Identity used in backup to allow partial backup to last unifying agent */
    protected int id;
    /** Initialization data (optional) */
    protected TemplateProperties properties;
    /** Link to next Template in chain. Used by Calculator. */
    protected Template next;
    /** Flag true if template declared a calculator */
    protected boolean isCalculator;
    /** Flag true if template used to make selection. Used by Calculator. */
    protected boolean isChoice;
    /** Flag true if inner template */
    protected boolean isInnerTemplate;
    /** Flag true if replicate. Required because terms are reused instead of preserved after evaluation. */
    protected boolean isReplicate;
    /** Head of call stack */
    protected CallContext headCallContext;
    /** Tail of call stack */
    protected CallContext tailCallContext;
    /** Pairs axiom terms in a Solution object with terms in a template */
    protected SolutionPairer solutionPairer;
    /** Template archetype - attribute avoids casting to get super archetype */
    private TemplateArchetype templateArchetype;
    /** Registered debug targets */
    private DebugTarget[] debugTargets;
   
    /**
     * Construct a replicate Template object. The new template has a unique id and specified qualified name 
     * @param master Template object to replicate
     * @param qname New Template qualified name. 
     */
    public Template(Template master, QualifiedName qname) 
    {
        // Replicates share the master template archetype
        this(master.getTemplateArchetype());
        // Context name is now set as qualified name of master template.
        // This template qualified name must be set to given value.
        // Note this is the only case where template qualified name and 
        // context name happen to be in different scopes.
        this.qname = qname;
        key = master.key;
        this.isCalculator = master.isCalculator;
        this.isChoice = master.isChoice;
        this.isInnerTemplate = master.isInnerTemplate;
        isReplicate = true;
        // Replicates share the terms of the master template
        for (Operand operand: master.termList)
            termList.add(operand);
        termCount = termList.size();
        properties = new TemplateProperties(master.properties);
    }

    /**
     * Construct Template object
     * @param qname Template qualified name. The axiom key is set to the name part as a default.
     */
    public Template(TemplateArchetype templateArchetype) 
    {
        super(templateArchetype);
        this.templateArchetype = templateArchetype;
        qname = templateArchetype.getQualifiedName();
        key = qname.getTemplate();
        id = referenceCount.incrementAndGet();
        properties = new TemplateProperties();
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

	/**
	 * Returns flage set true if this si a replicate template
	 * @return boolean
	 */
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

    public TemplateProperties getProperties()
    {
        return properties;
    }
    
    /**
	 * Evaluate Terms of this Template
	 * @return EvaluationStatus
	 */
	public EvaluationStatus evaluate(ExecutionContext executionContext)
	{
		if (!termList.isEmpty())
		{
		    if ((executionContext != null) && (debugTargets != null))
		        for (DebugTarget debugTarget: debugTargets)
		            debugTarget.setExecutionContext(executionContext);
			for (Operand term: termList)
			{
			    if (!term.isEmpty() && (term.getId() != id))
			        continue;
                if (executionContext != null)
                    executionContext.beforeEvaluate(term);
				EvaluationStatus evaluationStatus = term.evaluate(id);
				if (evaluationStatus != EvaluationStatus.COMPLETE)
					return evaluationStatus;
			}
		}
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * Evaluate Terms of this Template until status COMPLETE is returned 
	 * @param selectionTerm Term containing selection value
	 * @param executionContext Execution context - may be null
	 * @return Position of first term selected or Choice.NO_MATCH if evaluation does not return status COMPLETE
	 */
	public int select(Term selectionTerm, ExecutionContext executionContext)
	{
        int position = 0;
		if (!termList.isEmpty())
		{
			for (Operand term: termList)
			{
                if (executionContext != null)
                    executionContext.beforeEvaluate(qname, position);
                    
				if (term.evaluate(id) == EvaluationStatus.COMPLETE)
				{
				    Object value = term.getValue();
				    if (term.getValueClass() == Boolean.class)
				    {
				        if ((Boolean) value == Boolean.TRUE)
				            return position;
                    }
				    else if (value.equals(selectionTerm.getValue()))
                        return position;
				}
				else
			        return Choice.NO_MATCH;
                ++position;
			}
		}
		// NO Default
        return Choice.NO_MATCH;
	}

	   /**
     * Evaluate Terms of this Template until status COMPLETE is returned 
     * @param selectionTerm Term containing selection value
     * @param executionContext Execution context - may be null
     * @return Position of first term selected or Choice.NO_MATCH if evaluation does not return status COMPLETE
     */
    public EvaluationStatus chainShortCircuit(ExecutionContext executionContext)
    {
        EvaluationStatus evaluationStatus = EvaluationStatus.COMPLETE;
        if (!termList.isEmpty())
        {
            if ((executionContext != null) && (debugTargets != null))
                for (DebugTarget debugTarget: debugTargets)
                    debugTarget.setExecutionContext(executionContext);
            for (Operand term: termList)
            {
                if (!term.isEmpty() && (term.getId() != id))
                    continue;
                if (executionContext != null)
                    executionContext.beforeEvaluate(term);
                evaluationStatus = term.evaluate(id);
                if (evaluationStatus == EvaluationStatus.COMPLETE)
                    break;
            }
        }
        return evaluationStatus;
    }
    
	/**
	 * Backup from last unification.
	 * @param partial Flag to indicate backup to before previous unification or backup to start
	 * @return Flag to indicate if this Structure is ready to continue unification
	 */
	public boolean backup(boolean partial)
	{
		boolean isMutable = false;
		if (!termList.isEmpty())
			for (Operand term: termList)
			{
				boolean backupPerformed = (partial ? term.backup(id) : term.backup(0));
				if (backupPerformed)
					isMutable = true;
			}
		return isMutable;
	}

    /**
     * Backup from last unification.
     * @param modificationId Modification id
     * @return Flag to indicate if this Structure is ready to continue unification
     */
    public boolean backup(int modificationId)
    {
        boolean isMutable = false;
        if (!termList.isEmpty())
            for (Operand term: termList)
            {
                boolean backupPerformed = term.backup(modificationId);
                if (backupPerformed)
                    isMutable = true;
            }
        return isMutable;
    }

    /**
     * Add Operand term. 
     * @param operand Operand object
     */
    public void addTerm(Operand operand)
    {
        super.addTerm(operand);
        if (operand instanceof DebugTarget)
            registerDebugTarget((DebugTarget)operand);
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
        return unify(axiom, solution, templateArchetype.getTermMapping(axiom.getArchetype()));
    }

    /**
     * Returns solution pairer to perform unification with this template
     * @param solution The query solution up to this stage
     * @return SolutionPairer object
     */
    public SolutionPairer getSolutionPairer(Solution solution)
    {
       return new SolutionPairer(solution, id, qname);
    }

    /**
     * Returns flag set true if given operand is in same namespace as this template
     * @param operand Operand object
     * @return boolean
     */
    public boolean isInSameSpace(Operand operand)
    {
        return qname.inSameSpace(operand.getQualifiedName());

    }
    /**
	 * Returns an axiom containing the values of this template and 
	 * having same key and name as this template's name.
	 * @return Axiom
	 */
	public Axiom toAxiom()
	{
        Axiom axiom = new Axiom(qname.getTemplate());
        @SuppressWarnings("unchecked")
        Archetype<Axiom,Term> archetype = (Archetype<Axiom, Term>) axiom.getArchetype();
        //axiom.getArchetype().setDuplicateTermNames(true);
		for (Term term: termList)
		{
		    Operand operand = (Operand)term;
            if (!operand.isEmpty() && !operand.isPrivate()  && 
			    (isReplicate || isInSameSpace(operand)) &&
			    (archetype.getIndexForName(operand.getName()) == -1))
			{
                String termName = operand.getName();
				OperatorTerm param = new OperatorTerm(termName, operand.getValue(), operand.getOperator());
				axiom.addTerm(param);
			}
		}
		return axiom;
	}

	/**
	 * Returns all term values as a list. Unlike toAxiom(), there is no filtering eg. no honooring "private" flag 
	 * @return Term list
	 */
    public List<Term> toArray()
    {
        List<Term> arrayList  = new ArrayList<Term>();
        for (Operand operand: termList)
        {
            OperatorTerm param = new OperatorTerm(operand.getName(), operand.getValue(), operand.getOperator());
            arrayList.add(param);
        }
        return arrayList;
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
    			for (Operand term: template.termList)
    			    term.backup(0);
    		template = template.getNext();
	    }
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
     * Returns Term referenced by name
     * @param name String
     * @return Term object or null if not found
     */
	@Override
    public Operand getTermByName(String name)
    {
	    Operand term = super.getTermByName(name);
        if (term == null)
        {
            OperandFinder operandFinder = new OperandFinder(termList, name);
            term = operandFinder.findNode();
        }
        return term;
    }

   /**	
    * Returns query template instance
    * @param name Query name to be appended to template qualified name
    * @param templateType Template type - template or choice
    * @param master Master choice template - required only when template type is choice
    * @return Template object
    */
    public Template innerTemplateInstance(String name, TemplateType templateType, Template... master)
    {
        boolean isQueryTemplate = (name != null);
        boolean isChoice = templateType == TemplateType.choice;
        TemplateArchetype masterArchetype = null;
        
        if (master.length > 0)
        {
            masterArchetype = master[0].getTemplateArchetype();
            masterArchetype.clearMutable();
        }
        QualifiedName innerTemplateName = new QualifiedTemplateName(
                qname.getScope(), 
                qname.getTemplate() + 
                Integer.toString(qname.incrementReferenceCount() + 1));
        if (isQueryTemplate)
            innerTemplateName = new QualifiedName(name, innerTemplateName);
        TemplateArchetype newTemplateArchetype = masterArchetype == null ? new TemplateArchetype(innerTemplateName) : masterArchetype;
        Template newTemplate = new Template(newTemplateArchetype);
        if (isChoice)
            newTemplate.setChoice(true);
        newTemplate.isInnerTemplate = true;
        if (!isQueryTemplate)
            setNext(newTemplate);
        return newTemplate;
    }
    
	/**
	 * Returns inner template instance chained to this template
	 * @return Template object
	 */
    public Template innerTemplateInstance(TemplateType templateType)
    {
        return innerTemplateInstance(null, templateType);
    }

    /**
     * Create a choice template instance as an inner template of this template,
     * @param master Global scope template containing the choice operands 
     *                which are copied to the inner template.
     * @return Template object
     */
    public Template choiceInstance(Template master)
    {
        Template choiceTemplate = innerTemplateInstance(null, TemplateType.choice, master);
        for (Operand operand: master.termList)
            choiceTemplate.addTerm(operand);
        return choiceTemplate;
    }
    
    /**
     * Push template operand values on stack
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
        // Do full backup of chain templates so parameters can be re-evaluatoed
        while (template != null)
        {
            template.backup(false);
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
    }

    /**
     * Returns task to complete assembly of this template
     * @return Runnable
     */
    public Runnable getParserTask()
    {
        return new Runnable(){

            @Override
            public void run()
            {   // Use helper to set archive index and id in all operands
                ArchiveIndexHelper archiveIndexHelper = new ArchiveIndexHelper(Template.this);
                archiveIndexHelper.setOperandTree(1);
                archiveIndexHelper.setOperandTree(2);
                // Lock archetype
                getArchetype().clearMutable();
            }};
    }
    
    /**
     * Return the archetype with it's actual type
     * @return TemplateArchetype object
     */
    protected TemplateArchetype getTemplateArchetype()
    {
        return templateArchetype;
    }
 
    /**
     * Unify template using given axiom and solution
     * @param axiom Axiom with which to unify as TermList object 
     * @param solution Contains result of previous unify-evaluation steps
     * @param termMapping Maps operands to axiom terms
     * @return Flag set true if unification completed successfully
     */
    protected boolean unify(TermList<Term> axiom, Solution solution, int[] termMapping)
    {
        OperandWalker walker = new OperandWalker(termList);
        // Create list of term pairs to unify
        return walker.visitAllNodes(new Unifier(this, axiom, termMapping, solution));
    }

    /**
     * Register debug target so execution context is injected before evaluation
     * @param operand
     */
    private void registerDebugTarget(DebugTarget debugTarget)
    {
        // Uses basic storage approach as occurrence of debug operands will be low
        if (debugTargets == null)
        {
            debugTargets = new DebugTarget[1];
            debugTargets[0] = debugTarget;
        }
        else
        {
            DebugTarget[] newDebugTargets = new DebugTarget[debugTargets.length + 1];
            System.arraycopy(debugTargets, 0, newDebugTargets, 0, debugTargets.length);
            newDebugTargets[debugTargets.length] = debugTarget;
            debugTargets = newDebugTargets;
        }
    }


    /**
     * Serial I/O
     * @param ois ObjectInputStream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException  
    {
        archetype = new TemplateArchetype(qname);
    }

}
