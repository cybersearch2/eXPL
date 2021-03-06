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
package au.com.cybersearch2.classy_logic.tutorial13;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * ForeignScopeTest
 * @author Andrew Bowley
 * 25Apr.,2017
 */
public class ForeignScopeTest3
{
    @Test
    public void testForeignScope() throws Exception
    {
        File testFile = new File("src/main/resources/tutorial13", "foreign-scope3.txt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "UTF-8"));
        ForeignScope3 foreignScope3 = new ForeignScope3();
        List<Axiom> solutionList = foreignScope3.getFormatedTotalAmount();
        for (Axiom formatedTotal: solutionList)
        {
            QualifiedName key = formatedTotal.getArchetype().getQualifiedName();
            AxiomTermList axiomList = new AxiomTermList(key, key);
            axiomList.setAxiom(formatedTotal);
            for (int i = 0; i < formatedTotal.getTermCount(); ++i)
                checkSolution(reader, axiomList.getItem(i).toString());
        }
        reader.close();
     }

    
    protected void checkSolution(BufferedReader reader, String total)
    {
        try
        {
            String line = reader.readLine();
            assertThat(total).isEqualTo(line);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }

    }
}
