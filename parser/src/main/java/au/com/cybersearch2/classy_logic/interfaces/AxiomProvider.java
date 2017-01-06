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

import au.com.cybersearch2.classy_logic.expression.ExpressionException;


/**
 * AxiomProvider
 * Interface for an eXPL resource object which receives and transmits axioms on a data connection.
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
	 * Open with specified properties
	 * @param properties Optional properties specific to the provider implementation
	 */
	void open(Map<String, Object> properties)throws ExpressionException;

	/**
	 * Close to free all resources used by provider
	 */
	void close();
	
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
