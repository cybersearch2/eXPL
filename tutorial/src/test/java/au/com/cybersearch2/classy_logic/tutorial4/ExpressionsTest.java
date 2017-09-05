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
package au.com.cybersearch2.classy_logic.tutorial4;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.compile.SourceMarker;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * ExpressionsTest
 * @author Andrew Bowley
 * 5Feb.,2017
 */
public class ExpressionsTest
{
    @Test
    public void testExpressions() throws Exception
    {
        Expressions expressions = new Expressions();
        Axiom axiom = expressions.checkExpressions();
        Term evaluateTerm = axiom.getTermByName("can_evaluate");
        assertThat(((Boolean)evaluateTerm.getValue()).booleanValue()).isTrue();
        Iterator<SourceMarker> iterator = expressions.getParserContext().getSourceMarkerSet().iterator();
        assertThat(iterator.hasNext()).isTrue();
        SourceMarker sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("query expressions (12,1)");
        assertThat(sourceMarker.getHeadSourceItem()).isNotNull();
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("x_y:evaluate (12,26) (12,37)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("axiom x_y (1,1)");
        sourceMarker = iterator.next();
        //System.out.println(sourceMarker.toString());
        assertThat(sourceMarker.toString()).isEqualTo("template evaluate (3,1)");
        sourceItem = sourceMarker.getHeadSourceItem();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_add=x+y==3 (4,3) (4,30)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_subtract=y-x==1 (5,3) (5,35)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_multiply=x*y==2 (6,3) (6,35)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_divide=6/y==3 (7,3) (7,33)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_override_precedence=y+1*2>x*5 (8,3) (8,55)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        //System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_assign=y0==6&&y==6 (9,3) (9,46)");
        sourceItem = sourceItem.getNext();
        assertThat(sourceItem).isNotNull();
        System.out.println(sourceItem.toString());
        assertThat(sourceItem.toString()).isEqualTo("boolean can_evaluate=can_add&&can_subtract&&can_multiply&&can_divide&&can_override_precedence&&can_assign (10,3) (11,1)");
    }
    
}
