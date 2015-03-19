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

/**
 * KeyName
 * Axiom key and template name to pair for query
 * @author Andrew Bowley
 * 29 Dec 2014
 */
public class KeyName 
{
	/** Axiom key - actually name of axiom */
	protected String axiomKey;
	/** Template name */
	protected String templateName;
	
	/**
	 * Construct KeyName object
	 * @param axiomKey Axiom key
	 * @param templateName Template name
	 */
	public KeyName(String axiomKey, String templateName) 
	{
		this.axiomKey = axiomKey;
		this.templateName = templateName;
	}

	/**
	 * Returns axiom key
	 * @return String
	 */
	public String getAxiomKey() 
	{
		return axiomKey;
	}

	/**
	 * Returns template name
	 * @return String
	 */
	public String getTemplateName() 
	{
		return templateName;
	}

}
