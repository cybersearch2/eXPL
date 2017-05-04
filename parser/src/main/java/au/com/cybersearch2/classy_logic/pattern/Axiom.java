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
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;
import au.com.cybersearch2.classy_logic.terms.TermStore;


/**
 * Axiom
 * Contains data organised for pattern matching to Templates by unification 
 * @author Andrew Bowley
 *
 * @since 06/10/2010
 */
public class Axiom extends TermList
{
	//private static final long serialVersionUID = 2741521667825735668L;
    //private static final ObjectStreamField[] serialPersistentFields =
    //{
    //    new ObjectStreamField("name", String.class),
    //    new ObjectStreamField("pairByPosition", Boolean.class)
    //};

    /**
	 * TermPair holds paired Terms for unification
	 */
	public static class TermPair
	{
		public Term term1;
		public Term term2;

		public TermPair(Term term1, Term term2)
		{
			this.term1 = term1;
			this.term2 = term2;
		}
	}

	/** Special case of all Axiom terms are anonymous. Unify termList by position */
	protected boolean pairByPosition;
    /** Pairs axiom terms in a Solution object with terms in a template */
    protected SolutionPairer solutionPairer;
    /** Pairs axiom terms in this axiom with terms in a template */
    protected AxiomPairer axiomPairer;
    protected String name;
	
    /**
     * Construct an empty or parameter-supplied self-mangaged Axiom. Use addTerm() to add terms. 
     * @param name
     */
    public Axiom(String name, Parameter... params)
    {
        super(new AxiomArchetype(QualifiedName.parseGlobalName(name)));
        this.name = name;
        // Assume terms are anonymous. Change when non=anonymous term is added
        pairByPosition = true;
        if ((params != null)&& (params.length > 0))
        {
            if (archetype.isMutable())
                setTerms(params);
            for (Term term: params)
                addTerm(term);
            pairByPosition = archetype.getNamedTermCount() == 0;
        }
    }

	/**
	 * Construct an empty Axiom. Use addTerm() to add terms. 
	 * @param name
	 */
	public Axiom(AxiomArchetype axiomArchetype)
	{
        super(axiomArchetype);
		name = axiomArchetype.getName();
		// Assume terms are anonymous. Change when non=anonymous term is added
		pairByPosition = true;
	}

	/**
	 * Construct an Axiom from a variable Object argument
	 * @param name
	 * @param data Objects to add. 
	 */
	public Axiom(AxiomArchetype axiomArchetype, Object... data)
	{
        this(axiomArchetype);
        if ((data != null)&& (data.length > 0))
        {
            List<Term> terms = new ArrayList<Term>(data.length);
            for (Object datum: data)
            {
                if (datum instanceof Term)
                    terms.add((Term) datum);
                else
                    terms.add(new Parameter(Term.ANONYMOUS, datum));
            }
    		if (archetype.isMutable())
    		    setTerms(terms.toArray(new Term[terms.size()]));
            pairByPosition = archetype.getNamedTermCount() == 0;
        }
	}

	/**
	 * Construct an Axiom from a variable Term argument
	 * @param name
	 * @param terms Terms to add
	 */
	public Axiom(AxiomArchetype axiomArchetype, Term... terms)
	{
        this(axiomArchetype);
        if ((terms != null)&& (terms.length > 0))
        {
            if (archetype.isMutable())
                setTerms(terms);
            for (Term term: terms)
                addTerm(term);
        }
        pairByPosition = archetype.getNamedTermCount() == 0;
	}

	/**
	 * Construct an Axiom from a list of terms
	 * @param name
	 * @param terms List of Term objects
	 */
	public Axiom(AxiomArchetype axiomArchetype, List<Term> terms)
	{
        this(axiomArchetype);
        if ((terms != null)&& (terms.size() > 0))
        {
            if (archetype.isMutable())
                setTerms(terms.toArray(new Term[terms.size()]));
            for (Term term: terms)
                addTerm(term);
            pairByPosition = archetype.getNamedTermCount() == 0;
        }
 	}

