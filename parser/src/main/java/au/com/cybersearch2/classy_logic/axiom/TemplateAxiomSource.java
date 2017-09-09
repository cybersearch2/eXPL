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
package au.com.cybersearch2.classy_logic.axiom;

import java.util.ArrayList;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * TemplateAxiomSource
 * @author Andrew Bowley
 * 3Sep.,2017
 */
public class TemplateAxiomSource implements AxiomSource
{
    protected Archetype<Axiom,Term> archetype;
    protected Template template;
    protected AxiomList axiomList;
    
    /**
     * Construct TemplateAxiomSource object
     */
    public TemplateAxiomSource(Template template)
    {
        this.template = template;
    }

    @Override
    public Iterator<Axiom> iterator()
    {
        template.backup(true);
        Axiom axiom = new Axiom(template.getKey());
        template.getProperties().initialize(axiom, template);
        if ((axiom != null) && (axiom.getTermCount() > 0))
            template.unify(axiom, null);
        if (template.evaluate(null) == EvaluationStatus.COMPLETE)
            axiomList = (AxiomList) template.getTermByIndex(0).getValue();
        else
            return new ArrayList<Axiom>().iterator();
        return new Iterator<Axiom>(){
            Iterator<AxiomTermList> listIterator = axiomList.getIterable().iterator();
            @Override
            public boolean hasNext()
            {
                return listIterator.hasNext();
            }

            @Override
            public Axiom next()
            {
                return listIterator.next().getAxiom();
            }};
    }

    @Override
    public Archetype<Axiom, Term> getArchetype()
    {
        if (axiomList != null)
            return getAxiomListArchetype();
        if (archetype == null)
            archetype = getEmptyArchetype();
        return archetype;
    }

    private Archetype<Axiom, Term> getEmptyArchetype()
    {
        AxiomArchetype archetype = new AxiomArchetype(QualifiedName.ANONYMOUS);
        archetype.clearMutable();
        return archetype;
    }

    @SuppressWarnings("unchecked")
    private Archetype<Axiom, Term> getAxiomListArchetype()
    {
        if (!axiomList.isEmpty())
            return (Archetype<Axiom, Term>) axiomList.getItem(0).getAxiom().getArchetype();
        AxiomArchetype archetype = new AxiomArchetype(axiomList.getKey());
        for (String termName: axiomList.getAxiomTermNameList())
            archetype.addTermName(termName);
        archetype.clearMutable();
        return archetype;
    }
}
