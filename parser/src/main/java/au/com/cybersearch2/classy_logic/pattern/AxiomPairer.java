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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	/** Compiled regular expresson to extract name components */
	protected Pattern namePattern;

	/**
	 * Construct an AxiomPairer object
	 * @param owner Structure which is performing unification
	 */
	public AxiomPairer(Structure owner) 
	{
		this.owner = owner;
		pairList = new ArrayList<TermPair>();
		namePattern = Pattern.compile("([^.]*)\\.([^.]*)");
	}

	/**
	 * Visit next term
	 * @see au.com.cybersearch2.classy_logic.interfaces.OperandVisitor#next(au.com.cybersearch2.classy_logic.interfaces.Term, int)
	 */
	@Override
	public boolean next(Term term, int depth) 
	{
		if (!term.getName().isEmpty())
		{
			// Pair by name.
		    String termName = term.getName();
		    int pos = termName.lastIndexOf('.');
		    String matchName = pos == -1 ? termName : termName.substring(pos + 1);
			Term otherTerm = owner.getTermByName(matchName);
		    if ((otherTerm != null) && !pairTerms(term, otherTerm))
		        return false;
		}
		return true;
	}

	/**
	 * Process two terms, matched by name or list order, according to their status.
	 * @see au.com.cybersearch2.classy_logic.interfaces.UnificationPairer#pairTerms(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public boolean pairTerms(Term term, Term otherTerm)
	{
		// Pair first term to other term if first term is empty
        if (term.isEmpty())
			pairList.add(new TermPair(term, otherTerm));
        // Check for exit case: both terms non-empty and containing different values
        else if (!otherTerm.isEmpty() && 
        		  !term.getValue().equals(otherTerm.getValue()))
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
	
	public KeyName parseKeyName(String name)
	{
		// Test for compound name
		Matcher matcher = namePattern.matcher(name);
		if (matcher.find())
		{   // Compound name has format "<key>.<name>"
			return new KeyName(matcher.group(1), matcher.group(2));
		}
		return new KeyName("", name);
	}
}
