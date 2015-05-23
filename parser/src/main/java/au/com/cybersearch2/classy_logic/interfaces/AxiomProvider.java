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
package au.com.cybersearch2.classy_logic.interfaces;

import java.util.List;
import java.util.Map;


/**
 * AxiomProvider
 * Sources axioms from persistence system
 * @author Andrew Bowley
 * 11 Feb 2015
 */
public interface AxiomProvider 
{
	/**
	 * Returns Axiom Provider identity
	 * @return String
	 */
	String getName();
	
	/**
	 * Establish resource for specified axiom name and properties
	 * @param axiomName Axiom key
	 * @param properties Optional properties specific to the provider implementation
	 */
	void setResourceProperties(String axiomName, Map<String, Object> properties);
	/**
	 * Returns axiom source for specified axiom name  
	 * @param axiomName Axiom key
	 * @param axiomTermNameList List of axiom term names or null if use defaults
	 * @return AxiomSource object
	 */
	AxiomSource getAxiomSource(String axiomName, List<String> axiomTermNameList);
	/** 
	 * Returns listener to notify when an axiom is passed to this provider 
	 * @return AxiomListener object
	 */
	AxiomListener getAxiomListener();
	/** 
	 * Returns flag to indicate if no axioms are available from the provider 
	 * @return boolean
	 */
	boolean isEmpty();
}
