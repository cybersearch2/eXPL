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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DoubleTrait;
import au.com.cybersearch2.classy_logic.trait.IntegerTrait;

/**
 * DoubleOperand
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class DoubleOperand extends ExpressionOperand<Double> implements LocaleListener 
{
    static Trait DOUBLE_TRAIT;
    
    static
    {
        DOUBLE_TRAIT = new DoubleTrait();
    }
    
    /** Localization and specialization */
    protected Trait trait;

	/**
	 * Construct named DoubleOperand object
     * @param qname Qualified name
	 */
	public DoubleOperand(QualifiedName qname) 
	{
		super(qname);
		this.trait = DOUBLE_TRAIT;
	}

	/**
	 * Construct named, non empty DoubleOperand object
     * @param qname Qualified name
	 * @param value Double object
	 */
	public DoubleOperand(QualifiedName qname, Double value) 
	{
		super(qname, value);
        this.trait = DOUBLE_TRAIT;
	}

	/**
	 * Construct named DoubleOperand object which delegates to an expression to set value
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public DoubleOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
        this.trait = DOUBLE_TRAIT;
	}

	/**
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
			OperatorEnum.ASSIGN,
			OperatorEnum.LT, // "<"
			OperatorEnum.GT ,// ">"
			OperatorEnum.EQ, // "=="
			OperatorEnum.LE, // "<="
			OperatorEnum.GE, // ">="
			OperatorEnum.NE, // "!="
			OperatorEnum.PLUS,
			OperatorEnum.MINUS,
			OperatorEnum.STAR,
			OperatorEnum.SLASH,
			OperatorEnum.PLUSASSIGN,
			OperatorEnum.MINUSASSIGN,
			OperatorEnum.STARASSIGN,
			OperatorEnum.SLASHASSIGN
		};
	}

	/**
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
				OperatorEnum.ASSIGN,
				OperatorEnum.LT, // "<"
				OperatorEnum.GT ,// ">"
				OperatorEnum.EQ, // "=="
				OperatorEnum.LE, // "<="
				OperatorEnum.GE, // ">="
				OperatorEnum.NE, // "!="
				OperatorEnum.PLUS,
				OperatorEnum.MINUS,
				OperatorEnum.STAR,
				OperatorEnum.SLASH,
				OperatorEnum.PLUSASSIGN,
				OperatorEnum.MINUSASSIGN,
				OperatorEnum.STARASSIGN,
				OperatorEnum.SLASHASSIGN
		};
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
        double right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
		double calc = 0;
		switch (operatorEnum2)
		{
		case PLUS:  calc = +right; break;
		case MINUS: calc = -right; break;  
	    default:
		}
	    return new Double(calc);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
        double right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        double left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
		double calc = 0;
		switch (operatorEnum2)
		{
		case PLUSASSIGN: // "+="
		case PLUS: 	calc = left + right; break;
		case MINUSASSIGN: // "-="
		case MINUS:  calc = left - right; break;
		case STARASSIGN: // "*="
		case STAR:      calc = left * right; break;
		case SLASHASSIGN: // "/="
		case SLASH:     calc = left / right; break;
	    default:
		}
	    return new Double(calc);
	}

	/**
	 * Evaluate relational operation using this Term as the left term
	 * @param leftTerm Term on left
	 * @param operatorEnum2 Operator
	 * @param rightTerm Term on right
	 * @return Boolean result
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		double right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
		double left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
		boolean calc = false;
		switch (operatorEnum2)
		{
		case LT:  calc = left < right; break;
		case GT:  calc = left > right; break;
		case EQ:  calc = left == right; break; // "=="
		case LE:  calc = left <= right; break; // "<="
		case GE:  calc = left >= right; break; // ">="
		case NE:  calc = left != right; break; // "!="
	    default:
		}
		return calc;
	}

    /**
     * Evaluate value if expression exists
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if ((status == EvaluationStatus.COMPLETE) && !isEmpty())
            // Perform conversion to Double, if required
            setValue(convertObject(value, getValueClass()));
        return status;
    }

	/**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
	 */
	@Override
	public void assign(Term term) 
	{
		setValue(convertObject(term.getValue(), term.getValueClass()));
	}

    @Override
    public void setTrait(Trait trait)
    {
        trait.setLocale(this.trait.getLocale());
        this.trait = trait;
    }

    @Override
    public Trait getTrait()
    {
        if (trait == DOUBLE_TRAIT)
            trait = new IntegerTrait();
        return trait;
    }

    @Override
    public void onScopeChange(Scope scope)
    {
        if (trait == DOUBLE_TRAIT)
            trait = new DoubleTrait();
        trait.setLocale(scope.getLocale());
    }

    protected double convertObject(Object object, Class<?> clazz)
    {
        if (clazz == Double.class)
            return (Double)object;
        else if (clazz == String.class)
            return ((DoubleTrait)trait).parseValue(object.toString());
        else if (Number.class.isAssignableFrom(clazz))
            return ((Number)object).doubleValue();
        else return Double.NaN;
    }
}
