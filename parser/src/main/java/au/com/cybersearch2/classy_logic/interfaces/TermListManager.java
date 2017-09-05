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
package au.com.cybersearch2.classy_logic.interfaces;

import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * TermListManager
 * Interface for TermList instance factory
 * @author Andrew Bowley
 * 3May,2017
 */
public interface TermListManager
{
    QualifiedName getQualifiedName();
    String getName();
    int getTermCount();
    int addTerm(TermMetaData termMetaData);
    void checkTerm(TermMetaData termMetaData);
    int getIndexForName(String termName);
    TermMetaData getMetaData(int index);
    boolean changeName(int index, String name);
    boolean isMutable();
    boolean isAnonymousTerms();
    int getNamedTermCount();
    List<String> getTermNameList();
    void clearMutable();
    void setDuplicateTermNames(boolean isDuplicateTermNames);
    TermMetaData analyseTerm(Term term, int index);
}
