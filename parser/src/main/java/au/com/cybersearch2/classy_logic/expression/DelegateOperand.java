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
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.DelegateOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * DelegateOperand
 * @author Andrew Bowley
 * 25 Dec 2014
 */
public abstract class DelegateOperand extends Parameter implements Operand, LocaleListener 
{
	/** Qualified name of operand */
	protected QualifiedName qname;
	/** Flag set true if operand not visible in solution */
	protected boolean isPrivate;
    /** Defines operations that an Operand performs with other operands. */
    protected DelegateOperator operator;
    /** Index of this Operand in the archetype of it's containing template */
    private int index;

	/**
     * Construct empty DelegateOperand object
     * @param qname Qualified name of variable
	 */
	protected DelegateOperand(QualifiedName qname) 
	{
		this(qname, qname.getName());
	}

    /**
     * Construct empty DelegateOperand object with specified term name
     * @param qname Qualified name of variable
     */
    protected DelegateOperand(QualifiedName qname, String termName) 
    {
        super(termName);
        this.qname = qname;
        operator = new DelegateOperator();
        index = -1;
    }
    
    /**
     * Returns qualified name
     * @return QualifiedName object
     */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

    /**
     * Returns object which defines operations that an Operand performs with other operands
     * @return Operator object
     */
    @Override
    public Operator getOperator()
    {
        return operator;
    }
    
    /**  
     * Handle notification of change of scope
     * @param scope The new scope which will assigned a particular locale
     */
    @Override
    public void onScopeChange(Scope scope)
    {
        if (operator instanceof LocaleListener)
            ((LocaleListener)operator).onScopeChange(scope);
    }
	
	/**
	 * Set Parameter value
	 * GenericParameter has a protected type-specific setValue()
	 * @param value
	 */
	@Override
	public void setValue(Object value)
	{
		if (value == null)
			value = new Null();
		else
		{
			this.value = value;
			this.empty = false;
		    operator.setDelegate(getValueClass());
		}
	}

   /**
     * Assign a value to this Operand. It may overwrite and existing value
     * This value will be overwritten on next call to evaluate(), so calling
     * assign() on an Evaluator is pointless.
     */
    @Override
    public void assign(Term term) 
    {
         setValue(term.getValue());
         id = term.getId();
    }


	/**
	 * Delegate to perform actual unification with other Term. If successful, two terms will be equivalent. 
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int id)
	 */
    @Override
	public int unify(Term otherTerm, int id)
	{
		int result = super.unify(otherTerm, id);
		operator.setDelegate(getValueClass());
		return result;
	}

    /**
     * concatenate
     * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    //public Object concatenate(Operand rightOperand)
    //{   // Axioms are special case
    //    if (operator.getDelegateType() == DelegateType.AXIOM)
    //        return AxiomUtils.concatenate(this, rightOperand);
    //    return value.toString() + rightOperand.getValue().toString();
    //}

    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param modifierId Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int modifierId)
    {
        boolean backupOccurred = false;
        // Parameter backup() invokes this object's clearValue(), 
        // which we do not want to happen, so implement own logic
        if (!((id == 0) || ((modifierId != 0) && (id != modifierId))))
        {
            super.clearValue();
            backupOccurred = true;
        }
        return backupOccurred;
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
     * setIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#setIndex(int)
     */
    @Override
    public void setArchetypeIndex(int index)
    {
        this.index = index;
    }

    /**
     * getIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getIndex()
     */
    @Override
    public int getArchetypeIndex()
    {
        return index;
    }

    /**
     * Set value to null, mark Parameter as empty and set id to 0. 
     * Do full backup for left and right operands to allow re-evaluation.
     */
    public void clearValue()
    {
        OperandVisitor visitor = new OperandVisitor(){

            @Override
            public boolean next(Operand operand, int depth)
            {
                operand.backup(0);
                return true;
            }};
        Operand leftOperand = getLeftOperand();
        if (leftOperand != null)
            visit(leftOperand, visitor, 1);
        Operand rightOperand = getRightOperand();
        if (rightOperand != null)
            visit(rightOperand, visitor, 1);
        super.clearValue();
    }
    
    /**
     * Visit a node of the Operand tree. Recursively navigates left and right operands, if any.
     * @param operand The Operand being visited
     * @param visitor Object implementing OperandVisitor interface
     * @param depth Depth in tree. The root has depth 1.
     * @return flag set true if entire tree formed by this term is navigated. 
     */
    public boolean visit(Operand operand, OperandVisitor visitor, int depth)
    {
        visitor.next(operand, depth);
        if (operand.getLeftOperand() != null)
            visit(operand.getLeftOperand(), visitor, depth + 1);
        if (operand.getRightOperand() != null)
            visit(operand.getRightOperand(), visitor, depth + 1);
        return true;
    }

    protected static QualifiedName getDelegateQualifiedName(QualifiedName qname)
    {
        return new QualifiedName(qname.getName() + qname.incrementReferenceCount(), qname);
    }

}
