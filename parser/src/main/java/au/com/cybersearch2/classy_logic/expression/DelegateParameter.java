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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * DelegateParameter
 * @author Andrew Bowley
 * 25 Dec 2014
 */
public abstract class DelegateParameter extends Parameter implements Operand 
{
	static final String DELEGATE_NAME = "delegate";
    /** Constant value for no operators permited */
	protected static OperatorEnum[] EMPTY_OPERAND_OPS = new OperatorEnum[0];
	protected static OperatorEnum[] ASSIGN_OPERAND_OP = { OperatorEnum.ASSIGN };
	
	/** Map value class to Operand class */
    protected static Map<Class<?>, Operand> delegateClassMap;
  
	/** Operand delegate to support Operand interface */
	protected Operand delegate;

	static
	{
		delegateClassMap = new HashMap<Class<?>,  Operand>();
		delegateClassMap.put(String.class, new StringOperand(DELEGATE_NAME));
		delegateClassMap.put(Integer.class, new IntegerOperand(DELEGATE_NAME));
        delegateClassMap.put(Long.class, new IntegerOperand(DELEGATE_NAME));
		delegateClassMap.put(Boolean.class, new BooleanOperand(DELEGATE_NAME));
		delegateClassMap.put(Double.class, new DoubleOperand(DELEGATE_NAME));
		delegateClassMap.put(BigDecimal.class, new BigDecimalOperand(DELEGATE_NAME));
		delegateClassMap.put(AxiomTermList.class, new AssignOnlyOperand(DELEGATE_NAME));
		delegateClassMap.put(Null.class, null);
	}
	

	/**
	 * @param name
	 * @param value
	 */
	protected DelegateParameter(String name, Object value) 
	{
		super(name, value);

	}

	/**
	 * @param name
	 */
	protected DelegateParameter(String name) 
	{
		super(name);
        delegate = new AssignOnlyOperand(name);
	}

	/**
	 * Returns permited operations for this Variable as a right hand term
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return delegate.getRightOperandOps();
	}

	/**
	 * Returns permited operations for this Variable as a left hand term
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return delegate.getLeftOperandOps();
	}

 	/**
 	 * Returns OperatorEnum values for which this Term is a valid String operand
 	 * @return OperatorEnum[]
 	 */
	 @Override
     public OperatorEnum[] getStringOperandOps()
     {
	     return delegate.getStringOperandOps();
     }

	/**
	 * Performs unary evaluation which returns a Number type  
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
		return delegate.numberEvaluation(operatorEnum2, rightTerm);
	}

	/**
	 * Performs binary evaluation which returns a Number type  
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
			Term rightTerm) 
	{
		return delegate.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
	}

	/**
	 * Performs binary evaluation which returns a Boolean type  
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
			Term rightTerm) 
	{
		return delegate.booleanEvaluation(leftTerm, operatorEnum2, rightTerm);
	}

	
	/**
	 * Creates delegate instance according to type of value
	 */
	protected void setDelegate(Class<?> clazz)
	{
		Operand newDelegate = clazz == Null.class ? null : delegateClassMap.get(clazz);
		if (newDelegate == null) 
		{
			if (delegate.getClass() != AssignOnlyOperand.class)
				//throw new ExpressionException("Unknown value class: " + getValueClass().toString());
				delegate = new AssignOnlyOperand(name);
		}
		else
			delegate = newDelegate;
	}

	public static boolean isDelegateClass(Class<?> clazz)
	{
		return delegateClassMap.keySet().contains(clazz);
	}

	/**
	 * Set Parameter value
	 * GenericParameter has a protected type-specific setValue()
	 * @param value
	 */
	protected void setValue(Object value)
	{
		if (value == null)
			value = new Null();
		else
		{
			this.value = value;
			this.empty = false;
		    setDelegate(getValueClass());
		}
	}

	/**
	 * Delegate to perform actual unification with other Term. If successful, two terms will be equivalent. 
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int id)
	 */
	protected int unify(Term otherTerm, int id)
	{
		int result = super.unify(otherTerm, id);
		setDelegate(getValueClass());
		return result;
	}
}
