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
package au.com.cybersearch2.classy_logic.parser;

/**
 * MyToken
 * @author Andrew Bowley
 * 6Jul.,2017
 */
public class MyToken extends Token
{
    /**
     * The version identifier for this Serializable class.
     * Increment only if the <i>serialized</i> form of the
     * class changes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new token for the specified Image and Kind.
     */
    public MyToken(int kind, String image)
    {
       this.kind = kind;
       this.image = image;
    }

    int realKind = QueryParserConstants.GT;

    /**
     * Returns a new Token object.
    */

    public static final Token newToken(int ofKind, String tokenImage)
    {
      return new MyToken(ofKind, tokenImage);
    }
}
