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
import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomListVariable
 * Variable to access terms in an axiom contained in an axiom list
 * @author Andrew Bowley
 * 28 Jan 2015
 */
public class AxiomListVariable  extends Parameter implements Operand, Concaten<String>
{
    static AxiomList EMPTY_AXIOM_LIST;
    
    static
    {
        EMPTY_AXIOM_LIST = new AxiomList(QualifiedName.ANONYMOUS, new QualifiedName(Term.ANONYMOUS));
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
    /** Flag set true if operand not visible in solution */
    protected boolean isPrivate;
	
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
	 * @see #setTermIndex(int termIndex, int) 
	 * @see #setTermExpression(Operand termExpression, int) 
	 */
	public AxiomListVariable(AxiomList axiomList, int axiomIndex, String suffix) 
	{
		super(axiomList.getName() + "_" + axiomIndex + "_" + suffix);
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
		super(axiomList.getName() + "_index" + "_" + suffix);
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
              (axiomListSpec.getAxiomIndex() < 0 ? "_index" : axiomListSpec.getAxiomIndex()) + 
              "_" + axiomListSpec.getSuffix());
        this.axiomListSpec = axiomListSpec;
        // Set an empty axiom list to avoid NPEs
        axiomList = EMPTY_AXIOM_LIST;
        if (axiomListSpec.getAxiomIndex() < 0)
            axiomExpression = axiomListSpec.getAxiomExpression();
        else 
            axiomIndex = axiomListSpec.getAxiomIndex();
	}

	/**
	 * getQualifiedName
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getQualifiedName()
	 */
    @Override
    public QualifiedName getQualifiedName()
    {
        return axiomList.getQualifiedName();
    }
	
	/**
	 * Assign a value and set the delegate
	 */
    @Override
	public void assign(Term term) 
	{
        Object newValue = term.getValue();
	    if (newValue instanceof AxiomTermList)
	    {
	        AxiomTermList axiomTermList = (AxiomTermList)newValue;
	        if (axiomList == null)
	        {
	            axiomList = new AxiomList(axiomTermList.getQualifiedName(), axiomTermList.getKey());
	            axiomList.setAxiomTermNameList(AxiomUtils.getTermNames(axiomTermList.getAxiom()));
	        }
	        if (axiomIndex == -1)
	            axiomIndex = 0;
	        axiomList.assignItem(axiomIndex, newValue);
	        if ((termExpression != null) || (termIndex != -1))
	        {
    	        // Create new instance of a variable to access the axiom
    	        ItemListVariable<Object> variable = null;
    	        int pos = name.lastIndexOf('_');
    	        String suffix = pos == -1 ? "" : name.substring(pos+ 1);
    	        variable = termExpression != null ? 
    	                AxiomUtils.newVariableInstance(axiomTermList, termExpression, suffix, id) :
    	                AxiomUtils.newVariableInstance(axiomTermList, termIndex, suffix, id);
    	        axiomTermListVariable = variable;  
	        }
	    }
	    else if (axiomTermListVariable != null)
        	axiomTermListVariable.setValue(newValue);
        super.setValue(newValue);
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
	        super.setValue(itemValue);

	    return itemValue;
	}
	
    /**
     * Set index of axiom term to specified value
     * @param termIndex In range index value
     * @param modifierId Identity of caller, which must be provided for backup()
     */
	public void setTermIndex(int termIndex, int modifierId) 
	{
		this.termIndex = termIndex;
		// When both axiom and term indexes are fixed, 
		// then this object's axiom term list variable and value can be updated immediately
        if ((axiomIndex >= 0) && (termIndex >= 0))
        {
    		updateAxiomTermListVariable(modifierId);
        	Object item = axiomTermListVariable.getValue();
        	if (item != null)
        	{
        		super.setValue(item);
                this.id = modifierId;
        	}
        }
	}

