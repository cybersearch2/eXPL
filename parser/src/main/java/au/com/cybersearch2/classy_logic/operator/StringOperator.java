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
package au.com.cybersearch2.classy_logic.operator;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

/**
 * StringOperator
 * @see DelegateType.STRING
 * @see StringOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class StringOperator implements Operator, LocaleListener
{
    /** Localization and specialization */
    protected DefaultTrait trait;

    /**
     * Construct StringOperator object
     */
    public StringOperator()
    {
        super();
        trait = new DefaultTrait(OperandType.STRING);
    }

    /**
     * getTrait
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return trait;
    }

    @Override
    public void setTrait(Trait trait)
    {
        if (trait.getOperandType() != OperandType.STRING)
            throw new ExpressionException(trait.getOperandType().toString() + " is not a compatible operand type");
        this.trait = (DefaultTrait) trait;
    }
    
    /**
     * onScopeChange
     * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
     */
    @Override
    public void onScopeChange(Scope scope)
    {
        trait.setLocale(scope.getLocale());
    }

    /**
     * getRightOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return  new OperatorEnum[]
        { 
            OperatorEnum.ASSIGN,
            OperatorEnum.EQ, // "=="
            OperatorEnum.NE
        };
    }

    /**
     * getLeftOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return  new OperatorEnum[]
        { 
                OperatorEnum.ASSIGN,
                OperatorEnum.EQ, // "=="
                OperatorEnum.NE // "!="
        };
    }

    /**
     * getStringOperandOps
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#getConcatenateOps()
     */
     @Override
     public OperatorEnum[] getConcatenateOps()
     {
        return  new OperatorEnum[]
        { 
            OperatorEnum.PLUS,
            OperatorEnum.PLUSASSIGN
        };
     }

    /**
     * Evaluate a unary expression using this Term
     * @param operatorEnum OperatorEnum for one of +, - or ~ 
     * @return generic Parameter which implements Operand. The genericy type will be a sub class of Number.
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum, Term rightTerm) 
    {
        return new Integer(0);
    }

    /**
     * numberEvaluation - binary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluationTerm, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum, Term rightTerm) 
    {
        return new Integer(0);
    }

    /**
     * booleanEvaluation
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum, Term rightTerm) 
    {
        boolean calc = false;
        switch (operatorEnum)
        {
        case EQ:  calc = leftTerm.getValue().equals(rightTerm.getValue()); break; // "=="
        case NE:  calc = !leftTerm.getValue().equals(rightTerm.getValue()); break; // "!="
        default:
        }
        return calc;
    }

}
