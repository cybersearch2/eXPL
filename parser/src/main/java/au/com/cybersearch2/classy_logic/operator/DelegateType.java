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
package au.com.cybersearch2.classy_logic.operator;

import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.OperatorFactory;

/**
 * DelegateType
 * Classifies all cases where a value is assigned to a variable. 
 * If value is not one of 7 specific types, is it classified as ASSIGN_ONLY.
 * @author Andrew Bowley
 * 29Apr.,2017
 */
public enum DelegateType
{
    STRING,
    INTEGER,
    DOUBLE,
    DECIMAL,
    BOOLEAN,
    AXIOM, 
    NULL,
    ASSIGN_ONLY;
 
    /** Creates an Operator instance specific to one DelegateType */
    private OperatorFactory operatorFactory;
 
    /**
     * Returns operator factory for delegate type
     * @return OperatorFactory object
     */
    public OperatorFactory getOperatorFactory()
    {
        return operatorFactory;
    }

    static
    {
        STRING.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new StringOperator();
            }
        };

        INTEGER.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new IntegerOperator();
            }
        };
        
        DOUBLE.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new DoubleOperator();
            }
        };
        
        DECIMAL.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new BigDecimalOperator();
            }
        };
        
        BOOLEAN.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new BooleanOperator();
            }
        };
        
        AXIOM.operatorFactory = new OperatorFactory()
                {
            @Override
            public Operator delegate()
            {
                return new AxiomOperator();
            }
        };
        
        ASSIGN_ONLY.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new AssignOnlyOperator();
            }
        };
        
        NULL.operatorFactory = new OperatorFactory()
        {
            @Override
            public Operator delegate()
            {
                return new NullOperator();
            }
        };
    }
}
