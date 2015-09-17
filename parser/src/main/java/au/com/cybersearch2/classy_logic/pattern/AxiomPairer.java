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

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.UnificationPairer;
import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;

/**
 * AxiomPairer
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public class AxiomPairer implements UnificationPairer
{
	/** Axiom which is performing unification */
	protected Structure owner;
	/** List of paired terms collected by this object */
	protected List<TermPair> pairList;
	/** Qualified name for local context */
	protected QualifiedName localContext;

	/**
	 * Construct an AxiomPairer object
	 * @param owner Structure which is performing unification
	 */
	public AxiomPairer(Structure owner, QualifiedName localContext) 
	{
		this.owner = owner;
		this.localContext = localContext;
		pairList = new ArrayList<TermPair>();
	}

	/**
	 * Visit next term
	 * @see au.com.cybersearch2.classy_logic.interfaces.OperandVisitor#next(au.com.cybersearch2.classy_logic.interfaces.Operand, int)
	 */
	@Override
	public boolean next(Operand operand, int depth) 
	{
		if (!operand.getName().isEmpty())
		{
			// Pair by name. 
			Term otherTerm = owner.getTermByName(operand.getName());
		    if ((otherTerm != null) && !pairTerms(operand, otherTerm))
		        return false;
		}
		return true;
	}

	/**
	 * Process two terms, matched by name or list order, according to their status.
	 * @see au.com.cybersearch2.classy_logic.interfaces.UnificationPairer#pairTerms(au.com.cybersearch2.classy_logic.interfaces.Operand, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public boolean pairTerms(Operand operand, Term otherTerm)
	{
		// Pair first term to other term if first term is empty
        if (operand.isEmpty())
			pairList.add(new TermPair(operand, otherTerm));
        // Check for exit case: both terms non-empty and containing different values
        else if (!otherTerm.isEmpty() && localContext.inSameSpace(operand.getQualifiedName()) &&
        		  !operand.getValue().equals(otherTerm.getValue()))
        	return false;
        return true;
		
	}

	/**
	 * * Returns list of Term pairs to be unified
	 * @see au.com.cybersearch2.classy_logic.interfaces.UnificationPairer#getPairList()
	 */
	@Override
	public List<TermPair> getPairList()
	{
		return pairList;
	}

	/**
	 * Reset list of term pairs
	 */
	public void reset() 
	{
		pairList.clear();
	}
	
}
