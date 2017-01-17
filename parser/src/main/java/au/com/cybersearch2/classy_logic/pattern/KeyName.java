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
package au.com.cybersearch2.classy_logic.pattern;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;

/**
 * KeyName
 * Axiom key and template name to pair for query
 * @author Andrew Bowley
 * 29 Dec 2014
 */
public class KeyName 
{
    public static QualifiedName EMPTY_QNAME;
    
	/** Axiom key - actually name of axiom */
	protected QualifiedName axiomKey;
	/** Template name */
	protected QualifiedName templateName;

	static
	{
	    EMPTY_QNAME = new QualifiedName("");
	}
	/**
	 * Construct KeyName object
	 * @param axiomKey Axiom key
	 * @param templateName Template name
	 */
	public KeyName(String axiomKey, String templateName) 
	{
		this.axiomKey = QualifiedName.parseName(axiomKey);
		this.templateName = QualifiedName.parseTemplateName(templateName);
	}

	   /**
     * Construct KeyName object with empty axiom key
     * @param templateName Qualified name of template
     */
    public KeyName(QualifiedName templateName) 
    {
        this.axiomKey = EMPTY_QNAME;
        this.templateName = templateName;
    }

	/**
	 * Returns axiom key
	 * @return String
	 */
	public QualifiedName getAxiomKey() 
	{
		return axiomKey;
	}

	/**
	 * Returns template name
	 * @return String
	 */
	public QualifiedName getTemplateName() 
	{
		return templateName;
	}

}
