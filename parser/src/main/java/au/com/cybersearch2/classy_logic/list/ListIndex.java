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
package au.com.cybersearch2.classy_logic.list;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ListIndex
 * @author Andrew Bowley
 * 23May,2017
 */
public class ListIndex implements ListItemSpec
{
    String suffix;
    int index;
    Operand indexExpression;
    QualifiedName qname;

    public ListIndex(QualifiedName qname, int index, String suffix)
    {
        this.qname = qname;
        this.index = index;
        this.suffix = suffix;
    }
    
    public ListIndex(QualifiedName qname, Operand indexExpression, String suffix)
    {
        this.qname = qname;
        this.indexExpression = indexExpression;
        index = -1;
        this.suffix = suffix;
    }
    
    @Override
    public String getListName()
    {
        return qname.getName();
    }

    @Override
    public QualifiedName getQualifiedListName()
    {
        return new QualifiedName(getVariableName(qname.getName(), suffix), qname);
    }

    @Override
    public int getItemIndex()
    {
        return index;
    }

    @Override
    public Operand getItemExpression()
    {
        return indexExpression;
    }

    @Override
    public String getSuffix()
    {
        return suffix;
    }
    
    /**
     * Returns variable name given list name and suffix
     * @param listName
     * @param suffix
     * @return String
     */
    protected String getVariableName(String listName, String suffix)
    {
        return listName + "_" + suffix;
    }

}
