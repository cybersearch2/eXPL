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
package au.com.cybersearch2.classy_logic.list;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomListVariable
 * Variable to access terms in an axiom contained in an axiom list
 * @author Andrew Bowley
 * 28 Jan 2015
 */
public class AxiomListVariable  extends Parameter implements Operand
{
    static AxiomList EMPTY_AXIOM_LIST;
    
    static
    {
        EMPTY_AXIOM_LIST = new AxiomList("","");
    }
    
	/** The backing axiom list */
    protected AxiomList axiomList;
    /** Variable to access the terms of a particular axiom in the axiom list */
    protected ItemListVariable<Object> axiomTermListVariable;
	/** Curent index value. Will be constant if indexExpression is null.  */
	protected int axiomIndex;
	/** Operand to evaluate index. Will be null if index is fixed. */
	protected Operand axiomExpression;
	/** Fixed term index - valid if termExpression is null */
	protected int termIndex;
	/** Term index evaluation operand or null if fixed index specified */
	protected Operand termExpression;
	/** AxiomList specification used for dynamic AxiomList */
	protected AxiomListSpec axiomListSpec;
	
	/** OperatorEnum const array for no operations permitted */
	protected static OperatorEnum[] EMPTY_OPERAND_OPS = new OperatorEnum[0];
	/** OperatorEnum const array for assignment only */
	protected static OperatorEnum[] ASSIGN_OPERAND_OP = { OperatorEnum.ASSIGN };
   
	/**
	 * Construct a fixed index AxiomListVariable object.
	 * Note axiom term fixed index or expression operand must be set to complete set up
	 * @param axiomList The backing axiom list
	 * @param axiomIndex The index value to select the list item
	 * @param suffix The axiom term identity
	 * @see #setTermIndex(int termIndex) 
	 * @see #setTermExpression(Operand termExpression) 
	 */
	public AxiomListVariable(AxiomList axiomList, int axiomIndex, String suffix) 
	{
		super(axiomList.getName() + "." + axiomIndex + "." + suffix);
		this.axiomList = axiomList;
		this.axiomIndex = axiomIndex;
	}

	/**
	 * Construct an evaluated index AxiomListVariable object
	 * @param axiomList The backing axiom list
	 * @param axiomExpression Operand to evaluate index
	 * @param suffix The axiom term identity
	 */
	public AxiomListVariable(AxiomList axiomList, Operand axiomExpression, String suffix) 
	{
		super(axiomList.getName() + ".index" + "." + suffix);
		this.axiomList = axiomList;
        this.axiomExpression = axiomExpression;
        axiomIndex = -1; // Set index to invalid value to avoid accidental uninitialised list access
	}

	/**
	 * Construct a AxiomListVariable object for a dynamic AxiomList ie. created by a script query
	 * @param axiomListSpec AxiomList Specification including Variable holding AxiomList
	 */
	public AxiomListVariable(AxiomListSpec axiomListSpec)
	{
        super(axiomListSpec.getListName() + 
              (axiomListSpec.getAxiomIndex() < 0 ? ".index" : axiomListSpec.getAxiomIndex()) + 
              "." + axiomListSpec.getSuffix());
        this.axiomListSpec = axiomListSpec;
        // Set an empty axiom list to avoid NPEs
        axiomList = EMPTY_AXIOM_LIST;
        if (axiomListSpec.getAxiomIndex() < 0)
            axiomExpression = axiomListSpec.getAxiomExpression();
        else 
            axiomIndex = axiomListSpec.getAxiomIndex();
	}
	
	/**
	 * Assign a value and set the delegate
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#assign(java.lang.Object)
	 */
	@Override
	public void assign(Object value2) 
	{
        if (axiomTermListVariable != null)
        	axiomTermListVariable.assign(value2);
        super.assign(value2);
	}

	/**
	 * Returns value
	 * @return Object
	 */
    @Override
    public Object getValue()
    {
		if (axiomTermListVariable == null)
			return value; // index is not valid, so cannot reference list item, which should hold Null object
		// Refresh from list item in case it has changed from last update
		return getItemValue();
    }

    /**
     * Returns value of current item
     * @return Object
     */
	protected Object getItemValue()
	{
		Object oldValue = super.getValue();
		Object itemValue = axiomTermListVariable.getValue();
		if (itemValue == null) // Case index is not currently valid
			return oldValue;
		// Replace this variable's value, if stale
		if (!itemValue.equals(oldValue))
	        super.assign(itemValue);

	    return itemValue;
	}
	
    /**
     * Set index of axiom term to specified value
     * @param termIndex In range index value
     */
	public void setTermIndex(int termIndex) 
	{
		this.termIndex = termIndex;
		// When both axiom and term indexes are fixed, 
		// then this object's axiom term list variable and value can be updated immediately
        if ((axiomIndex >= 0) && (termIndex >= 0))
        {
    		updateAxiomTermListVariable();
        	Object item = axiomTermListVariable.getValue();
        	if (item != null)
        		super.assign(item);
        }
	}

	/**
	 * Set operand to evaluate index of axiom term
	 * @param termExpression Operand object which evaluates to an Integer value
	 */
	public void setTermExpression(Operand termExpression) 
	{
		this.termExpression = termExpression;
		termIndex = -1; // Set index to invalid value to avoid accidental uninitialised list access
        if (axiomIndex >= 0)
        	// Only  this object's axiom term list variable can be updated
    		updateAxiomTermListVariable();
	}

