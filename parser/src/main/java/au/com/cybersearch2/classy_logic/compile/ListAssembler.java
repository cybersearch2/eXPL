/**
    Copyright (C) 2017  www.cybersearch2.com.au

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermListIterable;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.ListType;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ListAssembler
 * Assembles list details collected by parser
 * @author Andrew Bowley
 * 12May,2017
 */
public class ListAssembler
{
    /** The axioms which are declared within the enclosing scope */ 
    protected Map<QualifiedName, List<Axiom>> axiomListMap;
    /** AxiomTermLists in template scope */
    protected Map<QualifiedName, AxiomTermList> axiomTermListMap;
    /** The axiom listeners, all belonging to list variables */
    protected Map<QualifiedName, List<AxiomListener>> axiomListenerMap;
    /** Item lists */
    protected Map<QualifiedName, ItemList<?>> listMap;
    /** Scope */
    protected Scope scope;

    /**
     * Construct ListAssembler object
     * @param scope Enclosing scope
     */
    public ListAssembler(Scope scope)
    {
        this.scope = scope;
        axiomListMap = new HashMap<QualifiedName, List<Axiom>>();
        axiomTermListMap = new HashMap<QualifiedName, AxiomTermList>();
        axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
        listMap = new TreeMap<QualifiedName, ItemList<?>>();
    }
    
    /**
     * Add contents of another ListAssembler to this object
     * @param listAssembler Other ListAssembler object
     */
    public void addAll(ListAssembler listAssembler) 
    {
        axiomListMap.putAll(listAssembler.axiomListMap);
        axiomTermListMap.putAll(listAssembler.axiomTermListMap);
        axiomListenerMap.putAll(Collections.unmodifiableMap(listAssembler.axiomListenerMap));
        listMap.putAll(listAssembler.listMap);
    }

    /**
     * Returns flag set true if list specified by type and name exists
     * @param listType List type
     * @param qualifiedName List name
     * @return boolean
     */
    public boolean existsKey(ListType listType, QualifiedName qualifiedName)
    {
        switch (listType)
        {
        case axiom_item:
            return axiomListMap.containsKey(qualifiedName);
        case term:
            return axiomTermListMap.containsKey(qualifiedName);
        default:
            return false;
        }
    }

    /**
     * Add given axiom to axiom item list specified by name
     * @param qualifiedName List name
     * @param axiom The axiom to add
     */
    public void add(QualifiedName qualifiedName, Axiom axiom)
    {
        List<Axiom> axiomList = axiomListMap.get(qualifiedName);
        if (axiomList != null)
            axiomList.add(axiom);
    }
 
    /**
     * Returns axiom item list specified by name
     * @param qualifiedName List name
     * @return list containing axioms
     */
    public List<Axiom> getAxiomItems(QualifiedName qualifiedName)
    {
        return axiomListMap.get(qualifiedName);
    }

    /**
     * Returns new axiom item list
     * @param qualifiedName List name
     * @return list to contain axioms
     */
    public List<Axiom> axiomItemsInstance(QualifiedName qualifiedName)
    {
        List<Axiom> axiomList = new ArrayList<Axiom>();
        axiomListMap.put(qualifiedName, axiomList);
        return axiomList;
    }

    /**
     * Returns axiom term list specified by name
     * @param qualifiedName List name
     * @return AxiomTermList object, which is created if it does not already exist
     */
    public AxiomTermList getAxiomTerms(QualifiedName qualifiedName)
    {
        AxiomTermList axiomTermList = axiomTermListMap.get(qualifiedName);
        if (axiomTermList == null)
        {
            axiomTermList = new AxiomTermList(qualifiedName, qualifiedName);
            axiomTermListMap.put(qualifiedName, axiomTermList);
        }
        return axiomTermList;
    }
    
    /**
     * Returns container with all axiom listeners belonging to this scope mapped by name
     * @return map object
     */
    public Map<QualifiedName, List<AxiomListener>> getAxiomListenerMap()
    {
        return axiomListenerMap;
    }

    /**
     * Add given axiom listener to list specified by name
     * @param qualifiedName Name of list
     * @param axiomListener The axiom listener to add
     */
    public void add(QualifiedName qualifiedName, AxiomListener axiomListener)
    {
        List<AxiomListener> axiomListenerList = getAxiomListenerList(qualifiedName);
        if (axiomListenerList != null)
            axiomListenerList.add(axiomListener);
    }
 
