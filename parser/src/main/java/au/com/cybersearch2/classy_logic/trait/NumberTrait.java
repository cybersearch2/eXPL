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
package au.com.cybersearch2.classy_logic.trait;

import java.text.NumberFormat;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.interfaces.StringCloneable;

/**
 * NumberTrait
 * @author Andrew Bowley
 * 21Apr.,2017
 */
public abstract class NumberTrait<T> extends DefaultTrait implements StringCloneable
{

    public NumberTrait(OperandType operandType)
    {
        super(operandType);
    }

    /**
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#formatValue(java.lang.Object)
     */
    @Override
    public String formatValue(Object value)
    {
        NumberFormat numberFormat = NumberFormat.getInstance(getLocale());
        String formatValue = numberFormat.format(value);
        return formatValue;
    }

    public abstract T parseValue(String string);

}