	/**
	 * Set operand to evaluate index of axiom term
	 * @param termExpression Operand object which evaluates to an Integer value
     * @param modifierId Identity of caller, which must be provided for backup()
	 */
	public void setTermExpression(Operand termExpression, int modifierId) 
	{
		this.termExpression = termExpression;
		termIndex = -1; // Set index to invalid value to avoid accidental uninitialised list access
        if ((axiomIndex >= 0) && (termExpression != null))
        	// Only  this object's axiom term list variable can be updated
    		updateAxiomTermListVariable(modifierId);
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
			axiomTermListVariable.assign(this);
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
	 * @param modifierId Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int modifierId)
	{
	    if ((axiomListSpec != null) && axiomListSpec.update(modifierId))
	    {   // Dynamic AxiomList should now exist, so initialization of
	        // of this object can be completed
	        axiomList = axiomListSpec.getAxiomList();
	        // Refresh index parameters which may have changed
	        axiomIndex = axiomListSpec.getAxiomIndex();
	        axiomExpression = axiomListSpec.getAxiomExpression();
	        termIndex = axiomListSpec.getTermIndex();
	        termExpression = axiomListSpec.getTermExpression();
            if (axiomListSpec.getTermIndex() < 0) 
                setTermExpression( axiomListSpec.getTermExpression(), modifierId);
            else
                setTermIndex(axiomListSpec.getTermIndex(), modifierId);
	    }
	    boolean axiomOnly = (termExpression == null) && (termIndex < 0);
		if (axiomExpression != null)
		{   // Evaluate index. The resulting value must be a sub class of Number to be usable as an index.
			axiomExpression.evaluate(modifierId);
			if (axiomExpression.isEmpty())
				throw new ExpressionException("Axiom index for list \"" + axiomList.getName() + "\" is empty" );
			int index = -1;
			if (axiomExpression.getValue() instanceof Number)
	            index = ((Number)(axiomExpression.getValue())).intValue();
			if (index == -1)
				throw new ExpressionException("\"" + axiomList.getName() + "[" + axiomExpression.getValue().toString() + "]\" is not a valid value" );
			
			if (!axiomList.hasItem(index))
				throw new ExpressionException("\"" + axiomList.getName() +"\" index " + index + " out of bounds");
			if (isEmpty() || (index != axiomIndex))
			{
				if (index != axiomIndex)
				{
					axiomIndex = index;
					updateAxiomTermListVariable(modifierId);
				}
				if (!axiomOnly)
				{
				    if (termExpression != null)
					    axiomTermListVariable.evaluate(modifierId);
				    super.assign(axiomTermListVariable);
				}
			}
			this.id = modifierId;
		}
		else if (termExpression != null)
		{
		    if (axiomTermListVariable.isEmpty())
                updateAxiomTermListVariable(modifierId);
			axiomTermListVariable.evaluate(modifierId);
		    super.assign(axiomTermListVariable);
			this.id = modifierId;
		}
        if (axiomOnly)
        {
            if (axiomList.hasItem(axiomIndex))
                super.setValue(axiomList.getItem(axiomIndex));
            else
                super.setValue(new Null());
        }
		return EvaluationStatus.COMPLETE;
	}

    /**
     * Set this operand private - not visible in solution
     * @param isPrivate Flag set true if operand not visible in solution
     */
    @Override
    public void setPrivate(boolean isPrivate)
    {
        this.isPrivate = isPrivate;
    }
    
    /**
     * Returns flag set true if this operand is private
     * @return
     */
    @Override
    public boolean isPrivate()
    {
        return isPrivate;
    }
    
	/**
	 * Update this object's axiom term list variable
     * @param modifierId Identity of caller, which must be provided for backup()
	 */
	protected void updateAxiomTermListVariable(int modifierId)
	{
		// Get currently selected item in owning axiom list
		AxiomTermList axiomTermList = null;
		if (!axiomList.isEmpty())
		    axiomTermList = axiomList.getItem(axiomIndex);
		else
		    axiomTermList = new AxiomTermList(getItemListName(axiomList), axiomList.getKey());
		String suffix = null;
		if (termExpression != null)
		{
			if (termExpression.getName().isEmpty())
				suffix = termExpression.toString();
			else
				suffix = termExpression.getName();
		}
		else if (termIndex >= 0)
			suffix = Integer.toString(termIndex);
		else
		    return;
		// Create new instance of a variable to access the axiom
		ItemListVariable<Object> variable = null;
		variable = termExpression != null ? 
				                   axiomTermList.newVariableInstance(termExpression, suffix, modifierId) :
				            	   axiomTermList.newVariableInstance(termIndex, suffix, modifierId);
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
	     if (!empty)
	     {
	         return  new OperatorEnum[]
	                 { 
	                     OperatorEnum.PLUS,
	                     OperatorEnum.PLUSASSIGN
	                 };

	     }
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

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
	 */
    @Override
    public String concatenate(Operand rightOperand)
    {
        return getItemValue().toString() + rightOperand.getValue().toString();
    }

    /**
     * Returns default qualified name for axiom term list
     * @param axiomList2 The containing axiom list
     * @return QualifiedName object
     */
    protected QualifiedName getItemListName(AxiomList axiomList2)
    {
        return new QualifiedName(axiomList2.getName() + "_item", axiomList2.getQualifiedName());
    }


}