    /**
	 * Unify this Axiom with given Template, pairing Terms of this Axiom with those
	 * of the Template. Terms are matched by name except when "pairByPosition" flag is set, 
	 * in which case, terms are paired in list order. 
	 * When both terms of a pairing have a value and values do not match, unification is skipped. 
	 * Unification of all terms is skipped if this Axiom and the other Template have different names.
     * @param solution Map of Axioms selectable by Axiom name
	 * @param other Template
	 * @return Flag unification completed = true
	 */
	public boolean unifyTemplate(Template other, Solution solution)
    {
		//if (!this.name.equals(other.getKey()))
		//	return false; // Names don't match
		Set<String> keySet = solution.keySet();
		List<TermPair> pairList = new ArrayList<TermPair>(termList != null ? termList.size() : other.termList.size());
		OperandWalker walker = new OperandWalker(other.termList);
		// If term list is not empty, unification will be restricted to solution only.
		if (!termList.isEmpty())
		{
			if (axiomPairer == null)
				axiomPairer = new AxiomPairer(this, other.getQualifiedName());
			else
				axiomPairer.reset();
			if (pairByPosition)
			{
				int index = 0;
				for (Term templateTerm: other.termList)
				{   // Match by position
					Term axiomTerm = getTermByIndex(index++);
					if (index > getTermCount())
					    break;
                    if (!axiomTerm.getName().isEmpty())
                        continue;
					if (!axiomPairer.pairTerms((Operand)templateTerm, axiomTerm))
						return false;
					QualifiedName qname = ((Operand)templateTerm).getQualifiedName();
					boolean isLocalTerm = other.getQualifiedName().inSameSpace(qname);
					if (isLocalTerm && !qname.getName().isEmpty())
					{   // Name axiom term for Operand navigation
						axiomTerm.setName(qname.getName());
						archetype.changeName(index, qname.getName());
					}
				}
				// Only needs to be done once
				pairByPosition = false;
			}
			if (!walker.visitAllNodes(axiomPairer))
				return false;
			pairList.addAll(axiomPairer.getPairList());
		}
		if (keySet.size() > 0)
		{
			if (solutionPairer == null)
				solutionPairer = new SolutionPairer(this, solution, other.getQualifiedName());
			else
				solutionPairer.setSolution(solution);
			if (!walker.visitAllNodes(solutionPairer))
				return false;
			pairList.addAll(solutionPairer.getPairList());
		}
		// Proceed with unification term by term
		for (TermPair termPair: pairList)
			termPair.term1.unifyTerm(termPair.term2, other.getId());
		return true;
    }

	/**
	 * Add a Term() with name assigned from a list according to position 
	 * @param term Term object
	 * @param nameList List of term names assembled by parser
	 */
	public void addTerm(Term term, List<String> nameList)
	{
		int index = termList == null ? 0 : termList.size();
		if (index < nameList.size())
			term.setName(nameList.get(index));
		addTerm(term);
		pairByPosition = false;
	}

    private void writeObject(ObjectOutputStream oos)
            throws IOException 
    {
        // termList size
        oos.writeInt(termList.size());
        // terms
        for (Term term: termList)
            oos.writeObject(new TermStore(term));
    }
    
    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException  
    {
        // termList size
        Term[] termArray = new Term[ois.readInt()];
        // terms
        for (int i = 0; i < termArray.length; i++)
        {
            TermStore termStore = (TermStore) ois.readObject();
            Parameter param = new Parameter(termStore.getName(), termStore.getValue());
            param.setId(termStore.getId());
            termArray[i] = param;
        }
        setTerms(termArray);
    }

    /**
     * Set Anonymous Terms using values from supplied Object array.
     * Any Object in the array not of type Term id converted to
     * an anonymous Term with value set to the object. 
     * @param terms Term array
     */
    protected void setTerms(Term[] terms) 
    {
        if (terms.length > 0)
        {
            int index = 0;
            for (Term term: terms)
                archetype.addTerm(new TermMetaData(term, index++));
            archetype.clearMutable();
        }
    }
}
