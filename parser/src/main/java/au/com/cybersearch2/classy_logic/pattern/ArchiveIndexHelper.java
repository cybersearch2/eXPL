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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /** Current archive index used when visiting operands */
    private int index;
    /** Map of term names and archive indexes used to detect multiple occurences of the same 
     *  unification pairing eg. same operand appearing in different places */
    private Map<String, Integer> indexMap;
    /** Template archetype to be updated with operand metadata */
    private TermListManager archetype;
    /** Template to assemble */
    private Template template;
 
    /**
     * Construct ArchiveIndexHelper object
     * @param template Template to assemble
     */
    public ArchiveIndexHelper(Template template)
    {
        this.archetype = template.getArchetype();
        this.template = template;
    }
    
    /**
     * Set term meta data in archetype for template operands in evaluation tree. 
     * Has 2 passes. The first if for operands in template namespace, where operands are bound to the archetype.
     * The second pass only updates archtypes and covers operands outside the template namespace.
     * @param pass Which one of two passes - 1 or 2.
     */
    public void setOperandTree(int pass) 
    {
        getIndexMap();
        List<Operand> operandList = new ArrayList<Operand>();
        for (int i = 0; i < template.getTermCount(); i++)
        {
            Operand operand = template.getTermByIndex(i);
            boolean isTemplateOperand = operand.getQualifiedName().getTemplate().equals(template.getQualifiedName().getTemplate());
            if (((pass == 1) && isTemplateOperand) || 
                 ((pass == 2) && !isTemplateOperand))
            {
                indexMap.put(archetype.getMetaData(i).getName(), i);
                operandList.add(operand);
                if (operand.getArchetypeId() == 0)
                {
                    operand.setArchetypeId(template.getId());
                    setArchiveIndex(operand, i);
                }
            }
        }
        index = pass == 1 ? template.getTermCount() : archetype.getTermCount();
        for (Operand operand: operandList)
        {
            OperandWalker operandWalker = new OperandWalker(operand);
            operandWalker.visitAllNodes(this);
        }
        // Remove indexMap reference as it is only needed while visiting operands
        indexMap = null;
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
        if ((depth > 1) && !name.isEmpty() && template.isInSameSpace(operand)) 
        {    
            // Set operand archive index or create fix up if a different value is already assigned
            getIndexMap();
            boolean containsKey = indexMap.containsKey(name);
            // Sometimes names appear more than once due to list variable naming
            // Just use a single index value as these variables do not perform unification
            int indexForName = template.getArchetype().getIndexForName(name);
            if (containsKey || (indexForName != -1))
            {
                if (operand.getArchetypeId() == 0)
                    // Index for name already assigned. Assumes meta data is identical to that of original operand.
                    setArchiveIndex(operand, containsKey ? indexMap.get(name) : indexForName);
            }
            else
            {   // Add term meta data to archetype and index map
                int archiveIndex = index++;
                indexMap.put(name, archiveIndex);
                if (archiveIndex >= archetype.getTermCount())
                    archetype.addTerm(new TermMetaData(operand, archiveIndex));
                if (operand.getArchetypeId() == 0)
                    setArchiveIndex(operand, archiveIndex);
            }
            if (operand.getArchetypeId() == 0)
                operand.setArchetypeId(template.getId());
        }
        // Keep going
        return true;
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
