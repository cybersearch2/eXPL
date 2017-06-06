/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.operator.AxiomParameterOperator;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.GenericParameter;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomParameterOperand
 * Populates an axiom from a parameter list after the list operands have been evaluated.
 * @author Andrew Bowley
 * 9 Aug 2015
 */
public class AxiomParameterOperand extends GenericParameter<AxiomTermList> implements Operand
{
    /** Qualified name of operand */
    protected QualifiedName qname;
    /** Collects parameters from an Operand tree and passes them to a supplied function object */
    protected ParameterList<AxiomTermList> parameterList;
    /** Name of axiom list to be generated */
    protected QualifiedName axiomName;
    /** Root of Operand tree for unification */
    protected Operand paramsTreeRoot;
    Template initializeTemplate;
    /** Flag set true if operand not visible in solution */
    protected boolean isPrivate;
    /** Defines operations that an Operand performs with other operands. To be set by super. */
    protected AxiomParameterOperator operator;
    /** Index of this Operand in the archetype of it's containing template */
    private int index;

    /**
     * Construct an AxiomParameterOperand object
     * @param qname Qualified name of operand
     * @param axiomKey Qualified name of backing axiom list
     * @param initializeList List of parameters to initialize this Operand
     */
    /*
    public AxiomParameterOperand(QualifiedName qname, QualifiedName axiomName, List<Template> initializeList)
    {
        super(qname.getName());
        this.qname = qname;
        this.axiomName = axiomName;
        if ((initializeList != null) && !initializeList.isEmpty())
        {
            parameterList = new ParameterList<AxiomTermList>(initializeList, axiomGenerator());
            //paramsTreeRoot = OperandParam.buildOperandTree(initializeList);
        }
        operator = new AxiomParameterOperator();
        index = -1;
    }
*/
    /**
     * Construct an AxiomParameterOperand object
     * @param qname Qualified name of operand
     * @param axiomKey Qualified name of backing axiom list
     * @param initializeTemplate Contains arameters to initialize this Operand
     */
    public AxiomParameterOperand(QualifiedName qname, QualifiedName axiomName, Template initializeTemplate)
    {
        super(qname.getName());
        this.qname = qname;
        this.axiomName = axiomName;
        this.initializeTemplate = initializeTemplate;
        if ((initializeTemplate != null))
        {
            parameterList = new ParameterList<AxiomTermList>(initializeTemplate, axiomGenerator());
        }
        operator = new AxiomParameterOperator();
        index = -1;
    }

    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        if (parameterList == null)
        {
            AxiomTermList axiomTermList = new AxiomTermList(axiomName, axiomName);
            setValue(axiomTermList);
            return EvaluationStatus.COMPLETE;
        }
        setValue(parameterList.evaluate(id));
        this.id = id;
        return EvaluationStatus.COMPLETE;
    }

    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int id)
    {
        if (paramsTreeRoot != null)
            paramsTreeRoot.backup(id);
        else if (initializeTemplate != null)
            initializeTemplate.backup(id != 0);
        return super.backup(id);
    }
    
    /**
     * Returns operand tree fur parameter unification     
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return paramsTreeRoot;
    }
    
    @Override
    public Operand getLeftOperand()
    {
        return null;
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
    
    @Override
    public Operator getOperator()
    {
        return operator;
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
     * Returns an object which implements CallEvaluator interface returning an AxiomList
     * given a list of terms to marshall into an axiom
     * @return CallEvaluator object of generic return type AxiomList
     */
    protected CallEvaluator<AxiomTermList> axiomGenerator() 
    {
        return new CallEvaluator<AxiomTermList>(){

            @Override
            public String getName()
            {
                return name;
            }

            @Override
            public AxiomTermList evaluate(List<Term> argumentList)
            {
                return marshallAxiomTerms(axiomName, axiomName, argumentList);
            }

            @Override
            public void setExecutionContext(ExecutionContext context)
            {
                // Not supported
            }};
    }

    /**
     * Returns an AxiomList object given a list of terms to marshall into an axiom
     * @param qualifiedListName Qualified name of axiom list to return
     * @param axiomKey Axiom name
     * @param argumentList List of terms
     * @return AxiomList object containing marshalled axiom
     */
    public AxiomTermList marshallAxiomTerms(QualifiedName qualifiedListName, QualifiedName axiomKey, List<Term> argumentList)
    {
        // Give axiom same name as operand
        Axiom axiom = new Axiom(axiomKey.getName());
        for (Term arg: argumentList)
        {   // Copy value to Parameter to make it immutable
            Parameter param = new Parameter(arg.getName(), arg.getValue());
            axiom.addTerm(param);
        }
        // Wrap axiom in AxiomList object to allow interaction with other AxiomLists
        AxiomTermList axiomTermList = new AxiomTermList(qualifiedListName, axiomKey);
        axiomTermList.setAxiom(axiom);
        return axiomTermList;
    }

}
