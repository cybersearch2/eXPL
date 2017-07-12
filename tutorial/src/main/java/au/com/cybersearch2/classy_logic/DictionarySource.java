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
package au.com.cybersearch2.classy_logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.terms.StringTerm;

/**
 * DictionarySource
 * @author Andrew Bowley
 * 10 Jul 2017
 */
public class DictionarySource implements AxiomSource 
{
    BufferedReader reader;
    String axiomName;
    List<String> axiomTermNameList;
    
    /**
     * 
     */
    public DictionarySource() 
    {
        axiomName = "dictionary";
        axiomTermNameList = new ArrayList<String>(1);
        axiomTermNameList.add("entry");
    }

    public DictionarySource(String axiomName, List<String> axiomTermNameList) 
    {
        this.axiomName = axiomName;
        this.axiomTermNameList = axiomTermNameList;
    }
    
    @Override
    public Iterator<Axiom> iterator() 
    {
        File dictionaryFile = new File("src/main/resources", "definitions.txt");
        
        try 
        {
            reader = new BufferedReader(new FileReader(dictionaryFile));
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        return new Iterator<Axiom>()
        {
            String line;
            AxiomArchetype axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName(axiomName));
            
            @Override
            public boolean hasNext() 
            {
                if (reader == null)
                    return false;
                try 
                {
                    String[] strings;
                    do
                    {
                        line = reader.readLine();
                        if (line == null)
                        {
                            reader.close();
                            reader = null;
                            return false;
                        }
                        strings = line.split("-");
                    } while (strings.length < 2);

                } 
                catch (IOException e) 
                {
                    reader = null;
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public Axiom next() 
            {
                StringTerm entry = new StringTerm(line.trim());
                entry.setName(axiomTermNameList.get(0));
                List<Term> terms = new ArrayList<Term>(1);
                terms.add(entry);
                Axiom axiom = axiomArchetype.itemInstance(terms);
                return axiom;
            }

            @Override
            public void remove() 
            {
            }
        };
    }

    @Override
    public List<String> getAxiomTermNameList()
    {
        return axiomTermNameList;
    }
}
