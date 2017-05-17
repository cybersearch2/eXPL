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
package au.com.cybersearch2.classy_logic.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.TermListManager;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * ArchiveIndexHelper
 * Completes parser task of adding metadata for template operands contained in operand tree to the archetype.
 * Also generates fix ups for operands referenced by other templates so they have correct archive indexes at unification.
 * @author Andrew Bowley
 * 11May,2017
 */
public class ArchiveIndexHelper implements OperandVisitor
{
    /** Container for quick access to fix up data */
    static class FixUp
    {
        public Operand operand;
        public int preFixIndex;
        public int postFixIndex;
    }
 
    static List<FixUp> EMPTY_FIXUP_LIST = Collections.emptyList();
    
    /** Current archive index used when visiting operands */
    private int index;
    /** Map of term names and archive indexes used to detect multiple occurences of the same 
     *  unification pairing eg. same operand appearing in different places */
    private Map<String, Integer> indexMap;
    /** Fixup data for operands requiring restoration of archive index value post-unification */
    private List<FixUp> fixUpList;
    /** Template archetype to be updated with operand metadata */
    private TermListManager archetype;
    /** Template narrowed to term list super class */
    private TermList<Operand> termList;
    /** Template context name(s) used to determine which operands are in template name space */
    private QualifiedName[] contextNames;
 
    /**
     * Construct ArchiveIndexHelper object
     * @param template Template to process
     */
    public ArchiveIndexHelper(Template template)
    {
        this.archetype = template.getArchetype();
        this.termList = template;
        this.contextNames = template.getContextNames();
    }
    
    /**
     * Set term meta data in archetype for template operands in evaluation tree. 
     */
    public List<FixUp> setOperandTree() 
    {
        index = termList.getTermCount();
        getIndexMap();
        for (int i = 0; i < index; i++)
        {
            Operand operand = termList.getTermByIndex(i);
            if (inSameSpace(operand))
            {
                indexMap.put(archetype.getMetaData(i).getName(), i);
                setArchiveIndex(operand, i);
            }
            else
                unsetArchiveIndex(operand);
        }
        for (int i = 0; i < termList.getTermCount(); ++i)
        {
            Operand operand =  termList.getTermByIndex(i);
            OperandWalker operandWalker = new OperandWalker(operand);
            operandWalker.visitAllNodes(this);
        }
        // Remove indexMap reference as it is only needed while visiting operands
        indexMap = null;
        if (fixUpList == null)
            return EMPTY_FIXUP_LIST;
        // Remove fixUpList reference as it is only needed while visiting operands
        List<FixUp> fixUps = fixUpList;
        fixUpList = null;
        return fixUps;
    }

    /**
     * next operand
     * @see au.com.cybersearch2.classy_logic.interfaces.OperandVisitor#next(au.com.cybersearch2.classy_logic.interfaces.Operand, int)
     */
    @Override
    public boolean next(Operand operand, int depth)
    {
        String name = operand.getName();
        // Skip anonymous Evaluator operands
        if (!name.isEmpty())
        {   // Strategy depends on whether operand in same name space as template 
            if (inSameSpace(operand))
            {   // Set operand archive index or create fix up if a different value is already assigned
                getIndexMap();
                if (indexMap.containsKey(name))
                {   // Index for name already assigned. Assumes meta data is identical to that of original operand.
                    int archiveIndex = indexMap.get(name);
                    setArchiveIndex(operand, archiveIndex);
                }
                else
                {   // Add term meta data to archetype and index map
                    int archiveIndex = index++;
                    indexMap.put(name, archiveIndex);
                    setArchiveIndex(operand, archiveIndex);
                    archetype.addTerm(new TermMetaData(operand, archiveIndex));
                }
            }
            else
                // Ensure archetype index is set to default -1 for unification so
                // there is no axiom pairing, only solution pairing
                unsetArchiveIndex(operand);
        }
        // Keep going
        return true;
    }

    /**
     * Create fix up for given operand and archive index and add to fix up list
     * @param operand The operand to fix
     * @param archiveIndex The archive index to apply
     */
    protected void addFixUp(Operand operand, int archiveIndex)
    {
        FixUp fixUp = new FixUp();
        fixUp.operand = operand;
        fixUp.preFixIndex = fixUp.operand.getArchetypeIndex();
        fixUp.postFixIndex = archiveIndex;
        getFixUpList().add(fixUp);
    }

    /**
     * Return flag set true if operand belongs to same name space as template
     * @param operand The operand to analyse
     * @return boolean
     */
    private boolean inSameSpace(Operand operand)
    {
        for (QualifiedName contextName: contextNames)
            if (contextName.inSameSpace(operand.getQualifiedName())) 
                return true;
        return false;
    }

    /**
     * Set archive index in operand or add fix up if required
     * @param operand The operand to set
     * @param archiveIndex The index value to set
     */
    private void setArchiveIndex(Operand operand, int archiveIndex)
    {
        if (operand.getArchetypeIndex() == -1)
            operand.setArchetypeIndex(archiveIndex);
        else if (operand.getArchetypeIndex() != archiveIndex)
            addFixUp(operand, archiveIndex);
    }

    /**
     * Ensure archetype index for given operand is set to default -1
     * @param operand The operand to set
     */
    private void unsetArchiveIndex(Operand operand)
    {
        int archiveIndex = operand.getArchetypeIndex();
        if (archiveIndex != -1)
            addFixUp(operand, -1);
    }

    /**
     * Returns fix up list, creating it if required
     * @return FixUp list
     */
    private List<FixUp> getFixUpList()
    {
        if (fixUpList == null)
            fixUpList = new ArrayList<FixUp>();
        return fixUpList;
    }
 
    /**
     * Returns index map, creating it if required 
     * @return index map
     */
    private Map<String, Integer> getIndexMap()
    {
        if (indexMap == null)
            indexMap = new HashMap<String, Integer>();
        return indexMap;
    }

}