	/**
	 * Perform unification with other Term. If successful, two terms will be equivalent.
	 * Determines Term ordering and then delegates to evaluate()  
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see Parameter#evaluate(int id)
	 * @see Parameter#backup(int id)
	 */
	@Override
	public int unifyTerm(Term otherTerm, int id)
    {
	    if ((axiomIndex < 0) || (termIndex < 0))
	    // Unification not possible as list item is not selected until evaluation
			return 0;
		int result = super.unifyTerm(otherTerm, id);
		if (result == id)
			axiomTermListVariable.assign(value);
		return result;
    }

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int)
	 */
	@Override
	public boolean backup(int id) 
	{   // Do not backup list item as unification still works regardless of whether it is empty or not.
		// Setting the list item to null, which is the only backup option, also risks NPE.
		// Backup index expression too as it always evaluates index
		boolean backupOccurred = false;
		if (super.backup(id))
			backupOccurred = true;
		if ((axiomExpression != null) && axiomExpression.backup(id)) 
			backupOccurred = true;
		if ((axiomTermListVariable != null) && axiomTermListVariable.backup(id))
			backupOccurred = true;
		return backupOccurred;
	}


	/**
	 * Evaluate index if expression provided
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id)
	{
	    if ((axiomListSpec != null) && axiomListSpec.update())
	    {   // Dynamic AxiomList should now exist, so initialization of
	        // of this object can be completed
	        axiomList = axiomListSpec.getAxiomList();
            if (axiomListSpec.getTermIndex() < 0) 
                setTermExpression(axiomListSpec.getTermExpression());
            else
                setTermIndex(axiomListSpec.getTermIndex());
	    }
		if (axiomExpression != null)
		{   // Evaluate index. The resulting value must be a sub class of Number to be usable as an index.
			axiomExpression.evaluate(id);
			if (axiomExpression.isEmpty())
				throw new ExpressionException("Axiom index for list \"" + axiomList.getName() + "\" is empty" );
			if (!(axiomExpression.getValue() instanceof Number))
				throw new ExpressionException("\"" + axiomList.getName() + "[" + axiomExpression.getValue().toString() + "]\" is not a valid value" );
			
			int index = ((Number)(axiomExpression.getValue())).intValue();
			if (!axiomList.hasItem(index))
				throw new ExpressionException("\"" + axiomList.getName() +"\" index " + index + " out of bounds");
			if (isEmpty() || (index != axiomIndex))
			{
				if (index != axiomIndex)
				{
					axiomIndex = index;
					updateAxiomTermListVariable();
				}
				if (termExpression != null)
					axiomTermListVariable.evaluate(id);
				super.assign(axiomTermListVariable.getValue());
			}
			this.id = id;
		}
		else if (termExpression != null)
		{
		    if (axiomTermListVariable.isEmpty())
                updateAxiomTermListVariable();
			axiomTermListVariable.evaluate(id);
		    super.assign(axiomTermListVariable.getValue());
			this.id = id;
		}
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * Update this object's axiom term list variable
	 * @param suffix The axiom term identity
	 */
	protected void updateAxiomTermListVariable()
	{
		// Get currently selected item in owning axiom list
	    
		AxiomTermList axiomTermList = null;
		if (!axiomList.isEmpty())
		    axiomTermList = axiomList.getItem(axiomIndex);
		else
		    axiomTermList = new AxiomTermList(axiomList.getName() + ".item", axiomList.getKey());
		String suffix = null;
		if (termExpression != null)
		{
			if (termExpression.getName().isEmpty())
				suffix = termExpression.toString();
			else
				suffix = termExpression.getName();
		}
		else
			suffix = Integer.toString(termIndex);
		// Create new instance of a variable to access the axiom
		ItemListVariable<Object> variable = null;
		variable = termExpression != null ? 
				                   axiomTermList.newVariableInstance(termExpression, suffix) :
				            	   axiomTermList.newVariableInstance(termIndex, suffix);
		axiomTermListVariable = variable;				                   
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return axiomTermListVariable == null ? 
				    ASSIGN_OPERAND_OP : 
					axiomTermListVariable.getRightOperandOps();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return axiomTermListVariable == null ? 
				    ASSIGN_OPERAND_OP : 
				    axiomTermListVariable.getLeftOperandOps();
	}

 	/**
 	 * Returns OperatorEnum values for which this Term is a valid String operand
 	 * @return OperatorEnum[]
 	 */
	 @Override
     public OperatorEnum[] getStringOperandOps()
     {
	     return Operand.EMPTY_OPERAND_OPS;
     }

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
		return axiomTermListVariable == null ? 
				    new Integer(0) : 
				    axiomTermListVariable.numberEvaluation(operatorEnum2, rightTerm);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
			Term rightTerm) 
	{
		return axiomTermListVariable == null ? 
				    new Integer(0) : 
		            axiomTermListVariable.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
			Term rightTerm) 
	{
		return axiomTermListVariable == null ?
				    Boolean.FALSE :
				    axiomTermListVariable.booleanEvaluation(leftTerm, operatorEnum2, rightTerm) ;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperand()
	 */
	@Override
	public Operand getLeftOperand() 
	{
		return axiomTermListVariable;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
	 */
	@Override
	public Operand getRightOperand() 
	{
		return null;
	}

}
