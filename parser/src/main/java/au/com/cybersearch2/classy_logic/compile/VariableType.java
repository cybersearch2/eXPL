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
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.AxiomOperand;
import au.com.cybersearch2.classy_logic.expression.AxiomParameterOperand;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.CurrencyOperand;
import au.com.cybersearch2.classy_logic.expression.DoubleOperand;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.ParameterList;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListListener;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
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
    /** Key value for literal initialization property */
    public final static String LITERAL = "Literal";
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
	    return getInstance(parserAssembler, parserAssembler.getContextName(name));
	}

    /**
     * Return new Operand instance of this type
     * @param parserAssembler ParserAssembler object
     * @param qname Qualified name of new variable
     * @return Operand object
     */
    public Operand getInstance(ParserAssembler parserAssembler, QualifiedName qname)
    {
        Operand expression = (Operand)getProperty(EXPRESSION);
		Operand operand = null;
        String axiomKey = null;
        AxiomListListener axiomListListener = null;
        if (operandType == OperandType.AXIOM || operandType == OperandType.LIST || operandType == OperandType.TERM)
        {
            getPropertyString(AXIOM_KEY);
            if (axiomKey == null)
                axiomKey = qname.getName();
            axiomListListener = axiomListListener(parserAssembler.getOperandMap());
        }
        boolean hasExpression = expression != null;
	    switch (operandType)
	    {
	    case INTEGER:
	    	operand = !hasExpression ? new IntegerOperand(qname) : new IntegerOperand(qname, expression);
	    	break;
        case DOUBLE:
        	operand = !hasExpression ? new DoubleOperand(qname) : new DoubleOperand(qname, expression);
	    	break;
        case BOOLEAN:
        	operand = !hasExpression ? new BooleanOperand(qname) : new BooleanOperand(qname, expression);
	    	break;
        case STRING:
        	operand = !hasExpression ? new StringOperand(qname) : new StringOperand(qname, expression);
	    	break;
        case DECIMAL:
        	operand = !hasExpression ? new BigDecimalOperand(qname) : new BigDecimalOperand(qname, expression);
	    	break;
        case TERM:
            // Expression is an initializer list Operand. 
            operand = new AxiomParameterOperand(new QualifiedName(qname.getName() + "_params", qname), axiomKey, expression);
            hasExpression = true;
            break;
        case AXIOM:
            operand = !hasExpression ? 
                          new AxiomOperand(qname, axiomKey, axiomListListener) : 
                          new AxiomOperand(qname, axiomKey, expression, axiomListListener);
            break;
        case LIST:
            // Expression is an initializer list 
            ParameterList<AxiomList> parameterList = new ParameterList<AxiomList>(expression, axiomListGenerator(qname, axiomKey));
            operand = new AxiomOperand(qname, axiomKey, parameterList, axiomListListener);
            hasExpression = true;
            break;
        case CURRENCY:
        	operand = !hasExpression ? 
        			  new CurrencyOperand(qname, parserAssembler.getScopeLocale()) : 
        		      new CurrencyOperand(qname, expression, parserAssembler.getScopeLocale());
	    	break;
        default:
        	operand = !hasExpression ? new Variable(qname) : new Variable(qname, expression);
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
	    Operand literal = (Operand) getProperty(LITERAL);
	    if (literal != null)
	        // Expression is a literal
	        operand.assign(literal.getValue());
	    return operand;
    }

	/**
	 * Returns ItemList object for this type in current scope. 
	 * NOTE: AxiomKey proptery must be set for Term, Axiom or Local type
     * @param parserAssembler ParserAssembler object
     * @param listname Name of new variable
	 * @return ItemList object
	 * @throws ParseException
	 */
  	public ItemList<?> getItemListInstance(ParserAssembler parserAssembler, String listName) throws ParseException
	{
        QualifiedName qualifiedListName = QualifiedName.parseName(listName, parserAssembler.getOperandMap().getQualifiedContextname());
  	    return getItemListInstance(parserAssembler, qualifiedListName);
	}
  	
  	 /**
     * Returns ItemList object for this type. 
     * NOTE: AxiomKey proptery must be set for Term, Axiom or Local type
     * @param parserAssembler ParserAssembler object
     * @param qname Qualified name of new variable
     * @return ItemList object
     * @throws ParseException
     */
    public ItemList<?> getItemListInstance(ParserAssembler parserAssembler, QualifiedName qname) throws ParseException
    {
		String axiomKey = getPropertyString(AXIOM_KEY);
		if ((operandType == OperandType.TERM) || 
			(operandType == OperandType.AXIOM) || 
			(operandType == OperandType.LOCAL))
		{
			if (axiomKey == null)
	            throw new ParseException("List " + qname.toString() + " missing axiom key");
		}
		else
		{
	        if (axiomKey != null)
	           throw new ParseException("List " + qname.toString() + " axiom key (" + axiomKey + ") is not valid for this type of list");
		}
	    switch (operandType)
	    {
        case INTEGER:
            return new ArrayItemList<Long>(Long.class, new IntegerOperand(qname));
        case DOUBLE:
            return new ArrayItemList<Double>(Double.class, new DoubleOperand(qname));
        case BOOLEAN:
            return new ArrayItemList<Boolean>(Boolean.class, new BooleanOperand(qname));
        case STRING:
            return new ArrayItemList<String>(String.class, new StringOperand(qname));
        case DECIMAL:
            return new ArrayItemList<BigDecimal>(BigDecimal.class, new BigDecimalOperand(qname));
        case CURRENCY:
            return new ArrayItemList<BigDecimal>(BigDecimal.class, getInstance(parserAssembler, qname));
        case TERM:
            AxiomTermList itemList = new AxiomTermList(qname, axiomKey);
            parserAssembler.registerAxiomTermList(itemList);
            return itemList;
        case LOCAL:
        	AxiomTermList localList = new AxiomTermList(qname, axiomKey);
        	parserAssembler.registerLocalList(localList);
        	return localList;
        case AXIOM:
            AxiomList axiomList = new AxiomList(qname, axiomKey);
            parserAssembler.registerAxiomList(axiomList);
            return axiomList;
        default:
            throw new ParseException("List " + qname.toString() + " type unknown");
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

    /**
     * Returns an object which implements CallEvaluator interface returning an AxiomList
     * given a list of axioms to marshall into an axiom list
     * @return CallEvaluator object of generic return type AxiomList
     */
    protected CallEvaluator<AxiomList> axiomListGenerator(final QualifiedName qname, final String axiomKey) 
    {
        return new CallEvaluator<AxiomList>(){

            @Override
            public String getName()
            {
                return qname.toString();
            }

            @Override
            public AxiomList evaluate(List<Term> argumentList)
            {
                AxiomList axiomList = new AxiomList(qname, axiomKey);
                int index = 0;
                for (Term term: argumentList)
                {
                    AxiomTermList axiomTermList = (AxiomTermList) term.getValue();
                    // Do not add empty axioms to list
                    if (!axiomTermList.isEmpty())
                        axiomList.assignItem(index++, axiomTermList);
                    if (!axiomTermList.isEmpty())
                        axiomList.setAxiomTermNameList(axiomTermList.getAxiomTermNameList());
                }
                return axiomList;
            }
        };
    }

    protected AxiomListListener axiomListListener(final OperandMap operandMap)
    {
        return new AxiomListListener(){

            @Override
            public void addAxiomList(QualifiedName qname, AxiomList axiomList)
            {
                if (operandMap.getItemList(qname) == null)
                    operandMap.addItemList(qname, axiomList);
            }
        };
    }
}
