/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.helper;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * QualifiedName
 * Three-part name consisting of scope, template and name.
 * QualifiedName objects are ordered according to scope, template and name.
 * @author Andrew Bowley
 * 22 Aug 2015
 */
public class QualifiedName implements Comparable<QualifiedName>, Serializable
{
    private static final long serialVersionUID = 3872140142266578675L;
    
    public static String EMPTY;
    public static QualifiedName ANONYMOUS;
    
    static 
    {
        EMPTY = "";
        ANONYMOUS = new QualifiedName(EMPTY, EMPTY, Term.ANONYMOUS);
    }
    
    protected String scope;
    protected String template;
    protected String name;
    transient protected AtomicInteger referenceCount;

    /**
     * Construct name-only QualifiedName in global namespace
     * @param name
     */
    public QualifiedName(String name)
    {
        this(EMPTY, EMPTY, name);
    }

    /**
     * Construct QualifiedName object from name and qualified context name  
     * @param name Name-only part of new qualified name
     * @param contextName Context qualified name supplying scope and template parts of new qualified name
     */
    public QualifiedName(String name, QualifiedName contextName)
    {
        this(contextName.scope, contextName.template, name);
    }

    /**
     * Construct QualifiedName object from namee and scope parts
     * @param template
     * @param name
     */
    public QualifiedName(String scope, String name)
    {
        this(QueryProgram.GLOBAL_SCOPE.equals(scope) ? EMPTY : scope, EMPTY, name);
    }

    /**
     * Construct QualifiedName object from separate components
     * @param scope
     * @param template
     * @param name
     */
    public QualifiedName(String scope, String template, String name)
    {
        this.scope = scope;
        this.template = template == null ? EMPTY : template;
        this.name = name;
    }

    /** 
     * Clear template component so qualified name is changed to scope namespace
     */
    public void clearTemplate()
    {
        template = EMPTY;
    }
    
    /** 
     * Clear scope component so qualified name is changed to global scope namespace
     */
    public void clearScope()
    {
        scope = EMPTY;
    }
 
    /**
     * compareTo
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(QualifiedName anotherQualifiedName)
    {
        int scopeComp = scope.compareTo(anotherQualifiedName.scope);
        int templateComp = template.compareTo(anotherQualifiedName.template);
        if (scopeComp == 0)
        {
            if (templateComp == 0)
               return name.compareTo(anotherQualifiedName.name);
            return templateComp;
        }
        return scopeComp;
    }

    /**
     * hashCode
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return scope.hashCode() ^ template.hashCode() ^ name.hashCode();
    }

    /**
     * equals
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (!(obj instanceof QualifiedName))
            return false;
        QualifiedName qualifiedName = (QualifiedName)obj;
        return scope.equals(qualifiedName.scope) &&
                template.equals(qualifiedName.template) &&
                name.equals(qualifiedName.name);
    }

    /**
     * toString - Display qualified name with non-empty parts separated with dot character
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(EMPTY);
        if (!scope.isEmpty())
            builder.append(scope).append('.');
        if (!template.isEmpty())
        {
            builder.append(template);
            if (!name.isEmpty())
                builder.append('.');
        }
        builder.append(name);
        return builder.toString();
    }

    /**
     * Returns scope
     * @return String
     */
    public String getScope()
    {
        return scope;
    }

    /**
     * Returns template
     * @return String
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * Returns name
     * @return String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Converts text to QualifiedName. Two part names are placed in scope namespace.  
     * @param text
     * @return QualifiedName object
     */
    public static QualifiedName parseName(String text)
    {
        String[] parts = text.split("\\.");
        if (parts.length > 3)
            throw new ExpressionException("Qualified name \"" + text + "\" is invalid");
        final String name = parts[parts.length - 1];
        if (parts.length == 1)
            return new QualifiedName(name, QualifiedName.ANONYMOUS);
        else if (parts.length == 3)
            return new QualifiedName(parts[0], parts[1], name);
        return new QualifiedName(parts[0], QualifiedName.EMPTY, name);
    }

    /**
     * Returns QualifiedName for 1 or 2-part name in template namespace
     * Use parseName() for 3-part name
     * @param text 1 or 2-part template name name expected
     * @return QualifiedName object
     */
    public static QualifiedName parseTemplateName(String text)
    {
        String[] parts = text.split("\\.");
        if (parts.length > 3)
            throw new ExpressionException("Qualified name \"" + text + "\" is invalid");
        final String name = parts[parts.length - 1];
        if (parts.length == 1)
            return new QualifiedName(QualifiedName.EMPTY, name, QualifiedName.EMPTY);
        else if (parts.length == 3)
            return new QualifiedName(parts[0], parts[1], name);
        return new QualifiedName(parts[0], name, QualifiedName.EMPTY );
    }
 
    /**
     * Returns QualifiedName for 1 or 2-part name in global scope
     * Use parseName() for 3-part name
     * @param text 1 or 2-part global scope name expected
     * @return QualifiedName object
     */
    public static QualifiedName parseGlobalName(String text)
    {
        String[] parts = text.split("\\.");
        if (parts.length == 0)
        {
            // Make parts valid
            parts = new String[1];
            parts[0] = text;
        }
        if (parts.length > 3)
            throw new ExpressionException("Qualified name \"" + text + "\" is invalid");
        final String name = parts[parts.length - 1];
        if (parts.length == 1)
            return new QualifiedName(QualifiedName.EMPTY, QualifiedName.EMPTY, name);
        else if (parts.length == 3)
            return new QualifiedName(parts[0], parts[1], name);
        return new QualifiedName(QualifiedName.EMPTY , parts[0], name);
    }

    /**
     * Returns QualifiedName for text using context qualified name to supply missing parts
     * @param text 1 or 2-part name in text format
     * @param qualifiedContextname Context qualified name supplying scope and template parts of new qualified name
     * @return QualifiedName object
     */
    public static QualifiedName parseName(String text, QualifiedName qualifiedContextname)
    {
        // If in template context, assume 2-part name is template name
        QualifiedName qname = qualifiedContextname.template.isEmpty() ? parseName(text) : parseGlobalName(text);
        if (qname.scope.isEmpty())
            qname.scope = qualifiedContextname.scope;
        if (qname.template.isEmpty())
            qname.template = qualifiedContextname.template;
        return qname;
    }

    /**
     * Returns flag set true if supplied specified qualified name is in same namespace as this one
     * @param qname Context qualified name - only scope and template parts are relevant.
     * @return boolean
     */
    public boolean inSameSpace(QualifiedName qname)
    {
        if (qname == null)
            throw new IllegalArgumentException("Parameter qname is null");
        // Unqualified name is always in same space
        if (qname.scope.isEmpty() && qname.template.isEmpty())
            return true;
        if (qname.template.equals(template) || qname.template.isEmpty())
            return qname.scope.equals(scope);
        return false;
    }

    public int incrementReferenceCount()
    {
        if (referenceCount == null)
            referenceCount = new AtomicInteger();
        return referenceCount.getAndIncrement();
    }
}
