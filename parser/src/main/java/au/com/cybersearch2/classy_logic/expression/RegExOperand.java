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
import au.com.cybersearch2.classy_logic.terms.Parameter;


/**
 * RegExOperand
 * Evaluates regular expression from current value and optionally populates group operands.
 * The evaluation returns false on no match which causes unification to short circuit. 
 * @author Andrew Bowley
 * 20 Dec 2014
 */
public class RegExOperand extends BooleanOperand 
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
	/** Optional flags to modify regular expression behavior */
	protected int flags;
	/** Regular expression operand */
	protected Operand regexOp;
	/** Text to match on */
    protected String input;
	
	/**
	 * Construct RegExOperand object
     * @param qname Qualified name
	 * @param regex Regular expression
	 * @param inputOp Input operand (optional)
	 * @param flags Optional flags to modify regular expression behavior
	 * @param group Group object or null if grouping not used
	 */
	public RegExOperand(QualifiedName qname, String regex, Operand inputOp, int flags, Group group) 
	{
		this(qname, (Operand)null, inputOp, flags, group);
		this.regex = regex.replace("\\\\", "\\");
	}

	/**
     * @param qname Qualified name
	 * @param regexOp Regular expression operand
	 * @param flags Optional flags to modify regular expression behavior
	 * @param group Group object or null if grouping not used
	 */
	public RegExOperand(QualifiedName qname, Operand regexOp, Operand inputOp, int flags, Group group) 
	{
		super(qname, inputOp);
		this.flags = flags;
		this.group = group;
		this.regexOp = regexOp;
		if ((regexOp != null) && !regexOp.isEmpty())
			regex = regexOp.getValue().toString();
		else
			regex = "";
	}

   /**
     * Delegate to perform actual unification with other Term. If successful, two terms will be equivalent. 
     * @param otherTerm Term with which to unify
     * @param id Identity of caller, which must be provided for backup()
     * @return Identity passed in param "id" or zero if unification failed
     * @see #backup(int id)
     */
    @Override
    public int unify(Term otherTerm, int id)
    {
        this.id = id;
        input = otherTerm.getValue().toString();
        return this.id;
    }

	/**
	 * Evaluate value using data gathered during unification.
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
	    this.id = id;
        if (expression != null)
        {
            EvaluationStatus status = expression.evaluate(id);
            if (status != EvaluationStatus.COMPLETE)
                return status;
            if (expression.isEmpty())
                input = "";
            else
                input = expression.getValue().toString();
        }
		if (regexOp != null) 
		{
			if (regexOp.isEmpty())
				regexOp.evaluate(id);
			regex = regexOp.getValue().toString().replace("\\\\", "\\");
		}
		// Note id not required as this object id is set during unification
        boolean isMatch = false;
		if (input != null)
		{
		    Matcher matcher = null;
		    if (regex.isEmpty())
		        isMatch = input.isEmpty();
		    else
		    {
		        // Retain value on match
		        matcher = getMatcher();
			    isMatch = matcher.find();
		    }
			if (isMatch && (group != null) && (matcher != null))
			{   // Assign values to group operands which are members of the same template as this term
				List<Operand> groupList = group.getGroupList();
				String[] groupValues = getGroups(matcher);
				if (groupValues.length > 0)
				{
					int index = -1;
					for (String group: groupValues)
					{   
                        ++index;
					    if (group == null)
					        continue;
					    // Group(0) is assigned to this object, so may be a subset of the original text
						if (index == 0)
							setValue(groupValues[0]);
						else 
						{   // Groups in regex start at group(1)
							if (index > groupList.size())
								break;
							Parameter param = new Parameter(Term.ANONYMOUS, group);
							param.setId(id);
							groupList.get(index - 1).assign(param);
						}
					}
				}
			}
		}
		setValue(isMatch);
		return EvaluationStatus.COMPLETE;
	}

   /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int id)
    {
        input = null;
        if (regexOp != null)
            regexOp.backup(id);
        return super.backup(id);
    }

	/**
	 * Override toString() to report &lt;empty&gt;, null or value
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
	 */
	@Override
	public String toString()
	{
	    if (regex.isEmpty() && (regexOp != null) && !regexOp.isEmpty())
            regex = regexOp.getValue().toString();
		if (empty || regex.isEmpty())
		    return getName() + " \\" + regex + "\\";
		String text = super.toString();
        if (!getMatcher().find())
            text += ": false";
        return text;
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
	public Operand getRightOperand() 
	{
		return regexOp;
	}

	protected Matcher getMatcher()
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
        return pattern.matcher(input);
	}
}
