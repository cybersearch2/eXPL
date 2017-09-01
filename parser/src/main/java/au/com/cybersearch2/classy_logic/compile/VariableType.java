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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.AppenderVariable;
import au.com.cybersearch2.classy_logic.expression.AxiomOperand;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.CountryOperand;
import au.com.cybersearch2.classy_logic.expression.DoubleOperand;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.MacroOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.TermOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListListener;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.RightOperand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.Appender;
import au.com.cybersearch2.classy_logic.list.ArrayIndex;
import au.com.cybersearch2.classy_logic.list.ArrayItemList;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.DynamicList;
import au.com.cybersearch2.classy_logic.operator.CurrencyOperator;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * VariableType
 * Factory class creates variables of specified types
 * @author Andrew Bowley
 * 10 Mar 2015
 */
public class VariableType 
{
    /** Key value for axiom key property, which allows an axiom list to 
     *  contain axioms with a different name to that of the list */
    public final static String AXIOM_KEY = "AxiomKey";
    /** Key value for operand expression property. The expression sets the intial value of the operand. */
    public final static String EXPRESSION = "Expression";
    /** Key value for parameter initialization property */
    public final static String PARAMS = "Params";
    /** Key value for Currency country property */
    public final static String QUALIFIER_STRING = "QualifierString";
    /** Key value for Currency country evaluation operand property */
    public final static String QUALIFIER_OPERAND = "QualifierOperand";
    /** Key value for literal operand property - evaluation not required */
    public final static String LITERAL = "Literal";
    /** Key value for list initialization template */
    public final static String TEMPLATE = "Template";
    /** Key value for exporting a list */
    public final static String EXPORT = "Export";
    
    /** Type of variable */
	protected OperandType operandType;
	/** Type qualifier properties eg. Currency country */
	protected Map<String, Object> properties;
	/** Parser assembler of current scope */
	protected ParserAssembler parserAssembler;
	
