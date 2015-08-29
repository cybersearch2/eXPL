/**
 * 
 */
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomListSpec;
import au.com.cybersearch2.classy_logic.list.AxiomListVariable;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.ItemListVariable;



/**
 * OperandMap
 * Operand and ItemList container. Items are referenced by name. 
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
    /** Tree Map of item lists, which create instances of variables, some of which need to be added to the operand map */
	protected Map<QualifiedName, ItemList<?>> listMap;
    /** Qualified name of operand context */
    protected QualifiedName qualifiedContextname;
    
	/**
	 * Construct OperandMap object
	 */
	public OperandMap(QualifiedName qualifiedContextname)
	{
		operandMap = new TreeMap<QualifiedName, Operand>();
		nameSet = new HashSet<String>();
		listMap = new TreeMap<QualifiedName, ItemList<?>>();
		this.qualifiedContextname = qualifiedContextname;
	}

	public QualifiedName getQualifiedContextname()
    {
        return qualifiedContextname;
    }

    public void setQualifiedContextname(QualifiedName qualifiedContextname)
    {
        this.qualifiedContextname = qualifiedContextname;
    }

    /**
	 * Returns all operand values in a map container
	 * @return Map with key of type Operand and value of type Object
	 */
	public  Map<Operand, Object> getOperandValues()
	{
		Map<Operand, Object> operandValueMap = new HashMap<Operand, Object>();
		for (Operand operand: operandMap.values())
		{
			if (!operand.isEmpty())
				operandValueMap.put(operand, operand.getValue());
		}
		return operandValueMap;
	}

	/**
	 * Set operand values
	 * @param operandValueMap Map container
	 */
	public void setOperandValues(Map<Operand, Object> operandValueMap)
	{
		for (Entry<Operand, Object> entry: operandValueMap.entrySet())
			if (entry.getValue() instanceof Null) // Null special case
				((Parameter)entry.getKey()).clearValue();
			else
			   entry.getKey().assign(entry.getValue());
		// Clear operands not included in operandValueMap
		for (Entry<QualifiedName, Operand> entry: operandMap.entrySet())
		{
			if (!operandValueMap.containsKey(entry.getValue()))
				((Parameter)entry.getValue()).clearValue();
		}
	}

	public void duplicateOperandCheck(String name)
	{
	    if (hasOperand(name))
	        throw new ExpressionException("Duplicate Operand name \"" + name + "\" encountered");
	}

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
    public Operand addOperand(String name, Operand expression)
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
		    QualifiedName sameScope = new QualifiedName(qname.getScope(), QualifiedName.EMPTY, qname.getName());
		    param = operandMap.get(sameScope);
		    if (param != null)
		        qname = sameScope;
		}
        if ((param == null) && !qname.getScope().isEmpty())
        {
            QualifiedName globalScope = new QualifiedName(QualifiedName.EMPTY, QualifiedName.EMPTY, qname.getName());
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
	 */
	public void addOperand(Operand operand)
    {
		if (operand.getName().isEmpty())
			throw new ExpressionException("addOperand() passed annonymous object");
		QualifiedName qname = operand.getQualifiedName();
        duplicateOperandCheck(qname);
		operandMap.put(qname, operand);
        nameSet.add(qname.getName());
    }

	/**
	 * Returns flag to indicate if this map contains operand with specified name
	 * @param name
	 * @return boolean
	 */
	public boolean hasOperand(String name)
	{
        if (!nameSet.contains(name)) 
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
	 * Returns operand of specified name or null if not found
     * @param qname Qualified name
	 * @return Operand
	 */
	public Operand getOperand(QualifiedName qname)
	{
		return operandMap.get(qname);
	}
	
	/**
	 * Add ItemList object to this container and it's name to the list Set
	 * @param qname Qualified name of list
	 * @param itemList ItemList object 
	 */
	public void addItemList(QualifiedName qname, ItemList<?> itemList)
	{
		if ((operandMap.get(qname) != null) || listMap.containsKey(qname))
			throw new ExpressionException("ItemList name \"" + qname.toString() + "\" clashes with existing Operand");
		listMap.put(qname, itemList);
	}

	/** 
	 * Returns Operand list referenced by name
     * @param qname Qualified name
	 * @return ItemList object or null if not exists
	 */
	public ItemList<?> getItemList(QualifiedName qname)
	{
		return listMap.get(qname);
	}
	
	/**
	 * Returns ItemListVariable object with specified name and index expression operand. 
	 * Will create object if it does not already exist.
	 * @param itemList The list
	 * @param expression Index expression operand
	 * @return ItemListVariable object
	 */
	public ItemListVariable<?> newListVariableInstance(ItemList<?> itemList, Operand expression)
	{
		String name = itemList.getName();
		ItemListVariable<?> listVariable = null;
		int index = -1;
		String suffix = null;
		if (!expression.isEmpty() && (expression instanceof IntegerOperand) && Term.ANONYMOUS.equals(expression.getName()))
	    {
		   index = ((Long)(expression.getValue())).intValue();
		   suffix = Long.toString(index);
		}
		else if (expression.isEmpty() && (expression instanceof Variable))
		{
			if (itemList instanceof AxiomTermList)
			{
				AxiomTermList axiomTermList = (AxiomTermList)itemList;
				List<String> axiomTermNameList = axiomTermList.getAxiomTermNameList();
				if (axiomTermNameList != null)
				{
					suffix = expression.getName();
					index = getIndexForName(name, suffix, axiomTermNameList);
				}
				else
					suffix = expression.toString();

			}
			else // Interpret identifier as a variable name for any primitive list
			{
				expression = new Variable(expression.getQualifiedName());
				suffix = expression.getName();
			}
		}
		if (suffix == null)
			suffix = expression.getName().isEmpty() ? expression.toString() : expression.getName();
		// IntegerOperand value is treated as fixed
		if (index >= 0)
			listVariable = getListVariable(itemList, name, index, suffix);
		else
			listVariable = itemList.newVariableInstance(expression, suffix, 0);
		return listVariable;
	}

	/**
	 * Returns index of item identified by name
	 * @param listName Name of list - used only for error reporting
	 * @param item Item name
	 * @param axiomTermNameList Term names of axiom source
	 * @return Index
	 */
	protected int getIndexForName(String listName, String item, List<String> axiomTermNameList) 
	{
	    int pos = item.lastIndexOf('.');
	    String name = pos == -1 ? item : item.substring(pos + 1);
		for (int i = 0; i < axiomTermNameList.size(); i++)
		{
			if (name.equals(axiomTermNameList.get(i)))
				return i;
		}
		throw new ExpressionException("List \"" + listName + "\" does not have term named \"" + item + "\"");
	}

	/**
	 * Returns a list variable for a term in an axiom in a axiom list. 
	 * Both the axiom and the term are referenced by index.
	 * There is only one variable instance for any specific index combination.
	 * @param axiomListSpec AxiomList specification
	 * @return AxiomListVariable object
	 */
	public AxiomListVariable newListVariableInstance(AxiomListSpec axiomListSpec) 
	{
        AxiomListVariable listVariable = null;
		// IntegerOperand value is treated as fixed
		if ((axiomListSpec.getAxiomIndex() >= 0) && (axiomListSpec.getTermIndex() >= 0))
			// The variable only has a single instance if both axiom and term indexes are fixed
			listVariable = getListVariable(axiomListSpec);
		else
		{
		    AxiomList axiomList = axiomListSpec.getAxiomList();
		    String suffix = axiomListSpec.getSuffix();
			if ((axiomListSpec.getAxiomIndex() >= 0))
				listVariable = axiomList.newVariableInstance(axiomListSpec.getAxiomIndex(), axiomListSpec.getTermExpression(), suffix);
			else if (axiomListSpec.getTermIndex() >= 0)
				listVariable = axiomList.newVariableInstance(axiomListSpec.getAxiomExpression(), axiomListSpec.getTermIndex(), suffix);
			else
				listVariable = axiomList.newVariableInstance(axiomListSpec.getAxiomExpression(), axiomListSpec.getTermExpression(), suffix);
		}
		return listVariable;
	}

	/**
	 * Returns operand referenced by name
	 * @param name
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

    /**
     * Copy contents of other OperandMap
     * @param operandMap2 OperandMap object
     */
	public void putAll(OperandMap operandMap2) 
	{
		operandMap.putAll(operandMap2.operandMap);
		listMap.putAll(operandMap2.listMap);
	}

	/**
	 * Returns ItemListVariable object with specified name and index. 
	 * There is only one variable instance for any specific index.
	 * Will create object if it does not already exist.
	 * @param itemList The owner list
	 * @param name List name
	 * @param index int
	 * @param suffix To append to name
	 * @return ItemListVariable object
	 */
	protected ItemListVariable<?> getListVariable(ItemList<?> itemList, String name, int index, String suffix)
	{
		// Variable name is list name with '_' index suffix
		QualifiedName listVarName = new QualifiedName(name + "_" + suffix, itemList.getQualifiedName());
		ItemListVariable<?> listVariable = (ItemListVariable<?>) get(listVarName);
		if (listVariable != null)
			return listVariable;
		// Use ItemList object to create new ItemListVariable instance
		listVariable = itemList.newVariableInstance(index, suffix, 0);
		return listVariable;
	}

	/**
	 * Returns a list variable for a term in an axiom in a axiom list. 
	 * Both the axiom and the term are referenced by index.
	 * There is only one variable instance for any specific index combination.
	 * @param axiomList The owner list
	 * @param name List name
	 * @param axiomIndex int
	 * @param termIndex int
	 * @param suffix To append to name
	 * @return AxiomListVariable object
	 */
	protected AxiomListVariable getListVariable(AxiomListSpec axiomListSpec) 
	{
		// Variable name is list name with '_' index suffix
	    String varName = axiomListSpec.getListName() + "_" + axiomListSpec.getAxiomIndex() + "_" + axiomListSpec.getSuffix();
		QualifiedName qualifiedtVarName = new QualifiedName(varName, axiomListSpec.getAxiomList().getQualifiedName());
		AxiomListVariable listVariable = (AxiomListVariable) get(qualifiedtVarName);
		if (listVariable != null)
			return listVariable;
		// Use axiomList object to create new ItemListVariable instance
		listVariable = axiomListSpec.getAxiomList().newVariableInstance(axiomListSpec.getAxiomIndex(), axiomListSpec.getTermIndex(), axiomListSpec.getSuffix());
		return listVariable;
	}

	/**
	 * Returns new ItemListVariable instance. 
	 * This is wrapped in an assignment evaluator if optional expression parameter needs to be evaluated.
	 * @param itemList The list
	 * @param index List index
	 * @param expression Optional expression operand
	 * @return Operand object
	 */
	public Operand setListVariable(ItemList<?> itemList, Operand index, Operand expression) 
	{
		ItemListVariable<?> variable = newListVariableInstance(itemList, index);
		if (expression != null)
		{
			if (expression.isEmpty() || (expression instanceof Evaluator))
			    // Expression needs evaluation
				return new Evaluator(itemList.getQualifiedName(), variable, "=", expression);
			// Expression has a value which will be assigned to the list item
			variable.assign(expression.getValue());
		}
		return variable;
	}

	/**
	 * Copy result axiom lists as iterables to supplied container
	 * @param prefix Scope name or empty if global scope
	 * @param listMap2 Container to receive lists
	 */
	public void copyLists(Map<QualifiedName, Iterable<Axiom>> listMap2) 
	{
		for (Entry<QualifiedName, ItemList<?>> entry: listMap.entrySet())
		{
		    ItemList<?> itemList = entry.getValue();
		    if (itemList.getItemClass().equals(Axiom.class))
		        AxiomUtils.copyList(entry.getKey(), (AxiomList)itemList, listMap2);
		}
	}

    /**
     * Copy result axioms to supplied container
     * @param prefix Scope name or empty if global scope
     * @param listMap2 Container to receive lists
     */
    public void copyAxioms(String prefix, Map<String, Axiom> axiomMap)
    {
        if (prefix == null)
            prefix = "";
        for (Entry<QualifiedName, ItemList<?>> entry: listMap.entrySet())
        {
            ItemList<?> itemList = entry.getValue();
            // Create deep copy in case item list is cleared
            Axiom axiom = null;
            if (itemList.getItemClass().equals(Term.class))
            {   // AxiomTermList contains backing axiom
                AxiomTermList axiomTermList = (AxiomTermList)itemList;
                axiom = new Axiom(entry.getKey().getName());
                Axiom source = axiomTermList.getAxiom();
                for (int i = 0; i < source.getTermCount(); i++)
                    axiom.addTerm(source.getTermByIndex(i));
            }
            else if (!itemList.getItemClass().equals(Axiom.class))
            {   // Regular ItemList contains objects which are packed into axiom to return
                axiom = new Axiom(entry.getKey().getName());
                Iterator<?> iterator = itemList.getIterable().iterator();
                while (iterator.hasNext())
                    axiom.addTerm(new Parameter(Term.ANONYMOUS, iterator.next()));
            }
            if (axiom != null)
            {
                // Use fully qualified key to avoid name collisions
                String key = prefix.isEmpty() ? entry.getKey().getName() : (prefix + "_" + entry.getKey().getName());
                axiomMap.put(key, axiom);
            }
        }
    }

	/**
	 * Clear result lists
	 */
	public void clearLists(List<QualifiedName> listNames) 
	{
	    for (QualifiedName listName: listNames)
	    {
		    ItemList<?> itemList= listMap.get(listName);
			itemList.clear();
	    }
	}

	/**
	 * Returns collection of the names of lists that are empty
	 * @return List of names
	 */
	public List<QualifiedName> getEmptyListNames()
	{
	    List<QualifiedName> listNames = new ArrayList<QualifiedName>();
	    for (ItemList<?> itemList: listMap.values())
	        if (itemList.isEmpty())
	            listNames.add(itemList.getQualifiedName());
	    return listNames;
	}

}
