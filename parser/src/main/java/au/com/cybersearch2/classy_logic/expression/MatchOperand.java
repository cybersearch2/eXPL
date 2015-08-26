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
package au.com.cybersearch2.classy_logic.expression;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * MatchOperand
 * Evaluates comparison of literal values. 
 * The evaluation returns false on no match which causes unification to short circuit. 
 * @author Andrew Bowley
 * 5 Jun 2015
 */
public class MatchOperand extends Variable
{
    
    /**
     * Construct MatchOperand object
     * @param qname Qualified name
     * @param literal The literal value to match on
     */
    public MatchOperand(QualifiedName qname, Operand expression)
    {
        super(qname, expression);
    }

    /**
     * Evaluate value using data gathered during unification.
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    @Override
    public EvaluationStatus evaluate(int id) 
    {
        // Save value populated by unification
        Object selectionValue = value;
        clearValue();
        // Evaluate match value
        super.evaluate(id);
        // Retain value on match
        boolean isMatch = value.equals(selectionValue);
        if (!isMatch)
        {   // Handle Null match value
            if (value.equals(null))
            {
                isMatch = true; // Null is default match
                setValue(selectionValue);
            }
            else // No match is same as unification failed
                clearValue();
        }
        // Returning SHORT_CIRCUIT for no match will cause evaluation short circuit
        return isMatch ? EvaluationStatus.COMPLETE : EvaluationStatus.SHORT_CIRCUIT;
    }

}
