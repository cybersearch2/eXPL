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
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * AxiomListEvaluator
 * @author Andrew Bowley
 * 7Jun.,2017
 */
public class AxiomListEvaluator
{
    private static final List<Template> EMPTY_TEMPLATE_LIST = Collections.emptyList();
    
    /** Qualified name of list to be created */
    protected QualifiedName qname;
    /** Qualified name of axioms in the list */
    protected QualifiedName axiomKey;
    /** List of templates, each defining the terms of an axiom to add to the list */
    protected List<Template> initializeList;
    
    /**
     * Evaluates to create an AxiomList from an initialization list
     * @param qname Qualified name of list to be created
     * @param axiomKey Qualified name of axioms in the list
     * @param initializeList List of templates, each defining the terms of an axiom to add to the list
      */
    public AxiomListEvaluator(QualifiedName qname, QualifiedName axiomKey, List<Template> initializeList)
    {
        this.qname = qname;
        this.axiomKey = axiomKey;
        this.initializeList = initializeList == null ? EMPTY_TEMPLATE_LIST : initializeList;
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
     * @return AxiomList object
     */
    public AxiomList evaluate(int id)
    {
        AxiomList axiomList = new AxiomList(qname, axiomKey);
        AxiomArchetype archetype = new AxiomArchetype(axiomKey);
        int index = 0;
        for (Template template: initializeList)
        {
            template.evaluate(null);
            List<Term> termList =  template.toArray();
            // Do not add empty axioms to list
            if (termList.size() > 0)
            {
                AxiomTermList axiomTermList = new AxiomTermList(qname, axiomKey);
                axiomTermList.setAxiom(new Axiom(archetype, termList));
                if (index == 0)
                    archetype.clearMutable();
                axiomList.assignItem(index++, axiomTermList);
            }
        }
        return axiomList;
    }

    /**
     * Backup initialization template(s)
     * @param id Modification id
     */
    public void backup(int id)
    {
        for (Template template: initializeList)
            template.backup(id != 0);
    }

    /**
     * Returns list size
     * @return int
     */
    public int size()
    {
        if (initializeList.size() == 1)
            return initializeList.get(0).getTermCount() == 0 ? 0 : 1;
        return initializeList.size();
    }
}
