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
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermPairList;
import au.com.cybersearch2.classy_logic.interfaces.UnificationPairer;

/**
 * AxiomPairer
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public abstract class AxiomPairer implements UnificationPairer
{
	/** Axiom which is performing unification */
	protected TermList<Term> axiom;
	/** List of paired terms collected by this object */
	protected TermPairList termPairList;
	/** Qualified name for local context */
	protected QualifiedName localContext;

	/**
	 * Construct an AxiomPairer object
	 */
	public AxiomPairer(TermPairList termPairList) 
	{
		this.termPairList = termPairList;
		axiom = AxiomArchetype.EMPTY_AXIOM;
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
			Term otherTerm = axiom.getTermByName(operand.getName());
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
            termPairList.add(operand, otherTerm);
        // Check for exit case: terms in the same name space have different values
        else if (!operand.getValue().equals(otherTerm.getValue()))
        	return !localContext.inSameSpace(operand.getQualifiedName());
        return true;
		
	}

	/**
	 * Set axiom and reset list of term pairs
     * @param axiom Axiom participating in unification
	 */
	public void setAxiom(TermList<Term> axiom) 
	{
        this.axiom = axiom;
        localContext = axiom.getArchetype().getQualifiedName();
	}
	
}
