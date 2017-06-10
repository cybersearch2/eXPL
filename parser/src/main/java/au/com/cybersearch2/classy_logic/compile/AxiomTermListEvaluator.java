/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.compile;

import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * AxiomTermListEvaluator
 * @author Andrew Bowley
 * 7Jun.,2017
 */
public class AxiomTermListEvaluator
{
    protected static List<Term> EMPTY_TERM_LIST = Collections.emptyList();
    
    /** Qualified name of list to be created */
    protected QualifiedName qname;
    /** Qualified name of axioms in the list */
    protected QualifiedName axiomKey;
    /** Template to evaluate axiom terms */
    protected Template template;
    /** Function call evaluator which returns result as axiom term list */
    protected CallEvaluator<AxiomTermList> callEvaluator;
    
    /**
     * Evaluates AxiomTermList
     * @param qname Qualified name of list to be created
     * @param axiomKey Qualified name of axiom backing the list
     * @param template Template to evaluate axiom terms
     */
    public AxiomTermListEvaluator(QualifiedName qname, QualifiedName axiomKey, Template template)
    {
        this.qname = qname;
        this.axiomKey = axiomKey;
        this.template = template;
    }
    
    /**
     * Implements call evaluator which returning the result in an AxiomTermList
     * @param qname Qualified name of list to be created
     * @param callEvaluator Function call evaluator which returns result as axiom term list
     * @param template Template to evaluate axiom terms
    */
    public AxiomTermListEvaluator(QualifiedName qname, CallEvaluator<AxiomTermList> callEvaluator, Template template)
    {
        this.qname = qname;
        this.axiomKey = qname;
        this.callEvaluator = callEvaluator;
        this.template = template;
    }
    
    /**
     * Returns list qualified name
     * @return QualifiedName object
     */
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

    /**
     * @return the axiomKey
     */
    public QualifiedName getAxiomKey()
    {
        return axiomKey;
    }

    /**
     * Returns list name
     * @return name
     */
   public String getName()
    {
        return qname.getName();
    }

   /**
    * Evaluate to create axiom list
    * @param id Modification id
    * @return AxiomTerm List object
    */
    public AxiomTermList evaluate(int id)
    {
        List<Term> termList;
        if (template != null)
        {
            template.evaluate(null);
            termList = template.toArray();
        }
        else
            termList = EMPTY_TERM_LIST;
        if (callEvaluator != null)
            return callEvaluator.evaluate(termList);
        else
        {
            AxiomArchetype archetype = new AxiomArchetype(axiomKey);
            AxiomTermList axiomTermList = new AxiomTermList(qname, axiomKey);
            axiomTermList.setAxiom(new Axiom(archetype, termList));
            archetype.clearMutable();
            return axiomTermList;
        }
    }

    /**
     * Backup initialization template
     * @param id Modification id
     */
    public void backup(int id)
    {
        if (template != null)
            template.backup(id != 0);
    }

    /**
     * Returns list size
     * @return int
     */
    public int size()
    {
        return template != null ? template.getTermCount() : 0;
    }
    
    /**
     * toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(qname.toString());
        builder.append('(');
        if (template != null)
        {
            Term op1 = template.getTermByIndex(0);
            builder.append(op1.toString());
            int count = template.getTermCount();
            if (count > 1)
            {
                Term op2 = template.getTermByIndex(count - 1);
                builder.append(" ... ").append(op2.toString());
            }
        }
        builder.append(')');
        return builder.toString();
    }
}
