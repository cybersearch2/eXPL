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
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * FileResourceProvider
 * @author Andrew Bowley
 * 6Jan.,2017
 */
public class FileResourceProvider implements ResourceProvider
{
    String resourceName;
    File resourceBase;
    List<Runnable> onCloseHandlerList;
    AxiomListener axiomListener;

    public FileResourceProvider(String resourceName, File resourceBase)
    {
        this.resourceName = resourceName;
        this.resourceBase = resourceBase;
    }
 
    public void chainListener(AxiomListener axiomListener)
    {
        this.axiomListener = axiomListener;
    }
    
    @Override
    public String getName()
    {
        return resourceName;
    }

    @Override
    public void open(Map<String, Object> properties) throws ExpressionException
    {
    }

    @Override
    public void close()
    {
        if (onCloseHandlerList != null)
            for (Runnable handler: onCloseHandlerList)
                handler.run();
    }

    @Override
    public Iterator<Axiom> iterator(Archetype<Axiom,Term> archetype)
    {
        String filename = archetype.getQualifiedName().toString();
        File axiomFile = new File(resourceBase, filename);
        if (onCloseHandlerList == null)
            onCloseHandlerList = new ArrayList<Runnable>();
        FileAxiomIterator fileAxiomIterator = new FileAxiomIterator(axiomFile); 
        onCloseHandlerList.add(fileAxiomIterator.getOnCloseHandler());
        return fileAxiomIterator;
    }

    @Override
    public AxiomListener getAxiomListener(String name)
    {
        if (onCloseHandlerList == null)
            onCloseHandlerList = new ArrayList<Runnable>();
        File axiomFile = new File(resourceBase, name);
        final FileAxiomListener fileAxiomListener =  new FileAxiomListener(name, axiomFile);
        onCloseHandlerList.add(fileAxiomListener.getOnCloseHandler());
        if (axiomListener != null)
        {
            return new AxiomListener(){

                @Override
                public void onNextAxiom(QualifiedName qname, Axiom axiom)
                {
                    fileAxiomListener.onNextAxiom(qname, axiom);
                    axiomListener.onNextAxiom(qname, axiom);
                }};
        }
        return fileAxiomListener;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

}