    /**
     * Create new axiom item list. Do not report a duplicate error if list already exists.
     * @param qualifiedName List name
     * @return flag set true if list created
     * @see AxiomAssembler#saveAxiom(QualifiedName)
     */
    public boolean createAxiomItemList(QualifiedName qualifiedName)
    {   // Create new axiom list if one does not already exist
        boolean axiomItemListExists = existsKey(ListType.axiom_item, qualifiedName);
        if (!axiomItemListExists)
            axiomItemsInstance(qualifiedName);
        return !axiomItemListExists;
    }

    /**
     * Returns item list specified by qualified name.
     * @param qname Qualified name of list
     * @return ItemList object or null if an axiom parameter is named and is not yet set 
     * @throws ExpressionException if item list not found
     */
    public ItemList<?> getItemList(QualifiedName qname)
    {
        ItemList<?> itemList = findItemList(qname);
        if (itemList == null)
            throw new ExpressionException("List not found with name \"" + qname.toString() + "\"");
        return itemList;
    }
 
    /**
     * Returns axiom term list specified by qualified name.
     * @param qname Qualified name of list
     * @return AxiomTermList object or null if list not found 
     * @throws ExpressionException if item list not found
     */
    public AxiomTermList getAxiomTermList(QualifiedName qname)
    {
        ItemList<?> itemList = listMap.get(qname);
        if (itemList == null)
            throw new ExpressionException("List not found with name \"" + qname.toString() + "\"");
        if (!(itemList instanceof AxiomTermList))
            throw new ExpressionException("List with name \"" + qname.toString() + "\" not expected type of 'AxiomTermList'");
        return (AxiomTermList) itemList;
    }
    
    /**
     * Returns item list identified by scope and name. 
     * If scope not, global, search for list in global scope too.
     * @param scope The name of the scope
     * @param listName The name of the list
     * @return ItemList or null if list not found
     */
    public ItemList<?> getItemList(String scopeName, String listName)
    {
        QualifiedName qualifiedListName = new QualifiedName(scopeName, listName);
        ItemList<?> itemList = listMap.get(qualifiedListName);
        if ((itemList == null) && !scopeName.isEmpty())
        {
            qualifiedListName = new QualifiedName(listName);
            itemList = scope.getGlobalListAssembler().listMap.get(qualifiedListName);
        }
        return itemList;
    }
    
    /**
     * Returns item list specified by qualified name. 
     * Ensures scope part of name identifies an existing scope.
     * @param qname Qualified name of list
     * @return ItemList object or null if not found
     */
    public ItemList<?> findItemList(QualifiedName qname)
    {
        ItemList<?> itemList = null;
        Scope nameScope = qname.getScope().isEmpty() ? scope.getGlobalScope() : scope.findScope(qname.getScope());
        if (nameScope != null)
            itemList = nameScope.getParserAssembler().getListAssembler().listMap.get(qname);
        return itemList;
    }

    /**
     * Add ItemList object to it's container identified by name
     * @param qname Qualified name of list
     * @param itemList ItemList object to add
     */
    public void addItemList(QualifiedName qname, ItemList<?> itemList)
    {
        if (listMap.containsKey(qname))
            throw new ExpressionException("ItemList name \"" + qname.toString() + "\" clashes with existing Operand");
        listMap.put(qname, itemList);
    }

    /**
     * Copy axiom lists as iterables to supplied container
     * @param axiomIterableMap Container to receive lists
     */
    public void copyLists(Map<QualifiedName, Iterable<Axiom>> axiomIterableMap) 
    {
        for (Entry<QualifiedName, ItemList<?>> entry: listMap.entrySet())
        {
            ItemList<?> itemList = entry.getValue();
            if (itemList instanceof TermListIterable)
                axiomIterableMap.put(entry.getKey(), getAxiomIterable((TermListIterable)itemList, itemList.getLength()));
        }
        if (!scope.getName().equals(QueryProgram.GLOBAL_SCOPE))
            scope.getGlobalListAssembler().copyLists(axiomIterableMap);
    }

    /**
     * Copy axioms from all item lists containing single axioms to supplied container
     * @param axiomMap Container to receive axioms
     */
    public void copyAxioms(Map<QualifiedName, Axiom> axiomMap)
    {
        for (Entry<QualifiedName, ItemList<?>> entry: listMap.entrySet())
            copyAxiom(entry, axiomMap);
    }
    
