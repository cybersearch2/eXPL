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

import java.util.Collections;
import java.util.Map;

/**
 * Result
 * @author Andrew Bowley
 * 3 Mar 2015
 */
public class Result 
{

	protected Map<String, Iterable<?>> listMap;
	static Map<String, Iterable<?>> EMPTY_LIST_MAP;

	static
	{
		EMPTY_LIST_MAP = Collections.emptyMap();
	}
	
	/**
	 * 
	 */
	public Result(Map<String, Iterable<?>> listMap) 
	{
		this.listMap = listMap == null ? EMPTY_LIST_MAP : listMap;
	}

	/**
	 * @return the list
	 */
	public Iterable<?> getList(String key) 
	{
		Iterable<?> list = listMap.get(key);
		if (list == null)
			return Collections.emptyList();
		return list;
	}

}
