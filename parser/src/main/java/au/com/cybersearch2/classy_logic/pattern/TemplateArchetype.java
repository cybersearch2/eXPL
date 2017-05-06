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

import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * TemplateArchetype
 * Template factory containing term metat-data including term names
 * @author Andrew Bowley
 * 5May,2017
 */
public class TemplateArchetype extends Archetype<Template, Operand>
{
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
