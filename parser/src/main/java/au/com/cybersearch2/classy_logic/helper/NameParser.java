/**
    Copyright (C) 2014  www.cybersearch2.com.au

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;

/**
 * NameParser
 * Utility class to parse qualified name in text format
 * @author Andrew Bowley
 * 7 Jun 2015
 */
public class NameParser 
{
    final static char DOT = '.';
    final static String IDENTIFIER = "[a-zA-Z0-9][a-zA-Z_0-9]*";
    final static String NAME_REGEX = "^(" + IDENTIFIER + ")(\\.)?(" + IDENTIFIER + ")?$" ;
    final static String NULL_KEY_MESSAGE = "Null key passed to name parser";

    /** Pre-compiled pattern */
    protected Pattern pattern;
    /** Scope part */
    protected String scope;
    /** Template part */
    protected String template;
    /** Name part */
    protected String name;

    /**
     * Construct NameParser instance
     * @param name Qualified name in text format
     */
    public NameParser(String name)
    {
        this.scope = QualifiedName.EMPTY;
        this.template = QualifiedName.EMPTY;
        this.name = QualifiedName.EMPTY;
        if (name.isEmpty())
            return;
        String[] fragments = name.split("@");
        if ((fragments.length > 2) || ((fragments.length == 2) && name.endsWith("@")))
            throw new ExpressionException("Name \"" + name + "\" with more than one \"@\" is invalid");
        if ((fragments.length < 2) && !name.endsWith("@"))
        {   // Parse part name
            parseGlobalName(fragments[0]);
            return;
        }
        // Parse artifact name
        try
        {
            pattern = Pattern.compile(NAME_REGEX, 0);
        }
        catch(PatternSyntaxException e)
        {   // This is not expected
            throw new ExpressionException("Error in regular expression", e);
        }
        // Use pattern to decompose name fragments each side of "@" into parts
        String[] groupValues1 = new String[0];
        Matcher matcher = pattern.matcher(fragments[0]);
        if (matcher.find())
            groupValues1 = getGroups(matcher);
        if (groupValues1.length == 0)
            throw new ExpressionException("Name \"" + name + "\" is invalid");
        String[] groupValues2 = new String[0];
        if (fragments.length == 2)
        {
            matcher = pattern.matcher(fragments[1]);
            if (matcher.find())
                groupValues2 = getGroups(matcher);
            if (groupValues2.length == 0)
                throw new ExpressionException("Name \"" + name + "\" is invalid after \"@\"");
        }
        if ((groupValues1.length == 4) && (groupValues2.length == 4))
            throw new ExpressionException("Name \"" + name + "\" with more than 3 parts is invalid");
        String templatePart = null;
        String scopePart = null;
        // With @, so name comes first
        String namePart = groupValues1[1];
        if (groupValues1.length == 4)
        {
            templatePart = groupValues1[3];
            if (groupValues2.length > 1)
                scopePart = groupValues2[1];
        }
        else if (groupValues2.length == 4)
        {
            templatePart = groupValues2[1];
            scopePart = groupValues2[3];
        }
        else if (groupValues2.length > 0)
            scopePart = groupValues2[1];
        if (templatePart == null)
            templatePart = QualifiedName.EMPTY;
        if (scopePart == null)
            scopePart = QualifiedName.EMPTY;
        this.scope = scopePart;
        this.template = templatePart;
        this.name = namePart;
    }

    /**
     * Returns Qualified name from parsed name
     * @return QualifiedName object
     */
    public QualifiedName getQualifiedName()
    {
        return new QualifiedName(scope, template, name);
    }
    
    /**
     * @return the scope
     */
    public String getScope()
    {
        return scope;
    }

    /**
     * @return the template
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * toString - Display qualified name with non-empty parts separated with dot character
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(QualifiedName.EMPTY);
        if (!scope.isEmpty())
            builder.append(scope).append('.');
        if (!template.isEmpty())
        {
            builder.append(template);
            if (!getName().isEmpty())
                builder.append('.');
        }
        builder.append(name);
        return builder.toString();
    }

    /**
     * Decompose part name, assuming 2-part name is template name in global scope
     * @param text Formated text
     */
    private void parseGlobalName(String text)
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
        this.name = parts[parts.length - 1];
        if (parts.length == 3)
        {
            this.scope = parts[0];
            this.template= parts[1];
        }
        else if (parts.length == 2)
            this.template= parts[0];
    }

    /**
     * Returns values for group 0 and above as array
     * @param matcher Matcher object in matched state
     * @return String array
     */
    protected static String[] getGroups(Matcher matcher)
    {
        List<String> groupList = new ArrayList<String>(matcher.groupCount() + 1);
        for (int i = 0; i < matcher.groupCount() + 1; i++)
        {
            String group = matcher.group(i);
            if (group != null)
                groupList.add(group);
        }
        return groupList.toArray(new String[groupList.size()]);
    }

    /**
     * Returns name part of key
     * @param key
     * @return String
     */
    public static String getNamePart(String key)
    {
        if (key == null)
            throw new IllegalArgumentException(NULL_KEY_MESSAGE);
        int dot = key.lastIndexOf(DOT);
        if (dot != -1)
            return key.substring(dot + 1);
        return key;
    }

    /**
     * Returns scope part of key or name of Global Scope if not in key
     * @param key
     * @return String
     */
    public static String getScopePart(String key)
    {
        if (key == null)
            throw new IllegalArgumentException(NULL_KEY_MESSAGE);
        int dot = key.indexOf(DOT);
        if (dot != -1)
            return key.substring(0, dot);
        return QueryProgram.GLOBAL_SCOPE;
    }
}