	/**
	 * Construct VariableType object
	 * @param operandType Logic programming type enumeration
	 */
	public VariableType(OperandType operandType, ParserAssembler parserAssembler) 
	{
		this.operandType = operandType;
		this.parserAssembler = parserAssembler;
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
	 * Set type to UNKNOWN to force Variable instance creation
	 * Returns self
	 */
	public VariableType setUnknownType()
	{
	    operandType = OperandType.UNKNOWN;
	    return this;
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
     * @param name Name of new variable
	 * @return Operand object
	 * @throws ParseException 
	 */
	public Operand getInstance(String name) throws ParseException
	{
	    return getInstance(parserAssembler.getContextName(name));
	}

    /**
     * Return new Operand instance of this type
     * @param qname Qualified name of new variable
     * @return Operand object
     * @throws ParseException 
     */
    public Operand getInstance(QualifiedName qname) throws ParseException
    {
        return getInstance(qname, (Operand)getProperty(EXPRESSION));
    }
    
    /**
     * Return new Operand instance of this type
     * @param qname Qualified name of new variable
     * @return Operand object
     * @throws ParseException 
     */
    @SuppressWarnings("unchecked")
    public Operand getInstance(QualifiedName qname, Operand expression) throws ParseException
    {
        boolean hasExpression = expression != null;
		Operand operand = null;
        QualifiedName axiomKey = null;
        List<Template> initializeList = null;
        Template initializeTemplate = null;
        AxiomListListener axiomListListener = null;
        if (operandType == OperandType.AXIOM || operandType == OperandType.LIST || operandType == OperandType.TERM)
        {
            axiomKey = (QualifiedName)getProperty(AXIOM_KEY);
            if (axiomKey == null)
                axiomKey = qname;
            axiomListListener = parserAssembler.getListAssembler().axiomListListenerInstance();
        }
        if (operandType == OperandType.LIST)
        {
            initializeList = (List<Template>)getProperty(PARAMS);
            initializeTemplate = (Template)getProperty(TEMPLATE);
        }
        else if (operandType == OperandType.TERM)
            initializeTemplate = (Template)getProperty(PARAMS);
	    
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
            operand = new TermOperand(new AxiomTermListEvaluator(qname, axiomKey, initializeTemplate));
            break;
        case AXIOM:
            operand = new AxiomOperand(qname, axiomKey, axiomListListener);
            break;
        case LIST:
        {
            AxiomListEvaluator axiomListEvaluator = new AxiomListEvaluator(qname, axiomKey, initializeList, initializeTemplate);
            if (getProperty(EXPORT) != null)
                axiomListEvaluator.setPublic(true);
            operand = new AxiomOperand(axiomListEvaluator, axiomListListener);
            break;
        }
        case CURRENCY:
        {
            BigDecimalOperand currencyOperand = !hasExpression ? new BigDecimalOperand(qname) : new BigDecimalOperand(qname, expression);
            currencyOperand.setOperator(getCurrencyOperator(currencyOperand));
            operand = currencyOperand;
	    	break;
        }
        case APPENDER:
            throw new IllegalArgumentException("Appender is not created in getInstance()");
            //return createAppender(qname, expression);
        case UNKNOWN: 	
        default:
        	operand = !hasExpression ? new Variable(qname) : new Variable(qname, expression);
	    }
	    if (operand instanceof LocaleListener)
	    	parserAssembler.registerLocaleListener((LocaleListener) operand);
	    Operand literalOperand = (Operand)getProperty(LITERAL);
	    if (literalOperand != null)
	        operand.assign(new Parameter(Term.ANONYMOUS, literalOperand.getValue()));
	    return operand;
    }

    /**
	 * Returns ItemList object for this type in current scope. 
	 * NOTE: AxiomKey proptery must be set for Term, Axiom or Local type
     * @param listName Name of new variable
	 * @return ItemList object
	 * @throws ParseException
	 */
  	public ItemList<?> getItemListInstance(String listName) throws ParseException
	{
        QualifiedName qualifiedListName = QualifiedName.parseName(listName, parserAssembler.getQualifiedContextname());
  	    return getItemListInstance(qualifiedListName);
	}
  	
    /**
     * Returns ItemList object for this type in current scope. 
     * NOTE: AxiomKey proptery must be set for Term, Axiom or Local type
     * @param listName Name of new variable
     * @return ItemList object
     * @throws ParseException
     */
    public ItemList<?> getContextListInstance(String listName, QualifiedName axiomKey) throws ParseException
    {
        QualifiedName qname = QualifiedName.parseName(listName, parserAssembler.getQualifiedContextname());
        AxiomTermList localList = new AxiomTermList(qname, axiomKey);
        parserAssembler.registerLocalList(localList);
        return localList;
    }
    
    /**
     * Returns Dynamic ItemList object for this type in current scope. 
     * NOTE: AxiomKey property must be set for Term, Axiom or Local type
     * @param listName Name of new variable
     * @param template Initialization template
     * @return ItemList object
     * @throws ParseException
     */
    public ItemList<?> getDynamicListInstance(String listName, Template template) throws ParseException
    {
        QualifiedName qualifiedListName = QualifiedName.parseName(listName, parserAssembler.getQualifiedContextname());
        return getDynamicListInstance(qualifiedListName, template);
    }

    /**
     * Macro to convert a literal value and return the result in a parameter
     * @param literal Literal value contained in an anonymous parameter
     * @param scope The scope context
     * @return Parameter object
     * @throws ParseException if type not supported
     */
    public Parameter getParameter(final Parameter literal, Scope scope) throws ParseException
    {
        Operand operand = null;
        final QualifiedName qname = QualifiedName.ANONYMOUS;
        Operand expression = new MacroOperand(literal.getValue());
        switch (operandType)
        {
        case INTEGER:
            operand = new IntegerOperand(qname, expression);
            break;
        case DOUBLE:
            operand = new DoubleOperand(qname, expression);
            break;
        case BOOLEAN:
            operand = new BooleanOperand(qname, expression);
            break;
        case STRING:
            operand = new StringOperand(qname, expression);
            break;
        case DECIMAL:
            operand = new BigDecimalOperand(qname, expression);
            break;
        case CURRENCY:
        {
            BigDecimalOperand currencyOperand =new BigDecimalOperand(qname, expression);
            currencyOperand.setOperator(getCurrencyOperator(currencyOperand));
            operand = currencyOperand;
            break;
        }
        default:
            throw new ParseException(operandType.toString() + " is not a literal type");
        }
        if ((operandType != OperandType.CURRENCY) || (getPropertyString(QUALIFIER_STRING) == null))
            ((LocaleListener)operand).onScopeChange(scope);
        operand.evaluate(0);
        return operand;
    }


  	 /**
     * Returns ItemList object for this type. 
     * NOTE: AxiomKey property must be set for Term, Axiom or Local type
     * @param qname Qualified name of new variable
     * @return ItemList object
     * @throws ParseException
     */
    protected ItemList<?> getItemListInstance(QualifiedName qname) throws ParseException
    {
		QualifiedName axiomKey = null;
		if ((operandType == OperandType.TERM) || 
			(operandType == OperandType.AXIOM))
			axiomKey = getAxiomKey(qname.toString());
		ArrayItemList<?> arrayItemList = null;
	    switch (operandType)
	    {
        case INTEGER:
            arrayItemList = new ArrayItemList<Long>(operandType, qname);
            break; 
        case DOUBLE:
            arrayItemList = new ArrayItemList<Double>(operandType, qname);
            break;
        case BOOLEAN:
            arrayItemList = new ArrayItemList<Boolean>(operandType, qname);
            break; 
        case STRING:
            arrayItemList = new ArrayItemList<String>(operandType, qname);
            break; 
        case DECIMAL:
            arrayItemList = new ArrayItemList<BigDecimal>(operandType, qname);
            break;
        case CURRENCY:
            arrayItemList = new ArrayItemList<BigDecimal>(operandType, qname);
            break;
        case TERM:
            final AxiomTermList itemList = new AxiomTermList(qname, axiomKey);
            parserAssembler.registerAxiomTermList(itemList);
            return itemList;
         case AXIOM:
            final AxiomList axiomList = new AxiomList(qname, axiomKey);
            parserAssembler.registerAxiomList(axiomList);
            return axiomList;
        case UNKNOWN:   
        default:
            throw new ParseException("List " + qname.toString() + " type unknown");
       }
       return arrayItemList;
    }
  	
    /**
     * Returns Dynamic ItemList object for this type. 
     * @param qname Qualified name of new variable
     * @param template Initialization template
     * @return ItemList object
     * @throws ParseException
     */
    protected ItemList<?> getDynamicListInstance(QualifiedName qname, Template template) throws ParseException
    {
        DynamicList<?> dynamicListList = null;
        switch (operandType)
        {
        case INTEGER:
            dynamicListList = new DynamicList<Long>(operandType, qname, template);
            break; 
        case DOUBLE:
            dynamicListList = new DynamicList<Double>(operandType, qname, template);
            break;
        case BOOLEAN:
            dynamicListList = new DynamicList<Boolean>(operandType, qname, template);
            break; 
        case STRING:
            dynamicListList = new DynamicList<String>(operandType, qname, template);
            break; 
        case DECIMAL:
        case CURRENCY:
            dynamicListList = new DynamicList<BigDecimal>(operandType, qname, template);
            break;
        default:
            throw new ParseException("List " + qname.toString() + " type does not permit initialization with list of values");
       }
       return dynamicListList;
    }
 
    /**
     * Returns operator for Currency Operand with country code set up according to
     * setting of QUALIFIER_STRING and QUALIFIER_OPERAND properties.
     * @param currencyOperand Currency operand, which has right operand assigned if QUALIFIER_OPERAND is set
     * @return CurrencyOperator object
     */
    protected CurrencyOperator getCurrencyOperator(RightOperand currencyOperand)
    {
        CurrencyOperator currencyOperator = new CurrencyOperator();
        String country = getPropertyString(QUALIFIER_STRING);
        if (country != null) // Country code set statically
            currencyOperator.setLocaleByCode(country);
        else
        {   // Country code set by evaluating a right operand assigned to the Currency Operand
            Operand countryOperand = (Operand)getProperty(QUALIFIER_OPERAND);
            if (countryOperand != null)
            {
                currencyOperand.setRightOperand(
                    new CountryOperand(
                        countryOperand.getQualifiedName(), //countryQname, 
                        currencyOperator.getTrait(), 
                        countryOperand));
            }
        }
        return currencyOperator;
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
	 * Create basic list appender variable. The expression evaluates a value which is appended to a list.
     * @param qname Qualified name of new variable
     * @param expression Operand object to provide value to be appended
     * @return Operand object
     * @throws ParseException 
	 */
	public Operand createAppender(QualifiedName qname, Operand expression, int id) throws ParseException
    {
	    OperandMap operandMap = parserAssembler.getOperandMap();
	    // The Appender is a singleton
        Operand operand = operandMap.getOperand(qname);
        if ((operand != null) && !(operand instanceof Appender))
            throw new ParseException(qname.toString() + " is not an appender");
        Appender appender;
        if (operand == null) 
        {
            ArrayIndex arrayIndex = new ArrayIndex(qname, 0, "appender");
            appender = new Appender(qname, qname, arrayIndex);
            parserAssembler.getOperandMap().addOperand(appender);
        }
        else
            appender = (Appender)operand; 
        String name = qname.getName();
        qname = new QualifiedName(name + id, qname);
        Variable var = new AppenderVariable(qname, name, expression);
        var.setRightOperand(appender);
        ParserTask parserTask = parserAssembler.addPending(appender);
        parserTask.setPriority(ParserTask.Priority.variable.ordinal());
        return var;
    }

	/**
	 * Returns qualified name for axiom key stored as a parameter
	 * @param listName Name of list which uses the key 
	 * @return QualifiedName object
	 * @throws ParseException
	 */
	protected QualifiedName getAxiomKey(String listName) throws ParseException
	{
        QualifiedName axiomKey = (QualifiedName)getProperty(AXIOM_KEY);
        if (axiomKey == null)
            throw new ParseException("List " + listName + " missing axiom key");
        return axiomKey;
	}
}
