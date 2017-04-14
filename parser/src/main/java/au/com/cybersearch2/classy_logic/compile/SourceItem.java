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

import au.com.cybersearch2.classy_logic.parser.Token;

/**
 * SourceItem
 * Marks the portion in a source document containing an item of
 * a language unit with a list structure eg. term of template
 * @author Andrew Bowley
 * 7Apr.,2017
 */
public class SourceItem
{
    /** Begin line in source document */
    int beginLine;
    /** Column in first line */
    int beginColumn;
    /** Line in source document */
    int endLine;
    /** Column in last line */
    int endColumn;
    /** Information available from parser eg. Operand.toString() */
    String information;
    /** Next item to form chain */
    SourceItem next;
    
    /**
     * Construct SourceItem object
     * @param token Parser token
     * @param information
     */
    public SourceItem(Token token, String information)
    {
        this.beginLine = token.beginLine;
        this.beginColumn = token.beginColumn;
        this.information = information;
    }

    /**
     * Set end line and column from given parser token
     * @param token
     */
    public void setEnd(Token token)
    {
        setEndLine(token.beginLine);
        // Set end column just before delimiter
        setEndColumn(token.beginColumn - 1);
    }
    
    /**
     * @return the endLine
     */
    public int getEndLine()
    {
        return endLine;
    }

    /**
     * @param endLine the endLine to set
     */
    public void setEndLine(int endLine)
    {
        this.endLine = endLine;
    }

    /**
     * @return the endColumn
     */
    public int getEndColumn()
    {
        return endColumn;
    }

    /**
     * @param endColumn the endColumn to set
     */
    public void setEndColumn(int endColumn)
    {
        this.endColumn = endColumn;
    }

    /**
     * @param information the information to set
     */
    public void setInformation(String information)
    {
        this.information = information;
    }

    /**
     * @return the information
     */
    public String getInformation()
    {
        return information;
    }

    /**
     * @return the next
     */
    public SourceItem getNext()
    {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(SourceItem next)
    {
        this.next = next;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(information);
        builder.append(" (")
                .append(beginLine)
                .append(',')
                .append(beginColumn)
                .append(')')
                .append(" (")
                .append(endLine)
                .append(',')
                .append(endColumn)
                .append(')');
        return builder.toString();
    }

}
