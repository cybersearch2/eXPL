/**
 * 
 */
package au.com.cybersearch2.classy_logic.compile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * OperandMap
 * Operand and ItemList container. 
 * Items are referenced by qualified name. 
 * Provides a context qualified name for creation of intem qualified names
 * @author Andrew Bowley
 *
 * @since 19/10/2010
 */
public class OperandMap
{
	/** Tree map of operands for efficient lookup */
    protected Map<QualifiedName, Operand> operandMap;
    /** Names only for quick existence test */
    protected Set<String> nameSet;
    
	/**
	 * Construct OperandMap object.
	 */
	public OperandMap()
	{
		operandMap = new TreeMap<QualifiedName, Operand>();
		nameSet = new HashSet<String>();
	}

    /**
	 * Returns all operand values in a map container
	 * @return Map with key of type Operand and value of type Object
	 */
	public  Map<Operand, Parameter> getOperandValues()
	{
		Map<Operand, Parameter> operandValueMap = new HashMap<Operand, Parameter>();
		for (Operand operand: operandMap.values())
		{
			if (!operand.isEmpty())
			{
			    Parameter parameter = new Parameter(operand.getName(), operand.getValue());
			    parameter.setId(operand.getId());
				operandValueMap.put(operand, parameter);
			}
		}
		return operandValueMap;
	}

	/**
	 * Set operand values
	 * @param operandValueMap Map container
	 */
	public void setOperandValues(Map<Operand, Parameter> operandValueMap)
	{
		for (Entry<Operand, Parameter> entry: operandValueMap.entrySet())
			if (entry.getValue().getValueClass() == Null.class) // Null special case
				entry.getKey().clearValue();
			else
			    entry.getKey().assign(entry.getValue());
		// Clear operands not included in operandValueMap
		for (Entry<QualifiedName, Operand> entry: operandMap.entrySet())
			if (!operandValueMap.containsKey(entry.getValue()))
				entry.getValue().clearValue();
	}

	/**
	 * Throws exception for 'Duplicate Operand name' if operand name is a duplicat
	 * @param name Name of operand in text format
	 * @see #hasOperand(java.lang.String)
     * @throws ExpressionException
	 */
	public void duplicateOperandCheck(String name, QualifiedName qualifiedContextname)
	{
	    if (hasOperand(name, qualifiedContextname))
	        throw new ExpressionException("Duplicate Operand name \"" + name + "\" encountered");
	}

	/**
     * Throws exception for 'Duplicate Operand name' if operand name is a duplicate
     * @param qname Qulaified name of operand
     * @throws ExpressionException
	 */
    public void duplicateOperandCheck(QualifiedName qname)
    {
        if (operandMap.containsKey(qname))
            throw new ExpressionException("Duplicate Operand name \"" + qname.toString() + "\" encountered");
    }

    /**
     * Add new Variable operand of specified name, unless it already exists
     * @param name Name of new Opernand
     * @param expression Optional expression
     * @return New or existing Operand object
     */
    public Operand addOperand(String name, Operand expression, QualifiedName qualifiedContextname)
    {
        Operand operand = addOperand(QualifiedName.parseName(name, qualifiedContextname), expression);
        return operand;
    }
    
    /**
     * Add new Variable operand of specified name, unless it already exists
     * @param qname Qualified name of new Operand
     * @param expression Optional expression
     * @return New or existing Operand object
     */
	public Operand addOperand(QualifiedName qname, Operand expression)
    {
		Operand param = operandMap.get(qname);
		if ((param == null) && !qname.getTemplate().isEmpty())
		{
		    QualifiedName sameScope = new QualifiedName(qname.getScope(), qname.getName());
		    param = operandMap.get(sameScope);
		    if (param != null)
		        qname = sameScope;
		}
        if ((param == null) && !qname.getScope().isEmpty())
        {
            QualifiedName globalScope = new QualifiedName(qname.getName());
            param = operandMap.get(globalScope);
            if (param != null)
                qname = globalScope;
        }
		if (param == null)
		{
			param = expression == null ? new Variable(qname) : new Variable(qname, expression);
			operandMap.put(qname, param);
	        nameSet.add(qname.getName());
		}
		else if (expression != null)
			param = new Evaluator(qname, param, "=", expression);
		return param;
    }

	/**
	 * Add supplied operand to map. Assumes operand is not annonymous.
	 * Note this will replace an existing operand with the same name
	 * @param operand Operand object
	 * @throws ExpressionException
	 */
	public void addOperand(Operand operand)
    {
		QualifiedName qname = operand.getQualifiedName();
        if (qname.getName().isEmpty())
            throw new ExpressionException("addOperand() passed annonymous object");
        duplicateOperandCheck(qname);
		operandMap.put(qname, operand);
        nameSet.add(qname.getName());
    }

	/**
	 * Returns flag to indicate if this map contains operand with specified name
	 * @param name
	 * @return boolean
	 */
	public boolean hasOperand(String name, QualifiedName qualifiedContextname)
	{
        if (!nameSet.contains(name) && (name.indexOf('.') == -1)) 
            return false;
        QualifiedName qname = QualifiedName.parseName(name, qualifiedContextname);
        if (operandMap.containsKey(qname))
            return true;
        if (!qname.getTemplate().isEmpty())
        {
            qname.clearTemplate();
            if (operandMap.containsKey(qname))
                return true;
        }
        if (!qname.getScope().isEmpty())
        {
            qname.clearScope();
            if (operandMap.containsKey(qname))
                return true;
        }
 		return false;
	}

    /**
     * Returns flag to indicate if this map contains operand with specified name
     * @param name
     * @return boolean
     */
    public boolean hasOperand(QualifiedName qname)
    {
        return operandMap.containsKey(qname);
    }

	/**
	 * Returns operand of specified name or null if not found
     * @param qname Qualified name
	 * @return Operand
	 */
	public Operand getOperand(QualifiedName qname)
	{
		return operandMap.get(qname);
	}
	
	/**
	 * Returns operand referenced by qualified name
	 * @param qname Qualified name
	 * @return Operand object or null if not exists
	 */
	public Operand get(QualifiedName qname)
	{
		return operandMap.get(qname);
	}

	/**
	 * Returns list of operand names
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString()
    {
    	StringBuilder builder = new StringBuilder();
    	boolean firstTime = true;
		for (QualifiedName name: operandMap.keySet())
		{
			if (firstTime)
				firstTime = false;
			else
				builder.append(", ");
			builder.append(name);
		}
		return builder.toString();
    }


}