    /**
     * Clear item lists identified by name
     * @param listNames List of qualified list names
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

    /**
     * Copy AxiomList to container holding iterable objects
     * @param termListIterable AxiomTermList iterable source of axioms
     */
    public Iterable<Axiom> getAxiomIterable(TermListIterable termListIterable, int listSize)
    {
        final List<Axiom> axiomList = new ArrayList<Axiom>(listSize);
        Iterator<AxiomTermList> axiomTermListIterator = termListIterable.iterator();
        while (axiomTermListIterator.hasNext())
            axiomList.add(axiomTermListIterator.next().getAxiom());
        return new Iterable<Axiom>(){
        
            @Override
            public Iterator<Axiom> iterator()
            {
                return axiomList.iterator();
            }};
    }

    /**
     * Returns list of axiom listeners for specified name, creating the list if it does not already exist.
     * @param qualifiedName Name of list
     * @return List containing AxiomListener objects or null if list not found
     */
    public List<AxiomListener> getAxiomListenerList(QualifiedName qualifiedName)
    {
        List<AxiomListener> axiomListenerList = axiomListenerMap.get(qualifiedName);
        if (axiomListenerList == null)
        {
            axiomListenerList = new ArrayList<AxiomListener>();
            axiomListenerMap.put(qualifiedName, axiomListenerList);
        }
        return axiomListenerList;
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
        for (int i = 0; i < axiomTermNameList.size(); i++)
        {
            if (item.equals(axiomTermNameList.get(i)))
                return i;
        }
        throw new ExpressionException("List \"" + listName + "\" does not have term named \"" + item + "\"");
    }

    /**
     * Returns axiom list listener which adds the list to a given operand container
     * @param operandMap OperandMap object
     * @return AxiomListListener object
     */
    protected AxiomListListener axiomListListenerInstance()
    {
        return new AxiomListListener(){

            @Override
            public void addAxiomList(QualifiedName qname, AxiomList axiomList)
            {
                if (!listMap.containsKey(qname))
                    listMap.put(qname, axiomList);
            }
        };
    }
 
    /**
     * Removes axiom item list specified by name
     * @param qualifiedName
     * @return list removed from container or null if list not found
     */
    protected List<Axiom> removeAxiomItems(QualifiedName qualifiedName)
    {
        return axiomListMap.remove(qualifiedName);
    }

    /**
     * Set axiom container from supplied axiom(s). 
     * @param axiomContainer AxiomList object or AxiomTermList object
     * @param axiomItems A single axiom if setting an AxiomTermList, otherwise an axiom collection
     */
    protected void setAxiomContainer(AxiomContainer axiomContainer, List<Axiom> axiomItems)
    {
        AxiomListener axiomListener = axiomContainer.getAxiomListener();
        QualifiedName axiomKey = axiomContainer.getKey();
        if (axiomContainer.getOperandType() == OperandType.AXIOM)
            // Populate axiom list if already created by the script being compiled
            // No listener required
            for (Axiom axiom: axiomItems)
               axiomListener.onNextAxiom(axiomKey, axiom);
        else
            add(axiomKey, axiomListener);
    }

    /**
     * Copy result axioms to supplied container
     * @param axiomMap Container to receive axioms
     */
    protected Axiom copyAxiom(Entry<QualifiedName, ItemList<?>> entry, Map<QualifiedName, Axiom> axiomMap)
    {
        ItemList<?> itemList = entry.getValue();
        // Create deep copy in case item list is cleared
        Axiom axiom = null;
        if (itemList.getOperandType().equals(OperandType.TERM))
        {   // AxiomTermList contains backing axiom
            AxiomTermList axiomTermList = (AxiomTermList)itemList;
            axiom = new Axiom(entry.getKey().getName());
            Axiom source = axiomTermList.getAxiom();
            for (int i = 0; i < source.getTermCount(); i++)
                axiom.addTerm(source.getTermByIndex(i));
        }
        else if (!itemList.getOperandType().equals(OperandType.AXIOM))
        {   // Regular ItemList contains objects which are packed into axiom to return
            axiom = new Axiom(entry.getKey().getName());
            Iterator<?> iterator = itemList.getIterable().iterator();
            while (iterator.hasNext())
                axiom.addTerm(new Parameter(Term.ANONYMOUS, iterator.next()));
        }
        if (axiom != null)
            // Use fully qualified key to avoid name collisions
            axiomMap.put(itemList.getQualifiedName(), axiom);
        return axiom;
    }
}
