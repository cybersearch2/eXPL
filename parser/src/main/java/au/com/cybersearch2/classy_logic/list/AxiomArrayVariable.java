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

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.operator.DelegateType;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;

/**
 * AxiomArrayVariable
 * Variable to access item list of AxiomTermList objects. 
 * The only operation allowed is assigment of one list item to a slot in other item list of AxiomTermList objects.  
 * @author Andrew Bowley
 * 3 Feb 2015
 */
public class AxiomArrayVariable extends ItemListVariable<AxiomTermList> 
{
	/**
	 * Construct AxiomArrayVariable object for fixed index
	 * @param itemList Owning list - must contain AxiomTermList objects
	 * @param index Fixed position in list
	 * @param suffix To append to name
	 */
	public AxiomArrayVariable(ItemList<?> itemList, int index, String suffix) 
	{
		super(itemList, DelegateType.ASSIGN_ONLY.getOperatorFactory().delegate(), index, suffix);
	}

	/**
	 * Construct AxiomArrayVariable object for expression index
	 * @param itemList Owning list - must contain AxiomTermList objects
	 * @param indexExpression Operand which evalualates index integer value 
	 * @param suffix To append to name
	 */
	public AxiomArrayVariable(ItemList<?> itemList, Operand indexExpression, String suffix) 
	{
		super(itemList, DelegateType.ASSIGN_ONLY.getOperatorFactory().delegate(), indexExpression, suffix);
	}

}
