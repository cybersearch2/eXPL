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

/**
 * Term
 * Interface for all parameters which participate in iterations of unify, evaluate, backup.
 * @author Andrew Bowley
 * 10 Dec 2014
 */
public interface Term
{
    /** Empty name constant */
	public static final String ANONYMOUS = "";

	/**
	 * Returns Parameter value or null if not assigned
	 * @return Object
	 */
	Object getValue();

	/**
	 * Returns Parameter value class or Null.class if value is null
	 * @return Class object
	 */
    Class<?> getValueClass();

    /**
     * Set Parameter name. 
     * This means the Term is annoymous until a name is assigned to it.
     * @param name String
	 * @throws IllegalStateException if name has already been assigned
     */
    void setName(String name);
    
    /**
     * Returns Parameter name
     * @return String
     */
	String getName();

	/**
	 * Returns true if no value has been assigned to this Parameter
	 * @return boolean true if empty
	 */
	boolean isEmpty();

	/**
	 * Backup to intial state if given id matches id assigned on unification . 
	 * @param id Identity of caller or 0 for full backup. 
	 * @return boolean true if backup occurred
	 * @see #unifyTerm(Term otherParam, int id)
	 * @see #evaluate(int id)
	 */
	//boolean backup(int id);

	/**
	 * Perform unification with other Term. 
	 * One Term must be empty and the other not empty.  
	 * If unification is successful, then the two terms will be equivalent. 
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see #backup(int id)
	 */
	int unifyTerm(Term otherTerm, int id);
	
	/**
	 * Evaluate value using data gathered during unification.
	 * @param id Identity of caller, which must be provided for backup()
	 * @return EvaluationStatus.continue if evaluation is to continue
	 */
	//EvaluationStatus evaluate(int id);

    /**
     * Set value, mark Term as not empty
     * @param value Object. If null a Null object will be set and empty status unchanged
     */
	void setValue(Object value);
	
    /**
     * Set value to Null, mark Term as empty and set id to 0
     */
     void clearValue();
	
	/**
     * Returns id
     * @return int
     */
    int getId(); 

}