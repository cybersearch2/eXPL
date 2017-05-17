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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.axiom.AxiomListSource;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;

/**
 * AxiomAssembler
 * @author Andrew Bowley
 * 13May,2017
 */
public class AxiomAssembler
{
    private static final List<String> EMPTY_NAME_LIST;

    static
    {
        EMPTY_NAME_LIST = Collections.emptyList();
    }
    
    /** Container for axioms under construction */
    protected Map<QualifiedName, Axiom> axiomMap;
    /** Archetypes for axioms */
    protected Map<QualifiedName, AxiomArchetype> axiomArchetypeMap;
    /** Scope */
    protected Scope scope;

    public AxiomAssembler(Scope scope)
    {
        this.scope = scope;
        axiomMap = new HashMap<QualifiedName, Axiom>();
        axiomArchetypeMap = new HashMap<QualifiedName, AxiomArchetype>();
    }

    /**
     * Add a term to axiom under construction
     * @param qualifiedAxiomName
     * @param term Term object
     */
    public void addAxiom(QualifiedName qualifiedAxiomName, Term term)
    {
        Axiom axiom = axiomMap.get(qualifiedAxiomName);
        if (axiom == null)
        {   // No axiom currently under construction, so create one.
            AxiomArchetype axiomArchetype = axiomArchetypeMap.get(qualifiedAxiomName);
            if (axiomArchetype == null)
                axiomArchetype = new AxiomArchetype(qualifiedAxiomName);
            axiomArchetypeMap.put(qualifiedAxiomName, axiomArchetype);
            axiom = axiomArchetype.itemInstance();
            axiomMap.put(qualifiedAxiomName, axiom);
        }
        axiom.addTerm(term);
    }

    /**
     * Add name to list of axiom term names
     * @param qualifiedAxiomName
     * @param termName
     */
    public void addAxiomTermName(QualifiedName qualifiedAxiomName, String termName)
    {
        AxiomArchetype axiomArchetype = axiomArchetypeMap.get(qualifiedAxiomName);
        if (axiomArchetype == null)
        {
            axiomArchetype = new AxiomArchetype(qualifiedAxiomName);
            axiomArchetypeMap.put(qualifiedAxiomName, axiomArchetype);
        }
        axiomArchetype.addTermName(termName);
    }
    
    /**
     * Get axiom term name by position
     * @param qualifiedAxiomName
     * @param position 
     */
    public String getAxiomTermName(QualifiedName qualifiedAxiomName, int position)
    {
        AxiomArchetype axiomArchetype = axiomArchetypeMap.get(qualifiedAxiomName);
        if (axiomArchetype == null)
            return null;
        return axiomArchetype.getMetaDataByIndex(position).getName();
    }
    
    /**
     * Returns list of axiom term names
     * @param qualifiedAxionName
     */
    public List<String> getAxiomTermNameList(QualifiedName qualifiedAxiomName)
    {
        AxiomArchetype axiomArchetype = scope.getGlobalAxiomAssembler().axiomArchetypeMap.get(qualifiedAxiomName);
        if ((axiomArchetype == null) && !QueryProgram.GLOBAL_SCOPE.equals(scope.getName()))
            axiomArchetype = axiomArchetypeMap.get(qualifiedAxiomName);
        return axiomArchetype == null ? EMPTY_NAME_LIST : axiomArchetype.getAxiomTermNameList();
    }
    
    /**
     * Transfer axiom under construction to the list of axioms with same name
     * @param qualifiedAxiomName
     */
    public Axiom saveAxiom(QualifiedName qualifiedAxiomName)
    {
        Axiom axiom = axiomMap.get(qualifiedAxiomName);
        scope.getParserAssembler().getListAssembler().add(qualifiedAxiomName, axiom);
        axiomMap.remove(qualifiedAxiomName);
        return axiom;
    }

    public AxiomArchetype getAxiomArchetype(QualifiedName qualifiedAxiomName)
    {
        return axiomArchetypeMap.get(qualifiedAxiomName);
    }
    
    protected List<String> findTermNameList(QualifiedName qualifiedAxiomName)
    {
        AxiomArchetype axiomArchetype = axiomArchetypeMap.get(qualifiedAxiomName);
        List<String> axiomTermNameList;
        if (axiomArchetype == null)
            axiomTermNameList = Collections.emptyList();
        else
            axiomTermNameList = axiomArchetype.getAxiomTermNameList();
        return axiomTermNameList;
    }
    
    protected AxiomSource createAxiomSource(QualifiedName qualifiedAxiomName, List<Axiom> axiomList)
    {
        List<String> terminalNameList = axiomArchetypeMap.get(qualifiedAxiomName).getAxiomTermNameList();
        AxiomListSource axiomListSource = new AxiomListSource(axiomList);
        axiomListSource.setAxiomTermNameList(terminalNameList);
        return axiomListSource;
    }
    
}
