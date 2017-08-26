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
 * @see NameParser
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
    
    protected int name;
    protected int scope;
    protected int template;
    protected String[] parts;
    transient protected String source;
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
     * Construct QualifiedName object from name using context name to provide defaults for empty parts 
     * @param name Formatted qualified name
     * @param contextName Context qualified name
     */
    public QualifiedName(String name, QualifiedName contextName)
    {
        NameParser nameParser = new NameParser(name);
        String scope = nameParser.getScope();
        if (scope.isEmpty() && !contextName.getScope().isEmpty())
            scope = contextName.getScope();
        String template = nameParser.getTemplate(); 
        if (template.isEmpty() && !contextName.getTemplate().isEmpty())
            template = contextName.getTemplate();
        setParts(scope, template, nameParser.getName());
        // Preserve source name for post construction analysis
        source = nameParser.toString();
    }

    /**
     * Construct QualifiedName object from namee and scope parts
     * @param template
     * @param name
     */
    public QualifiedName(String scope, String name)
    {
        this(scope, EMPTY, name);
    }

    /**
     * Construct QualifiedName object from separate components
     * @param scope
     * @param template
     * @param name
     */
    public QualifiedName(String scope, String template, String name)
    {
        setParts(scope, template, name);
        source = toString();
    }
    
    /**
     * Construct QualifiedName object copy
     * @param qname Qualified name to copy
     */
    public QualifiedName(QualifiedName qname)
    {
        if (qname.parts ==null)
        {
            this.template = -1;
            this.scope = -1;
            this.name = -1;
            return;
        }
        parts = new String[qname.parts.length];
        System.arraycopy(qname.parts, 0, parts, 0, qname.parts.length);
        this.template = qname.template;
        this.scope = qname.scope;
        this.name = qname.name;
    }
    
    /**
     * Returns scope
     * @return String
     */
    public String getScope()
    {
        return scope == -1 ? EMPTY : parts[scope];
    }

    /**
     * Set scope part to specied name. Create space slot first if none available.
     * @param scopeName
     */
    public void setScope(String scopeName)
    {
        if (scope == -1)
        {
            String[] newParts = new String[parts.length + 1];
            System.arraycopy(parts, 0, newParts, 1, parts.length);
            parts = newParts;
            scope = 0;
            if (template != -1)
                template += 1;
            if (name != -1)
                name += 1;
        }
        parts[scope] = scopeName;
    }
    
    /**
     * Returns template
     * @return String
     */
    public String getTemplate()
    {
        return template == -1 ? EMPTY : parts[template];
    }

    /**
     * Returns name
     * @return String
     */
    public String getName()
    {
        return name == -1 ? EMPTY : parts[name];
    }

    /**
     * Returns flag set true if this name is global
     * @return
     */
    public boolean isGlobalName()
    {
        return scope == -1 && template == -1;
    }

    /**
     * Convert this scope name to template name.
     * @return this QualifiedName
     */
    public QualifiedName toTemplateName()
    {
        if (template != -1)
            scope = template;
        if (name != -1)
        {
            template = name;
            name = -1;
        }
        return this;
    }

    /**
     * Convert this template name to scope name.
     * @return this QualifiedName
     */
    public QualifiedName toScopeName()
    {
        if (template != -1)
        {
            if (scope != -1)
                parts[template] = EMPTY;
            scope = template;
            template = -1;
        }
        return this;
    }
    
    /**
     * Convert this name to context name in which template is empty and scope is set to "scope" 
     */
    public void toContextName()
    {
        switch (parts.length)
        {
        case 3:
            parts[0] = "scope";
            parts[1] = EMPTY;
            template = -1;
            break;
        case 2:
            parts[0] = "scope";
            scope = 0;
            template = -1;
            break;
        case 1:
        {   // Create slot for scope
            name = 1;
            String name = parts[0];
            parts = new String[2];
            parts[1] = name;
            parts[0] = "scope";
            scope = 0;
            break;
        }
        default:
        }
    }
    
    /** 
     * Clear template component so qualified name is changed to scope namespace
     */
    public void clearTemplate()
    {
        if (template != -1)
            parts[template] = EMPTY;
        template = -1;
    }
    
    /** 
     * Clear scope component so qualified name is changed to global scope namespace
     */
    public void clearScope()
    {
        if (scope != -1)
            parts[scope] = EMPTY;
        scope = -1;
    }
 
    /**
     * @return the name in part format used to construct this object 
     */
    public String getSource()
    {
        return source;
    }

    /**
     * compareTo
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(QualifiedName anotherQualifiedName)
    {
        int scopeComp = scope - anotherQualifiedName.scope;
        if ((scopeComp == 0) && (scope != -1)) 
            scopeComp = getScope().compareTo(anotherQualifiedName.getScope());
        int templateComp = template - anotherQualifiedName.template;
        if ((templateComp == 0) && (template != -1)) 
            templateComp = getTemplate().compareTo(anotherQualifiedName.getTemplate());
        if (scopeComp == 0)
        {
            if (templateComp == 0)
               return getName().compareTo(anotherQualifiedName.getName());
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
        return getScope().hashCode() ^ getTemplate().hashCode() ^ getName().hashCode();
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
        return compareTo(qualifiedName) == 0;
    }

    /**
     * toString - Display qualified name with non-empty parts separated with dot character
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(EMPTY);
        if (scope != -1)
            builder.append(getScope()).append('.');
        if (template != -1)
        {
            builder.append(getTemplate());
            if (!getName().isEmpty())
                builder.append('.');
        }
        builder.append(getName());
        return builder.toString();
    }

    /**
     * Construct QualifiedName object from separate components
     * @param scope
     * @param template
     * @param name
     */
    protected void setParts(String scope, String template, String name)
    {
       if (QueryProgram.GLOBAL_SCOPE.equals(scope) || (scope == null) || scope.isEmpty())
            this.scope = 0;
        else
            this.scope = 1;
        if ((template == null) || template.isEmpty())
            this.template = 0;
        else
            this.template = 1;
        if ((name == null) || name.isEmpty())
            this.name = 0;
        else
            this.name = 1;
        int length = this.name + this.scope + this.template;
        if (length > 0)
            parts = new String[length];
        switch (length)
        {
        case 3:
            this.scope = 0;
            parts[this.scope] = scope;
            this.template = 1;
            parts[this.template] = template;
            this.name = 2;
            parts[this.name] = name;
            break;
        case 2:
        case 1:
        { 
            int index = 0;
            if (this.scope == 1)
            {
                this.scope = index++;
                parts[this.scope] = scope;
            }
            else
                this.scope = -1;
            if (this.template == 1)
            {
                this.template = index++;
                parts[this.template] = template;
            }
            else
                this.template = -1;
            if (this.name == 1)
            {
                this.name = index;
                parts[this.name] = name;
            }
            else
                this.name = -1;
            break;
        }
        default:
            this.template = -1;
            this.scope = -1;
            this.name = -1;
        }
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
        if (text.startsWith(".") || text.endsWith("."))
            throw new ExpressionException("Qualified name \"" + text + "\" missing part");
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
        QualifiedName qname = qualifiedContextname.template == -1 ? parseName(text) : parseGlobalName(text);
        String newScope = null;
        String newTemplate = null;
        if ((qname.scope == -1) && (qualifiedContextname.scope != -1))
            newScope = qualifiedContextname.getScope();
        if ((qname.template == -1) && (qualifiedContextname.template != -1))
            newTemplate = qualifiedContextname.getTemplate();
        boolean replace = false;
        if (newScope != null)
            replace = true;
        else
            newScope = qname.getScope();
        if (newTemplate != null)
            replace = true;
        else
            newTemplate = qname.getTemplate();
        return replace ? new QualifiedName(newScope, newTemplate, qname.getName()) : qname;
    }

    /**
     * Returns qualified name as axiom version of supplied template name
     * @param templateName Qualified template name
     * @return QualifiedName object
     */
    public static QualifiedName axiomFromTemplate(QualifiedName templateName)
    {
        return new QualifiedName(templateName.getScope(),  templateName.getTemplate());
    }

    /**
     * Returns qualified name as template version of supplied axiom name
     * @param axiomName Qualified axiom name
     * @return QualifiedName object
     */
    public static QualifiedName templateFromAxiom(QualifiedName axiomName)
    {
        return new QualifiedTemplateName(axiomName.getScope(),  axiomName.getName());
    }

    /**
     * Returns flag set true if scope is empty
     * @return boolean
     */
    public boolean isScopeEmpty()
    {
        return scope == -1;
    }
    
    /**
     * Returns flag set true if template is empty
     * @return boolean
     */
    public boolean isTemplateEmpty()
    {
        return template == -1;
    }
    
    /**
     * Returns flag set true if name is empty
     * @return boolean
     */
   public boolean isNameEmpty()
    {
        return name == -1;
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
        if (qname.scope == -1 && qname.template == -1)
            return true;
        if (qname.getTemplate().equals(getTemplate()) || (qname.template == -1))
            return qname.getScope().equals(getScope());
        return false;
    }

    /**
     * Returns reference count value prior to increment 
     * @return positive number
     */
    public int incrementReferenceCount()
    {
        if (referenceCount == null)
            referenceCount = new AtomicInteger();
        return referenceCount.getAndIncrement();
    }
}
