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
package au.com.cybersearch2.classy_logic.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomUtils
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class AxiomUtils
{
    public static List<Axiom> copyItemList(String listName, ItemList<?> itemList)
    {
        List<Axiom> copyList = new ArrayList<Axiom>();
        // Create deep copy in case item list is cleared
        Axiom axiom = null;
        if (itemList.getItemClass().equals(Term.class))
        {   // AxiomTermList contains backing axiom
            AxiomTermList axiomTermList = (AxiomTermList)itemList;
            axiom = new Axiom(listName);
            Axiom source = axiomTermList.getAxiom();
            for (int i = 0; i < source.getTermCount(); i++)
                axiom.addTerm(source.getTermByIndex(i));
            copyList.add(axiom);
        }
        else if (!itemList.getItemClass().equals(Axiom.class))
        {   // Regular ItemList contains objects which are packed into axiom to return
            axiom = new Axiom(listName);
            Iterator<?> iterator = itemList.getIterable().iterator();
            while (iterator.hasNext())
                axiom.addTerm(new Parameter(Term.ANONYMOUS, iterator.next()));
            copyList.add(axiom);
        }
        else
        {
            AxiomList axiomList = (AxiomList)itemList;
            Iterator<AxiomTermList> iterator = axiomList.getIterable().iterator();
            while (iterator.hasNext())
                copyList.add(copyAxiom(iterator.next().getAxiom()));
        }
        return copyList;
    }

    public static Axiom copyAxiom(Axiom axiom)
    {
        Axiom copyAxiom = new Axiom(axiom.getName());
        for (int i = 0; i < axiom.getTermCount(); i++)
            copyAxiom.addTerm(copyTerm(axiom.getTermByIndex(i)));
        return copyAxiom;
    }
    
    public static Term copyTerm(Term term)
    {
        Term copyTerm = new Parameter(term.getName());
        Object value = term.getValue();
        if (term.getValueClass() == ItemList.class)
        {
            ItemList<?> itemList = (ItemList<?>) value;
            List<Axiom> copyListAxioms = copyItemList(itemList.getName(), itemList);
            if (term.getValueClass() == AxiomList.class)
            {
                AxiomList axiomList = (AxiomList)itemList;
                AxiomList copyAxiomList = new AxiomList(axiomList.getName(), axiomList.getKey());
                copyAxiomList.setAxiomTermNameList(axiomList.getAxiomTermNameList());
                int index = 0;
                for (int i = 0; i < axiomList.getLength(); )
                {
                    if (axiomList.hasItem(index))
                    {
                        copyAxiomList.assignItem(index, copyListAxioms.get(i));
                        ++i;
                    }
                    ++index;
                }
                value = copyAxiomList;
            }
            else 
                value = copyListAxioms.get(0);
        }
        copyTerm.assign(value);
        return copyTerm;
    }
}
