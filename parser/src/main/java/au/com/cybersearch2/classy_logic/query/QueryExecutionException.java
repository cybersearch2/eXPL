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
package au.com.cybersearch2.classy_logic.query;

/**
 * QueryExecutionException
 * @author Andrew Bowley
 * 30 Dec 2014
 */
public class QueryExecutionException extends RuntimeException 
{

	private static final long serialVersionUID = 1L;

	/**
	 * Construct QueryExecutionException object
	 * @param message
	 */
	public QueryExecutionException(String message) 
	{
		super(message);

	}

	/**
	 * Construct QueryExecutionException object
	 * @param message
	 * @param cause Throwable object
	 */
	public QueryExecutionException(String message, Throwable cause) 
	{
		super(message, cause);

	}

}
