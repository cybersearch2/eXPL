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
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.terms.StringTerm;

/**
 * LexiconSource
 * @author Andrew Bowley
 * 21 Dec 2014
 */
public class LexiconSource implements AxiomSource 
{
	BufferedReader reader;
	String axiomName;
	List<String> axiomTermNameList;
	
	/**
	 * 
	 */
	public LexiconSource() 
	{
		axiomName = "Lexicon";
		axiomTermNameList = new ArrayList<String>(2);
		axiomTermNameList.add("Word");
		axiomTermNameList.add("Definition");
	}

	public LexiconSource(String axiomName, List<String> axiomTermNameList) 
	{
		this.axiomName = axiomName;
		this.axiomTermNameList = axiomTermNameList;
	}
	
	@Override
	public Iterator<Axiom> iterator() 
	{
		File dictionaryFile = new File("src/test/resources", "definitions.txt");
		
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
			String[] strings;
	        AxiomArchetype axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName(axiomName));
			
			@Override
			public boolean hasNext() 
			{
				if (reader == null)
					return false;
	            try 
	            {
	            	do
		            {
		                String line = reader.readLine();
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
				StringTerm word = new StringTerm(strings[0].trim());
				word.setName(axiomTermNameList.get(0));
				StringTerm definition = new StringTerm(strings[1].trim());
				definition.setName(axiomTermNameList.get(1));
				List<Term> terms = new ArrayList<Term>(2);
				terms.add(word);
				terms.add(definition);
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
