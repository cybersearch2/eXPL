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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.terms.TermStore;


/**
 * Axiom
 * Contains data organised for pattern matching to Templates by unification 
 * @author Andrew Bowley
 *
 * @since 06/10/2010
 */
public class Axiom extends TermList<Term>
{
	//private static final long serialVersionUID = 2741521667825735668L;
    //private static final ObjectStreamField[] serialPersistentFields =
    //{
    //    new ObjectStreamField("name", String.class),
    //    new ObjectStreamField("pairByPosition", Boolean.class)
    //};

    protected String name;
	
    /**
     * Construct an empty or parameter-supplied self-mangaged Axiom. Use addTerm() to add terms. 
     * @param name
     */
    public Axiom(String name, Parameter... params)
    {
        super(new AxiomArchetype(QualifiedName.parseGlobalName(name)));
        this.name = name;
        if ((params != null)&& (params.length > 0))
        {
            if (archetype.isMutable())
                setTerms(params);
            for (Term term: params)
                addTerm(term);
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
         }
 	}

    /**
	 * Unify this Axiom with given Template, pairing Terms of this Axiom with those
	 * of the Template. Terms are matched by name except when "pairByPosition" flag is set, 
	 * in which case, terms are paired in list order. 
	 * When both terms of a pairing have a value and values do not match, unification is skipped. 
	 * Unification of all terms is also skipped if this Axiom and the other Template have different names.
	 * @param template Template with which to unify 
     * @param solution Contains result of previous unify-evaluation steps
	 * @return Flag set true if unification completed successfully
	 */
	public boolean unifyTemplate(Template template, Solution solution)
    {
	    return template.unify(this, solution);
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
}
