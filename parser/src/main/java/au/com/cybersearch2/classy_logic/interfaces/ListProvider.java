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
package au.com.cybersearch2.classy_logic.interfaces;

import java.util.Iterator;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;

/**
 * ListProvider
 * @author Andrew Bowley
 * 13Jul.,2017
 */
public interface ListProvider<T>
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
     * Returns iterator for specified list name  
     * @param listName Name of list
     * @return Iterator
     */
    Iterator<T> iterator(String listName);
    
    /** 
     * Returns listener to notify when an axiom is passed to this provider 
     * @param listName Name of list
     * @return AxiomListener object
     */
    AxiomListener getAxiomListener(String axiomName);
    
    /** 
     * Returns flag to indicate if no axioms are available from the provider 
     * @return boolean
     */
    boolean isEmpty();
}
