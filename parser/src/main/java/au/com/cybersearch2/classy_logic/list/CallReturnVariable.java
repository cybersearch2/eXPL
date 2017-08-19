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
package au.com.cybersearch2.classy_logic.list;

import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * TermVariable
 * Evaluates to resolve list from call return variable
 * @author Andrew Bowley
 * 22May,2017
 */
public class CallReturnVariable extends Variable
{
    /** Axiom term list to reference */
    protected AxiomTermList itemList;
    
    /**
     * Construct TermVariable object
     * @param qname Qualified name
     * @param itemList Axiom term list to reference
     */
    public CallReturnVariable(QualifiedName qname, AxiomTermList itemList)
    {
        super(qname);
        this.itemList = itemList;
    }

    /**
     * Evaluate term value after unification stage has set index expression.
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        this.id = id;
        Term term = itemList.getAxiom().getTermByIndex(0);
        if (!ItemList.class.isAssignableFrom(term.getValueClass()))
            // Return this value wrapped by the item list
            setValue(itemList);
        else
            setValue(term.getValue());
        return EvaluationStatus.COMPLETE;
    }
    
    @Override
    public boolean backup(int id)
    {
        return super.backup(id);
    }
}
