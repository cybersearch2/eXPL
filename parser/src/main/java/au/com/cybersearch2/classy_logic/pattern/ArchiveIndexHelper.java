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
 * Set archive data for Operands in evaluation tree 
 * @author Andrew Bowley
 * 11May,2017
 */
public class ArchiveIndexHelper implements OperandVisitor
{
    static class FixUp
    {
        public Operand operand;
        public int preFixIndex;
        public int postFixIndex;
    }
    
    private int index;
    private Map<String, Integer> indexMap;
    private List<FixUp> fixUpList;
    private TermListManager archetype;
    private TermList<Operand> termList;
    private QualifiedName[] contextNames;
    
    public ArchiveIndexHelper(TermListManager archetype, TermList<Operand> termList, QualifiedName... contextNames)
    {
        this.archetype = archetype;
        this.termList = termList;
        this.contextNames = contextNames;
        indexMap = new HashMap<String, Integer>();
        fixUpList = Collections.emptyList();
    }
    
    /**
     * Set Term meta data for Operands in evaluation tree. 
     */
    public List<FixUp> setOperandTree() 
    {
        // Now analyse operands in operand trees
        // Use set to avoid duplicates
        indexMap.clear();
        fixUpList = new ArrayList<FixUp>();
        index = archetype.getTermCount();
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
        indexMap.clear();
        archetype.clearMutable();
        return fixUpList;
    }

    @Override
    public boolean next(Operand operand, int depth)
    {
        String name = operand.getName();
        if (!name.isEmpty())
        {
            if (inSameSpace(operand))
            {
                if (indexMap.containsKey(name))
                {
                    int archiveIndex = indexMap.get(name);
                    setArchiveIndex(operand, archiveIndex);
                }
                else
                {
                    int archiveIndex = index++;
                    indexMap.put(name, archiveIndex);
                    setArchiveIndex(operand, archiveIndex);
                    archetype.addTerm(new TermMetaData(operand, archiveIndex));
                }
            }
            else
                unsetArchiveIndex(operand);
        }
        return true;
    }

    private boolean inSameSpace(Operand operand)
    {
        for (QualifiedName contextName: contextNames)
            if (contextName.inSameSpace(operand.getQualifiedName())) 
                return true;
        return false;
    }

    private void setArchiveIndex(Operand operand, int archiveIndex)
    {
        if (operand.getArchetypeIndex() == -1)
            operand.setArchetypeIndex(archiveIndex);
        else if (operand.getArchetypeIndex() != archiveIndex)
            addFixUp(operand, archiveIndex);
    }

    private void unsetArchiveIndex(Operand operand)
    {
        int archiveIndex = operand.getArchetypeIndex();
        if (archiveIndex != -1)
            addFixUp(operand, -1);
    }

    protected void addFixUp(Operand operand, int archiveIndex)
    {
        FixUp fixUp = new FixUp();
        fixUp.operand = operand;
        fixUp.preFixIndex = fixUp.operand.getArchetypeIndex();
        fixUp.postFixIndex = archiveIndex;
        fixUpList.add(fixUp);
    }


}
