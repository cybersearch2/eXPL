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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.TermListManager;
import au.com.cybersearch2.classy_logic.terms.LiteralType;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * TemplateArchetype
 * Template factory containing term metat-data including term names
 * @author Andrew Bowley
 * 5May,2017
 */
public class TemplateArchetype extends Archetype<Template, Operand> implements Serializable
{
    private static final long serialVersionUID = 5322860830312952352L;
    
    transient protected Map<QualifiedName,int[]> termMappingMap;
    
    /**
     * Construct TemplateArchetype object
     * @param structureName Qualified name which uniquely identifies the templates being produced -must have a template part
     */
    public TemplateArchetype(QualifiedName structureName)
    {
        super(structureName, StructureType.template);
        if (structureName.getTemplate().isEmpty())
            throw new IllegalArgumentException("Template qualified name must have a template part");
        
    }

    public int[] getTermMapping(TermListManager pairArchetype)
    {
        QualifiedName pairQName = pairArchetype.getQualifiedName();
        int[] termMapping = termMappingMap.get(pairQName);
        if (termMapping == null)
        {
            termMapping = createTermMapping(pairArchetype);
            termMappingMap.put(pairQName, termMapping);
        }
        return termMapping;
    }
    
    protected int[] createTermMapping(TermListManager pairArchetype)
    {
        int[] termMapping = new int[getTermCount()];
        for (int i = 0; i < termMapping.length; i++)
            termMapping[i] = -1;
        int index = 0;
        boolean isAnonymousTerms = pairArchetype.isAnonymousTerms();
        for (TermMetaData termMetaData: termMetaList)
        {
            int pairIndex = -1;
            TermMetaData pairMetaData = null;
            if (isAnonymousTerms)
            {
                if (index == pairArchetype.getTermCount())
                    break;
                pairIndex = index;
                pairMetaData = pairArchetype.getMetaData(pairIndex);
                if ((termMetaData.getLiteralType() == pairMetaData.getLiteralType()) ||
                        (termMetaData.getLiteralType() == LiteralType.object) ||    
                        areConvertibleTypes(termMetaData.getLiteralType(),pairMetaData.getLiteralType() )) 
                {
                    pairArchetype.changeName(index, termMetaData.getName());
                    termMapping[index] = index; 
                }
                else
                    // Remaining items left set to -1 to indicate "no mapping"
                    break;
            }
            else
            {
                pairIndex = pairArchetype.getIndexForName(termMetaData.getName());
                if (pairIndex != -1)
                    termMapping[index] = pairIndex; 
            }
            ++index;
        }
        return termMapping;
    }

    protected boolean areConvertibleTypes(LiteralType literalType,  LiteralType literalType2)
    {
        switch (literalType)
        {
        case integer:
        case xpl_double:
        case decimal:
            switch(literalType2)
            {
            case integer:
            case xpl_double:
            case decimal:
            case string:
                return true;
            default:
            }
        default:
       }
        return false;
    }

    /**
     * Create new Template instance
     * @see au.com.cybersearch2.classy_logic.pattern.Archetype#newInstance(java.util.List)
     */
    @Override
    protected Template newInstance(List<Operand> terms)
    {
        return new Template(this, terms);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Archetype " + structureName.toString();
    }

}
