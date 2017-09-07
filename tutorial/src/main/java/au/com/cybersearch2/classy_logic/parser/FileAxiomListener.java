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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import com.thoughtworks.xstream.XStream;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * FileAxiomListener
 * @author Andrew Bowley
 * 8Jan.,2017
 */
public class FileAxiomListener implements AxiomListener
{
    int count;
    String name;
    File axiomFile;
    FileOutputStream fos;
    ObjectOutputStream oos;
    
    public FileAxiomListener(String name, File axiomFile)
    {
        this.name = name;
        this.axiomFile = axiomFile;
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomListener#onNextAxiom(au.com.cybersearch2.classy_logic.pattern.Axiom)
     */
    @Override
    public void onNextAxiom(QualifiedName qname, Axiom axiom)
    {
        try
        {
            if (oos == null)
                try
                {
                     openFile();
                }
                catch(IOException e)
                {
                    throw new ExpressionException(axiomFile.toString() + " error opening file", e);
                }
            oos.writeObject(axiom);
            oos.flush();
            ++count;
        }
        catch (IOException e)
        {
            throw new ExpressionException(axiomFile.toString() + " error", e);
        }
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

    private void openFile() throws IOException
    {
        fos = new FileOutputStream(axiomFile);
        oos = new ObjectOutputStream(fos);
    }
    
    /**
     * Closes input stream quietly
     * @param instream InputStream
     */
    private void close() 
    {
        if (oos != null)
        {
            try
            {
                oos.close();
                if (count > 0)
                {
                    writeHeader();
                    count = 0;
                }
                oos = null;
            }
            catch (IOException e)
            {
            }
        }
    }

    private void writeHeader()
    {
        AxiomHeader axiomHeader = new AxiomHeader();
        axiomHeader.setName(name);
        axiomHeader.setCreated(new Date());
        axiomHeader.setUser(System.getProperty("user.name"));
        axiomHeader.setCount(count);
        XStream xStream = new XStream();
        xStream.alias("axiomHeader", AxiomHeader.class);
        File headerFile = new File(axiomFile.getAbsolutePath() + ".xml");
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(headerFile);
            xStream.toXML(axiomHeader, writer);
        }
        catch (FileNotFoundException e)
        {
            throw new ExpressionException(axiomFile.toString() + " error writing axiom header", e);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
        
    }
}
