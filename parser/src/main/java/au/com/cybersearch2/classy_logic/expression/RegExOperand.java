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
package au.com.cybersearch2.classy_logic.expression;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import au.com.cybersearch2.classy_logic.compile.Group;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;


/**
 * RegExOperand
 * Evaluates regular expression from current value and optionally populates group operands.
 * The evaluation returns false on no match which causes unification to short circuit. 
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public class RegExOperand extends StringOperand 
{
	// In multiline mode the expressions '^' and '$' match
    // just after or just before, respectively, a line terminator or the end of
    // the input sequence. 
	protected static int REGEX_DEFAULT_FLAGS = 0; // Pattern.MULTILINE;
	/** Regular expression */
	protected String regex;
	/** Pre-compiled pattern */
	protected Pattern pattern;
	/** Optional object to assign group values on evaluation */
	protected Group group;
	protected int flags;
	protected Operand regexOp;
	
	/**
	 * Construct RegExOperand object
     * @param qname Qualified name
	 * @param regex Regular expression
	 * @param flags Optional flags to modify regular expression behavior
	 * @param group Group object or null if grouping not used
	 */
	public RegExOperand(QualifiedName qname, String regex, int flags, Group group) 
	{
		this(qname, (Operand)null, flags, group);
		this.regex = regex;
	}

	/**
     * @param qname Qualified name
	 * @param regexOp Regular expression operand
	 * @param flags Optional flags to modify regular expression behavior
	 * @param group Group object or null if grouping not used
	 */
	public RegExOperand(QualifiedName qname, Operand regexOp, int flags, Group group) 
	{
		super(qname);
		this.flags = flags;
		this.group = group;
		this.regexOp = regexOp;
		if ((regexOp != null) && !regexOp.isEmpty() && Term.ANONYMOUS.equals(regexOp.getName()))
			regex = regexOp.getValue().toString();
		else
			regex = "";
	}

	/**
	 * Evaluate value using data gathered during unification.
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
		if (regexOp != null) 
		{
			if (regexOp.isEmpty())
				regexOp.evaluate(id);
			regex = regexOp.getValue().toString();
		}
		// Note id not required as this object id is set during unification
		if (!isEmpty())
		{
			try
			{
				pattern = Pattern.compile(regex, flags | REGEX_DEFAULT_FLAGS);
			}
			catch(PatternSyntaxException e)
			{
				throw new ExpressionException("Error in regular expression", e);
			}
			// Retain value on match
			Matcher matcher = pattern.matcher(value.toString());
			boolean isMatch = matcher.find();
			if (!isMatch) // No match is same as unification failed
				clearValue();
			else if (group != null)
			{   // Assign values to group operands which are members of the same template as this term
				List<Operand> groupList = group.getGroupList();
				String[] groupValues = getGroups(matcher);
				if (groupValues.length > 0)
				{
					int index = 0;
					for (String group: groupValues)
					{   // Group(0) is assigned to this object, so may be a subset of the original text
						if (index == 0)
							assign(groupValues[0]);
						else 
						{   // Groups in regex start at group(1)
							if (index > groupList.size())
								break;
							groupList.get(index - 1).assign(group);
						}
						++index;
					}
				}
			}
			// Returning false for no match will cause evaluation short circuit
			return isMatch ? EvaluationStatus.COMPLETE : EvaluationStatus.SHORT_CIRCUIT;
		}
		return EvaluationStatus.COMPLETE;
	}


	/**
	 * Override toString() to report &lt;empty&gt;, null or value
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
	 */
	@Override
	public String toString()
	{
		if (empty)
			return "\"" + regex + "\"";
		return super.toString();
	}

	/**
	 * Returns values for group 0 and above as array
	 * @param matcher Matcher object in matched state
	 * @return String array
	 */
	public static String[] getGroups(Matcher matcher)
	{
		String[] groups = new String[matcher.groupCount() + 1];
		for (int i = 0; i < groups.length; i++)
			groups[i] = matcher.group(i);
		return groups;
	}

	/**
	 * Returns expression Operand to an operand visitor
	 * @return Operand object or null if expression not set
	 */
	@Override
	public Operand getLeftOperand() 
	{
		return regexOp;
	}

}
