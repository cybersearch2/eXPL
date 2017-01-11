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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import com.thoughtworks.xstream.XStream;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * FileAxiomIterator
 * @author Andrew Bowley
 * 8Jan.,2017
 */
public class FileAxiomIterator implements Iterator<Axiom>
{
    int count;
    File axiomFile;
    Axiom current;
    FileInputStream fileInputStream;
    ObjectInputStream ois;

    public FileAxiomIterator(File axiomFile)
    {
        this.axiomFile = axiomFile;
        FileInputStream reader = null;
        try
        {
            XStream xStream = new XStream();
            xStream.alias("axiomHeader", AxiomHeader.class);
            File headerFile = new File(axiomFile.getAbsolutePath() + ".xml");
            if (headerFile.exists())
            {
                reader = new FileInputStream(headerFile);
                AxiomHeader axiomHeader = (AxiomHeader) xStream.fromXML(reader);
                count = axiomHeader.getCount();
                if (count > 0)
                {
                    fileInputStream = new FileInputStream(axiomFile);
                    ois = new ObjectInputStream(fileInputStream);
                    doIterate();
                }
            }
        }
        catch (FileNotFoundException e)
        {
            throw new ExpressionException(axiomFile.toString() + " not found", e);
        }
        catch (IOException e)
        {
            throw new ExpressionException(axiomFile.toString() + " error", e);
        }
        finally
        {
            if ((count <= 0) && (fileInputStream != null))
                close(fileInputStream);
            if (reader != null)
                close(reader);
        }
    }
    
    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext()
    {
        return current != null && (count >= 0);
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Axiom next()
    {
        if (current == null)
            throw new NoSuchElementException("Axiom from " + axiomFile.toString());
        Axiom nextAxiom = current;
        if (count > 0)
            doIterate();
        return nextAxiom;
    }

    public Runnable getOnCloseHandler()
    {
        return new Runnable(){

            @Override
            public void run()
            {
                close(ois);
            }};
    }

    private void doIterate()
    {
        try
        {
            current = readNextAxiom();
            --count;
        }
        catch (IOException e)
        {
            throw new ExpressionException(axiomFile.toString() + " error", e);
        }
        catch (ClassNotFoundException e)
        {
            throw new ExpressionException(axiomFile.toString() + " file wrong type or corrupt", e);
        }
        finally
        {
            if (((count <= 0) || (current == null)) && (fileInputStream != null))
            {
                close(fileInputStream);
                fileInputStream = null;
            }
        }
    }
    
    private Axiom readNextAxiom() throws IOException, ClassNotFoundException
    {
        Object marshalled = ois.readObject();
        if (marshalled == null)
            throw new ExpressionException(axiomFile.toString() + " no data");
        return (Axiom)marshalled;
    }

    /**
     * Closes input stream quietly
     * @param instream InputStream
     */
    private void close(InputStream instream) 
    {
        if (instream != null)
            try
            {
                instream.close();
            }
            catch (IOException e)
            {
            }
    }
    
}
