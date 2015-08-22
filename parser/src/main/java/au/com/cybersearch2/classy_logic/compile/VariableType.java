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
package au.com.cybersearch2.classy_logic.compile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.AxiomOperand;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.CurrencyOperand;
import au.com.cybersearch2.classy_logic.expression.DoubleOperand;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.list.ArrayItemList;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.parser.ParseException;

/**
 * VariableType
 * Factory class creates instances of logic programming types
 * @author Andrew Bowley
 * 10 Mar 2015
 */
public class VariableType 
{
    /** Logic programming type enumeration */
	protected OperandType operandType;
	/** Type qualifier properties eg. Currency country */
	protected Map<String, Object> properties;
	
	/** Key value for axiom name property */
	public final static String AXIOM_KEY = "AxiomKey";
    /** Key value for variable initialization property */
	public final static String EXPRESSION = "Expression";
    /** Key value for Currency country property */
	public final static String QUALIFIER_STRING = "QualifierString";
    /** Key value for Currency country evaluation operand property */
	public final static String QUALIFIER_OPERAND = "QualifierOperand";
	
	/**
	 * Construct VariableType object
	 * @param operandType Logic programming type enumeration
	 */
	public VariableType(OperandType operandType) 
	{
		this.operandType = operandType;
	}

	/**
	 * Returns logic programming type enumeration
	 * @return the operandType
	 */
	public OperandType getOperandType() 
	{
		return operandType;
	}

	/**
	 * Set property
	 * @param key  Key - AxiomKey, Expression, QualifierString or QualifierOperand
	 * @param value Object
	 */
	public void setProperty(String key, Object value)
	{
		if (properties == null)
			properties = new HashMap<String, Object>();
		properties.put(key, value);
	}

	/**
	 * Return new Operand instance of this type
	 * @param parserAssembler ParserAssembler object
	 * @param name Name of new variable
	 * @return Operand object
	 */
	public Operand getInstance(ParserAssembler parserAssembler, String name)
	{
		Operand expression = (Operand)getProperty(EXPRESSION);
		boolean hasExpression = 
				(expression != null) &&
				(expression.isEmpty() || (expression instanceof Evaluator));
		Operand operand = null;
	    switch (operandType)
	    {
	    case INTEGER:
	    	operand = !hasExpression ? new IntegerOperand(name) : new IntegerOperand(name, expression);
	    	break;
        case DOUBLE:
        	operand = !hasExpression ? new DoubleOperand(name) : new DoubleOperand(name, expression);
	    	break;
        case BOOLEAN:
        	operand = !hasExpression ? new BooleanOperand(name) : new BooleanOperand(name, expression);
	    	break;
        case STRING:
        	operand = !hasExpression ? new StringOperand(name) : new StringOperand(name, expression);
	    	break;
        case DECIMAL:
        	operand = !hasExpression ? new BigDecimalOperand(name) : new BigDecimalOperand(name, expression);
	    	break;
        case AXIOM:
            String axiomKey = getPropertyString(AXIOM_KEY);
            if (axiomKey == null)
                axiomKey = name;
            operand = !hasExpression ? new AxiomOperand(name, axiomKey) : new AxiomOperand(name, axiomKey, expression);
            parserAssembler.addAxiomOperand(operand.getName());
            break;
        case CURRENCY:
        	operand = !hasExpression ? 
        			  new CurrencyOperand(name, parserAssembler.getScopeLocale()) : 
        		      new CurrencyOperand(name, expression, parserAssembler.getScopeLocale());
	    	break;
        default:
        	operand = !hasExpression ? new Variable(name) : new Variable(name, expression);
	    }
	    if (operandType == OperandType.CURRENCY)
	    {
	    	CurrencyOperand currencyOperand = (CurrencyOperand)operand;
	    	String country = getPropertyString(QUALIFIER_STRING);
    		Operand countryOperand = (Operand) getProperty(QUALIFIER_OPERAND);
	    	if (country != null)
	    		currencyOperand.setCountry(country);
	    	else if (countryOperand != null)
	    		currencyOperand.setCountryOperand(countryOperand);
	    	else
	    		parserAssembler.registerLocaleListener(currencyOperand);
	    }
	    if ((expression != null) && !hasExpression)
	        // Expression is a literal
	        operand.assign(expression.getValue());
	    return operand;
    }

	/**
	 * Returns ItemList object for this type. 
	 * NOTE: AxiomKey proptery must be set for Term, Axiom or Local type
     * @param parserAssembler ParserAssembler object
     * @param name Name of new variable
	 * @return ItemList object
	 * @throws ParseException
	 */
  	public ItemList<?> getItemListInstance(ParserAssembler parserAssembler, String name) throws ParseException
	{
		String axiomKey = getPropertyString(AXIOM_KEY);
		if ((operandType == OperandType.TERM) || 
			(operandType == OperandType.AXIOM) || 
			(operandType == OperandType.LOCAL))
		{
			if (axiomKey == null)
	            throw new ParseException("List " + name + " missing axiom key");
		}
		else
		{
	        if (axiomKey != null)
	           throw new ParseException("List " + name + " axiom key (" + axiomKey + ") is not valid for this type of list");
		}
	    switch (operandType)
	    {
        case INTEGER:
            return new ArrayItemList<Long>(Long.class, new IntegerOperand(name));
        case DOUBLE:
            return new ArrayItemList<Double>(Double.class, new DoubleOperand(name));
        case BOOLEAN:
            return new ArrayItemList<Boolean>(Boolean.class, new BooleanOperand(name));
        case STRING:
            return new ArrayItemList<String>(String.class, new StringOperand(name));
        case DECIMAL:
            return new ArrayItemList<BigDecimal>(BigDecimal.class, new BigDecimalOperand(name));
        case CURRENCY:
            return new ArrayItemList<BigDecimal>(BigDecimal.class, getInstance(parserAssembler, name));
        case TERM:
            AxiomTermList itemList = new AxiomTermList(name, axiomKey);
            parserAssembler.registerAxiomTermList(itemList);
            return itemList;
        case LOCAL:
        	AxiomTermList localList = new AxiomTermList(name, axiomKey);
        	parserAssembler.registerLocalList(localList);
        	return localList;
        case AXIOM:
            AxiomList axiomList = new AxiomList(name, axiomKey);
            parserAssembler.registerAxiomList(axiomList);
            return axiomList;
        default:
            throw new ParseException("List " + name + " type unknown");
       }
    }
  	
    /**
     * Returns property value as String
     * @param key
     * @return String
     */
	protected String getPropertyString(String key) 
	{
		if (properties == null)
			return null;
		Object value = properties.get(key);
		return value == null ? null : value.toString();
	}

	/**
	 * Returns property for specified key
	 * @param key
	 * @return Object
	 */
	protected Object getProperty(String key) 
	{
		return properties == null ? null : properties.get(key);
	}

}
