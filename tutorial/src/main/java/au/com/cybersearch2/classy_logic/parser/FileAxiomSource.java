/**
    Copyright (C) 2016  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * FileAxiomSource
 * @author Andrew Bowley
 * 8Jan.,2017
 */
public class FileAxiomSource implements AxiomSource
{
    File axiomFile;
    List<String> axiomTermNameList;
    List<Runnable> onCloseHandlerList;
    
    public FileAxiomSource(File axiomFile, List<String> axiomTermNameList)
    {
        this.axiomFile = axiomFile;
        this.axiomTermNameList = axiomTermNameList;
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#iterator()
     */
    @Override
    public Iterator<Axiom> iterator()
    {
        if (onCloseHandlerList == null)
            onCloseHandlerList = new ArrayList<Runnable>();
        FileAxiomIterator fileAxiomIterator = new FileAxiomIterator(axiomFile); 
        onCloseHandlerList.add(fileAxiomIterator.getOnCloseHandler());
        return fileAxiomIterator;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#getAxiomTermNameList()
     */
    @Override
    public List<String> getAxiomTermNameList()
    {
        return axiomTermNameList;
    }

    public void close()
    {
        if (onCloseHandlerList != null)
            for (Runnable handler: onCloseHandlerList)
                handler.run();
    }

    public Runnable getOnCloseHandler()
    {
        return new Runnable(){

            @Override
            public void run()
            {
                close();
            }};
    }

}
