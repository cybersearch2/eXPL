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
package au.com.cybersearch2.classy_logic.helper;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;


/**
 * NameParserTest
 * @author Andrew Bowley
 * 19Jul.,2017
 */
public class NameParserTest
{
    static final String TEST_NAME1 = "one_part_name";
    static final String TEST_NAME_AT = "one_part_name@";
    static final String TEST_NAME2 = "part1.part2";
    static final String TEST_AT_NAME2 = "part2@part1";
    static final String TEST_NAME2_AT = "part1.part2@";
    static final String TEST_NAME3 = "part1.part2.part3";
    static final String TEST_AT_NAME3 = "part3@part2.part1";
    static final String TEST_NAME2_AT_NAME = "part3.part2@part1";
    static final String TEST_NAME4 = "part3.part2@part1.part4";

    @Test
    public void testEmptyName()
    {
        NameParser nameParser = new NameParser("");
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.ANONYMOUS);
    }

    @Test
    public void test1_partName()
    {
        NameParser nameParser = new NameParser(TEST_NAME1);
        assertThat(nameParser.getQualifiedName()).isEqualTo(new QualifiedName(TEST_NAME1));
    }
    
    @Test
    public void test1_partNameAt()
    {
        NameParser nameParser = new NameParser(TEST_NAME_AT);
        assertThat(nameParser.getQualifiedName()).isEqualTo(new QualifiedName(TEST_NAME1));
    }
    
    @Test
    public void test2_partName()
    {
        NameParser nameParser = new NameParser(TEST_NAME2);
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.parseGlobalName(TEST_NAME2));
    }
    
    @Test
    public void test2_partNameAt()
    {
        NameParser nameParser = new NameParser(TEST_NAME2_AT);
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.parseGlobalName("part2.part1"));
    }
    
    @Test
    public void test2_at_partName()
    {
        NameParser nameParser = new NameParser(TEST_AT_NAME2);
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.parseName(TEST_NAME2));
    }
    
    @Test
    public void test2_at_partName_at()
    {
        try
        {
            new NameParser(TEST_AT_NAME2 + "@");
            failBecauseExceptionWasNotThrown(ExpressionException.class);
        }
        catch(ExpressionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Name \"" + TEST_AT_NAME2 + "@" + "\" with more than one \"@\" is invalid");
        }
    }
        
    @Test
    public void test3_partName()
    {
        NameParser nameParser = new NameParser(TEST_NAME3);
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.parseGlobalName(TEST_NAME3));
    }

    @Test
    public void test3_at_partName()
    {
        NameParser nameParser = new NameParser(TEST_AT_NAME3);
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.parseName(TEST_NAME3));
    }

    @Test
    public void test2_at_name()
    {
        NameParser nameParser = new NameParser(TEST_NAME2_AT_NAME);
        assertThat(nameParser.getQualifiedName()).isEqualTo(QualifiedName.parseName(TEST_NAME3));
    }
    
    @Test
    public void test_invalidName()
    {
        try
        {
            new NameParser("." +TEST_AT_NAME2);
            failBecauseExceptionWasNotThrown(ExpressionException.class);
        }
        catch(ExpressionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Name \"" + "." +TEST_AT_NAME2 + "\" is invalid");
        }
    }
    
    @Test
    public void test_invalidNameAfterAt()
    {
        try
        {
            new NameParser(TEST_AT_NAME3 + ".");
            failBecauseExceptionWasNotThrown(ExpressionException.class);
        }
        catch(ExpressionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Name \"" + TEST_AT_NAME3 + "." + "\" is invalid after \"@\"");
        }
    }
    
    @Test
    public void test_invalidName4()
    {
        try
        {
            new NameParser(TEST_NAME4);
            failBecauseExceptionWasNotThrown(ExpressionException.class);
        }
        catch(ExpressionException e)
        {
            assertThat(e.getMessage()).isEqualTo("Name \"" + TEST_NAME4 + "\" with more than 3 parts is invalid");
        }
    }
}
