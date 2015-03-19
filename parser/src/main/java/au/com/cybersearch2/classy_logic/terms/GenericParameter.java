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
package au.com.cybersearch2.classy_logic.terms;

import au.com.cybersearch2.classy_logic.helper.Null;


/**
 * GenericParameter
 * Parameter with value of generic type. 
 * @author Andrew Bowley
 * 16 Nov 2014
 */
public  class GenericParameter<T> extends Parameter  
{

	/**
	 * Construct a non-empty named GenericParameter object
	 * @param name String
	 * @param value Object of generic type T
	 */
	protected GenericParameter(String name, T value) 
	{
		super(name, value);

	}

	/**
	 * Construct an empty named Parameter object
	 * @param name String
	 */
	protected GenericParameter(String name) 
	{
		super(name);
	}

	/**
	 * Set value
	 * @param value Object of generic type T
	 */
	public void setValue(T value)
	{
		if (value == null)
			this.value = new Null();
		else
			this.value = value;
	    this.empty = false;
	}

	/**
	 * Returns value
	 * @return Object of generic type T
	 */
	@SuppressWarnings("unchecked")
    @Override
    public T getValue()
    {
	    return (T) value;
    }

}
