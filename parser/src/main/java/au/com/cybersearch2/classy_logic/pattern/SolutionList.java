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
package au.com.cybersearch2.classy_logic.pattern;

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermPairList;

/**
 * SolutionList
 * Container for TermPair objects designed to recycle objects for efficiency
 * @author Andrew Bowley
 * 7May,2017
 */
public class SolutionList implements TermPairList
{
    /** Head of list */
    private TermPair termPairHead;
    /** List tail */
    private TermPair termPairTail;
    /** Head of free list */
    private TermPair freeTermPairHead;

    /**
     * add
     * @see au.com.cybersearch2.classy_logic.interfaces.TermPairList#add(au.com.cybersearch2.classy_logic.interfaces.Operand, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public void add(Operand term1, Term term2)
    {
        TermPair nextTermPair = null;
        if (freeTermPairHead == null)
            nextTermPair = new TermPair(term1, term2);
        else
        {
            nextTermPair = freeTermPairHead;
            nextTermPair.setTerm1(term1);
            nextTermPair.setTerm2(term2);
            nextTermPair.setNext(null);
            freeTermPairHead = freeTermPairHead.getNext();
        }
        if (termPairHead == null)
        {
            termPairHead = nextTermPair;
            termPairTail = nextTermPair;
        }
        else
        {
            termPairTail.setNext(nextTermPair);
            termPairTail = nextTermPair;
        }
    }

    /**
     * Returns head of list
     * @return TermPair object
     */
    public TermPair getHead()
    {
        return termPairHead;
    }
 
    /**
     * Returns list to initial state
     */
    public void clearTermPairList()
    {
        freeTermPairHead = termPairHead;
        termPairHead = termPairTail = null;
    }
}
