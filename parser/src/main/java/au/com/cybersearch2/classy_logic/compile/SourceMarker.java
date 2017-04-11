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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.parser.Token;
import au.com.cybersearch2.classy_logic.parser.QueryParserConstants;

/**
 * SourceMarker
 * Associates uniquely identifiable code elements with source location ie. line and column
 * @author Andrew Bowley
 * 6Apr.,2017
 */
public class SourceMarker implements Comparable<SourceMarker>
{
    /** Unique identifier */
    QualifiedName qualifiedName;
    /** String literal enumeration */
    XplLiteral literal;
    /** Line in source document */
    int line;
    /** Column in line */
    int column;
    /** Source document identity */
    int sourceDocumentId;
    /** Head source item in list of contained items */
    SourceItem headSourceItem;
    /** Tail source item in list of contained items */
    SourceItem tailSourceItem;
   

    /**
     * Construct SourceMarker object
     * @param qualifiedName
     * @param literal
     * @param line
     * @param column
     * @param sourceDocument
     */
    public SourceMarker(
            QualifiedName qualifiedName, 
            XplLiteral literal,
            int line, 
            int column, 
            int sourceDocumentId)
    {
        super();
        this.qualifiedName = qualifiedName;
        this.literal = literal;
        this.line = line;
        this.column = column;
        this.sourceDocumentId = sourceDocumentId;
    }

    /**
     * Construct SourceMarker from parser token
     * @param token Parser token
     */
    public SourceMarker(Token token)
    {
        this.line = token.beginLine;
        this.column = token.beginColumn;
        switch(token.kind)
        {
        case QueryParserConstants.AXIOM: 
            literal = XplLiteral.axiom; 
            break;
        case QueryParserConstants.BOOLEAN:
            literal = XplLiteral.xpl_boolean; 
            break;
        case QueryParserConstants.CALC:
            literal = XplLiteral.calc; 
            break;
        case QueryParserConstants.CHOICE:
            literal = XplLiteral.choice; 
            break;
        case QueryParserConstants.CURRENCY:
            literal = XplLiteral.currency; 
            break;
        case QueryParserConstants.DECIMAL:
            literal = XplLiteral.decimal; 
            break;
        case QueryParserConstants.DOUBLE:
            literal = XplLiteral.xpl_double; 
            break;
        case QueryParserConstants.FACT:
            literal = XplLiteral.fact; 
            break;
        case QueryParserConstants.FORMAT:
            literal = XplLiteral.format; 
            break;
        case QueryParserConstants.INCLUDE:
            literal = XplLiteral.include; 
            break;
        case QueryParserConstants.INTEGER:
            literal = XplLiteral.integer; 
            break;
        case QueryParserConstants.LENGTH:
            literal = XplLiteral.length; 
            break;
        case QueryParserConstants.LIST:
            literal = XplLiteral.list; 
            break;
        case QueryParserConstants.LOCAL:
            literal = XplLiteral.local; 
            break;
        case QueryParserConstants.PARAMETER:
            literal = XplLiteral.parameter; 
            break;
        case QueryParserConstants.QUERY:
            literal = XplLiteral.query; 
            break;
        case QueryParserConstants.REGEX:
            literal = XplLiteral.regex; 
            break;
        case QueryParserConstants.RESOURCE:
            literal = XplLiteral.resource; 
            break;
        case QueryParserConstants.SCOPE:
            literal = XplLiteral.scope; 
            break;
        case QueryParserConstants.STRING:
            literal = XplLiteral.string; 
            break;
        case QueryParserConstants.TEMPLATE:
            literal = XplLiteral.template; 
            break;
        case QueryParserConstants.TERM:
            literal = XplLiteral.term; 
            break;
        case QueryParserConstants.UNKNOWN:
            literal = XplLiteral.unknown; 
            break;
        case QueryParserConstants.IDENTIFIER:
            literal = XplLiteral.variable; 
            break;
        default:
            literal = XplLiteral.post_release; 
        }
    }
    
    /**
     * @param qualifiedName the qualifiedName to set
     */
    public void setQualifiedName(QualifiedName qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }

    /**
     * @param sourceDocumentId the sourceDocumentId to set
     */
    public void setSourceDocumentId(int sourceDocumentId)
    {
        this.sourceDocumentId = sourceDocumentId;
    }

    /**
     * @return the qualifiedName
     */
    public QualifiedName getQualifiedName()
    {
        return qualifiedName;
    }

    /**
     * @return the literal
     */
    public XplLiteral getLiteral()
    {
        return literal;
    }

    /**
     * @return the line
     */
    public int getLine()
    {
        return line;
    }

    /**
     * @return the column
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * @return the sourceDocumentId
     */
    public int getSourceDocumentId()
    {
        return sourceDocumentId;
    }

    /**
     * compareTo - Order SourceMarker objects by qualified name
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SourceMarker other)
    {
        int comparison = qualifiedName.compareTo(other.qualifiedName);
        if (comparison == 0)
            return literal.compareTo(other.literal);
        return comparison;
    }

    public void addSourceItem(SourceItem sourceItem)
    {
        if (headSourceItem == null)
        {
            headSourceItem = sourceItem;
            tailSourceItem = sourceItem;
        }
        else
        {
            tailSourceItem.setNext(sourceItem);
            tailSourceItem = sourceItem;
        }
    }
    
    /**
     * @return the headSourceItem
     */
    public SourceItem getHeadSourceItem()
    {
        return headSourceItem;
    }

    /**
     * @return the tailSourceItem
     */
    public SourceItem getTailSourceItem()
    {
        return tailSourceItem;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(literal.toString())
                .append(' ')
                .append(qualifiedName)
                .append(" (")
                .append(line)
                .append(',')
                .append(column)
                .append(')');
        return builder.toString();
    }

}
