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
package au.com.cybersearch2.classy_logic.query;

import java.util.Deque;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * ChainQuery
 * Abstract base class for all chain queries. 
 * The chain is formed by each query linking to the next.
 * @author Andrew Bowley
 * 15 Dec 2014
 */
public abstract class ChainQuery 
{
	/** Next query or null if tail of chain */
 	protected ChainQuery next;

 	/**
 	 * Execute query and if not tail, chain to next.
 	 * Sub classes override this method and call it upon completion to handle the chaining
 	 * @param solution The object which stores the query results
     * @param templateChain Template chain to manage same query repeated in different scopes
	 * @return EvaluationStatus enum: SHORT_CIRCUIT, SKIP or COMPLETE
 	 */
	public EvaluationStatus executeQuery(Solution solution, Deque<Template> templateChain)
	{
		return next == null ? EvaluationStatus.COMPLETE : next.executeQuery(solution, templateChain);
 	}

	/**
	 * Set next query in the chain
	 * @param next
	 */
	protected void setNext(ChainQuery next) 
	{
		this.next = next;
	}

	/**
	 * Returns next query in the chain or null if tail
	 * @return ChainQuery object or null if this is the tail 
	 */
	public ChainQuery getNext() 
	{
		return next;
	}

 	/**
	 * Backup to state before previous unification
	 */
	abstract protected void backupToStart();

	/**
	 * Force reset to initial state
	 */
	abstract protected void reset();

	/**
	 * Set axiom listener to receive each solution as it is produced
	 * @param qname Reference to axiom by qualified name
	 * @param axiomListener The axiom listener object
	 */
	abstract void setAxiomListener(QualifiedName qname, AxiomListener axiomListener);

    protected void backup()
    {
    } 
}
