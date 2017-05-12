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
package au.com.cybersearch2.classy_logic.helper;

import au.com.cybersearch2.classy_logic.QueryProgram;

/**
 * QualifiedTemplateName
 * @author Andrew Bowley
 * 11Jan.,2017
 */
public class QualifiedTemplateName extends QualifiedName
{

    private static final long serialVersionUID = 3634005314122046014L;

    public QualifiedTemplateName(String scope, String name)
    {
        super(QueryProgram.GLOBAL_SCOPE.equals(scope) ? EMPTY : scope, name, EMPTY);
    }

    public static String toString(String scope, String template)
    {
        StringBuilder builder = new StringBuilder();
        if (!scope.isEmpty())
            builder.append(scope).append('.');
        if (!template.isEmpty())
        {
            builder.append(template);
        }
        return builder.toString();
    }
    
}
