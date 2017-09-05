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
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.SourceInfo;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.Token;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * ParserContext
 * Aggregates variables required while parsing 
 * @author Andrew Bowley
 * 6Apr.,2017
 */
public class ParserContext
{
    static class MarkerPair
    {
        public SourceMarker sourceMarker;
        public Token itemToken;
        
        public MarkerPair(SourceMarker sourceMarker, Token itemToken)
        {
            this.sourceMarker = sourceMarker;
            this.itemToken = itemToken;
        }
    }
    
    QueryProgram queryProgram;
    /** Current source marker */
    SourceMarker sourceMarker;
    /** Current scope */
    Scope scope;
    /** Current parserAssembler */
    ParserAssembler parserAssembler;
    /** Current operand map */
    OperandMap operandMap;
    /** List of source documents */
    List<String> sourceDocumentList;
    /** Index of current source document in list */
    int sourceDocumentId;
    /** Stack to nest calls */
    Deque<MarkerPair> markerStack;
    /** Stack to retain nested source documents */
    Deque<Integer> documentStack;
    /** Set of source markers collated by qualified name */
    Set<SourceMarker> sourceMarkerSet;
    /** Flag set true if waiting for source item start token */
    boolean isSourceItemPending;
    /** Source item start token */
    Token itemToken;
    /** Store for qualified names which employ reference counting eg. lists */
    Map<QualifiedName, QualifiedName> qnameMap; 
    /** Name of current outer template - provided to allow ad hoc inner template creation */
    QualifiedName outerTemplate;

    /**
     * Contruct ParserContext with empty source document path
     * @param queryProgram Main query object
     */
    public ParserContext(QueryProgram queryProgram)
    {
        this.queryProgram = queryProgram;
        sourceMarkerSet = new TreeSet<SourceMarker>();
        markerStack = new ArrayDeque<MarkerPair>();
        documentStack = new ArrayDeque<Integer>();
        qnameMap = new HashMap<QualifiedName, QualifiedName>();
        resetScope();
    }

    /**
     * Contruct ParserContext
     * @param queryProgram Main query object
     * @param sourceDocument Source document path
     */
    public ParserContext(QueryProgram queryProgram, String sourceDocument)
    {
        this(queryProgram);
        sourceDocumentList = new ArrayList<String>();
        sourceDocumentList.add(sourceDocument);
        documentStack.push(0);
    }

    /**
     * Set current scope - call {@link #resetScope()}resetScope on exit from this scope
     * @param scope
     */
    public void setScope(Scope scope)
    {
        this.scope = scope;
        parserAssembler = scope.getParserAssembler();
        operandMap = parserAssembler.getOperandMap();
    }

    /**
     * Switch to global scope
     */
    public void resetScope()
    {
        setScope(queryProgram.getGlobalScope());
    }

    /**
     * Returns current scope
     * @return Scope object
     */
    public Scope getScope()
    {
        return scope;
    }
    
    /**
     * @return the sourceMarker
     */
    public SourceMarker getSourceMarker()
    {
        return sourceMarker;
    }

    /**
     * @return the outerTemplate
     */
    public QualifiedName getTemplateName()
    {
        if (outerTemplate == null)
        {
            outerTemplate = new QualifiedTemplateName(scope.getAlias(), "scope");
            // Do not set context name for scope template
            parserAssembler.getTemplateAssembler().createTemplate(outerTemplate, TemplateType.calculator); 
        }
        return outerTemplate;
    }

    /**
     * @param outerTemplate the outerTemplate to set
     */
    public void setTemplateName(QualifiedName outerTemplate)
    {
        this.outerTemplate = outerTemplate;
    }

    /**
     * Create source marker for item identified by name
     * @param token Parser token for start of construct
     * @param name Identifier in text format
     */
    public void setSourceMarker(Token token, String name)
    {
        SourceMarker sourceMarker = new SourceMarker(token);
        // Convert name to qualified name using current context
        sourceMarker.setQualifiedName(QualifiedName.parseName(name, getContextName()));
        setSourceMarker(sourceMarker);
        // Innitialize item token for cane of single item starting at source marker
        itemToken = token;
    }
    
    /**
     * Create source marker for item identified by qualified name
     * @param token Parser token for start of construct
     * @param name Identifier as qualified name
     */
    public void setSourceMarker(Token token, QualifiedName qname)
    {
        SourceMarker sourceMarker = new SourceMarker(token);
        sourceMarker.setQualifiedName(qname);
        setSourceMarker(sourceMarker);
        // Innitialize item token for case of single item starting at source marker
        itemToken = token;
    }

    /**
     * Push current source marker on a stack
     */
    public void pushSourceMarker()
    {
        markerStack.push(new MarkerPair(sourceMarker, itemToken));
    }

    /**
     * Pop source marker off stack
     */
    public void popSourceMarker()
    {
        MarkerPair markerPair = markerStack.pop();
        sourceMarker = markerPair.sourceMarker;
        itemToken = markerPair.itemToken;
    }

    /**
     * Checks that current source marker has at least one item with a short circuit.
     * @throws ParseException 
     */
    public void checkForShortCircuit() throws ParseException
    {
        SourceItem sourceItem = sourceMarker.getHeadSourceItem();
        while (sourceItem != null)
        {
            if (sourceItem.getInformation().startsWith("?") || sourceItem.getInformation().startsWith(":"))
                return;
            sourceItem = sourceItem.getNext();
        }
        throw new ParseException(sourceMarker.getLiteral().toString() + " " + sourceMarker.getQualifiedName().toString() + " has loop with no short circuit");
    }
    /**
     * Possible source item start token encountered
     * @param token Parser token
     */
    public void onTokenIntercept(Token token)
    {
        if (isSourceItemPending)
        {
            itemToken = token;
            isSourceItemPending = false;
        }
    }

    /**
     * Add SourceItem object for SourceInfo interface to current source marker 
     * @param operand Operand object
     * @return SourceItem object
     */
    public SourceItem addSourceItem(SourceInfo sourceInfo)
    {
        SourceItem sourceItem = addSourceItem(sourceInfo.toString());
        // Update information when operand is completed in a parser task
        sourceInfo.setSourceItem(sourceItem);
        return sourceItem;
    }
    
    /**
     * Add SourceItem object for operand to current source marker 
     * @param operand Operand object
     * @return SourceItem object
     */
    public SourceItem addSourceItem(Operand operand)
    {
        SourceItem sourceItem = addSourceItem(operand.toString());
        // Update information when operand is completed in a parser task
        if (operand instanceof SourceInfo)
            ((SourceInfo)operand).setSourceItem(sourceItem);
        return sourceItem;
    }
    
    /**
     * Add SourceItem object to current source marker
     * @param information
     * @return SourceItem object
     */
    public SourceItem addSourceItem(String information)
    {
        SourceItem sourceItem = new SourceItem(itemToken, information);
        if (sourceMarker != null)
            sourceMarker.addSourceItem(sourceItem);
        if (itemToken.next != null)
            // Advance to next token if available
            itemToken = itemToken.next;
        isSourceItemPending = true;
        return sourceItem;
    }

    /**
     * Push source document on stack
     * @param sourceDocument Source document resource name
     * @return id of source document
     */
    public int pushSourceDocument(String sourceDocument)
    {
        if (sourceDocumentList == null)
        {   // Opening source document not specified, so set to empty string
            sourceDocumentList = new ArrayList<String>();
            sourceDocumentList.add("");
            documentStack.push(0);
        }
        sourceDocumentId = sourceDocumentList.size();
        documentStack.push(sourceDocumentId);
        sourceDocumentList.add(sourceDocument);
        return sourceDocumentId;
    }
 
    /**
     * Pop source document stack to reference previous document
     */
    public void popSourceDocument()
    {
        if (documentStack.size() > 0)
        {
            documentStack.removeFirst();
            sourceDocumentId = documentStack.getFirst();
        }
    }
    
    /**
     * @return the parserAssembler
     */
    public ParserAssembler getParserAssembler()
    {
        return parserAssembler;
    }

    /**
     * Returns current operand map
     * @return OperandMap object
     */
    public OperandMap getOperandMap()
    {
        return operandMap;
    }

    /**
     * Returns qualified name of current context
     * @return QualifiedName object
     */
    public QualifiedName getContextName()
    {
        return parserAssembler.getQualifiedContextname();
    }

    /**
     * Set current context name
     * @param qualifiedName
     */
    public void setContextName(QualifiedName qualifiedName)
    {
        parserAssembler.setQualifiedContextname(qualifiedName);
    }
    
    /**
     * @param parserAssembler the parserAssembler to set
     */
    public void setParserAssembler(ParserAssembler parserAssembler)
    {
        this.parserAssembler = parserAssembler;
    }

    /**
     * @return the queryProgram
     */
    public QueryProgram getQueryProgram()
    {
        return queryProgram;
    }

    /**
     * @return the sourceDocumentList
     */
    public List<String> getSourceDocumentList()
    {
        return sourceDocumentList;
    }

    /**
     * @return the sourceDocumentId
     */
    public int getSourceDocumentId()
    {
        return sourceDocumentId;
    }

    /**
     * @return the sourceMarkerSet
     */
    public Set<SourceMarker> getSourceMarkerSet()
    {
        return sourceMarkerSet;
    }

    /**
     * @param isSourceItemPending the isSourceItemPending to set
     */
    public void setSourceItemPending(boolean isSourceItemPending)
    {
        this.isSourceItemPending = isSourceItemPending;
    }

    /**
     * @return the isSourceItemPending
     */
    public boolean isSourceItemPending()
    {
        return isSourceItemPending;
    }

    /**
     * @return the itemToken
     */
    public Token getItemToken()
    {
        return itemToken;
    }

    /**
     * Sets template qualified name to associate with self as identifier
     * @param template Template with enclosing name space
     * @param name Name to use with identifier
     * @return QualifiedName object
     */
    public QualifiedName setQualifiedName(String template, String name)
    {
        QualifiedName qname = new QualifiedName(QualifiedName.EMPTY, template , name);
        qnameMap.put(qname, qname);
        return qname;
    }
    
    /**
     * Returns qualified name stored in context for given identifier
     * @param name Identifier in text format
     * @return QualifiedName object
     */
    public QualifiedName getQualifiedName(String name)  
    {
        QualifiedName qname = null;
        QualifiedName contextName = getContextName();
        if ((name.indexOf(".") == -1) && !contextName.getTemplate().isEmpty())
        {
            qname = new QualifiedName(QualifiedName.EMPTY, contextName.getTemplate() , name);
            if (qnameMap.containsKey(qname))
                return (qnameMap.get(qname));
        }
        qname = QualifiedName.parseName(name);
        if (qnameMap.containsKey(qname))
            return (qnameMap.get(qname));
        if (qname.isGlobalName() && (!scope.getName().equals(QueryProgram.GLOBAL_SCOPE)))
            qname.setScope(scope.getAlias());
        if (qnameMap.containsKey(qname))
            return (qnameMap.get(qname));
        qnameMap.put(qname, qname);
        return qname;
    }

    /**
     * Add source item for calculator query
     * @param qname
     * @param parameterTemplate
     * @return
     */
    public SourceItem addCalcQuery(QualifiedName qname, Template parameterTemplate)
    {
        StringBuilder builder = new StringBuilder("<- ");
        builder.append(qname.toString()).append('(');
        if (parameterTemplate != null)
        {
            int count = parameterTemplate.getTermCount();
            if ((parameterTemplate != null) && (count > 0))
            {
                builder.append(parameterTemplate.getTermByIndex(0));
                if (count > 1)
                    builder.append(" ... ").append(parameterTemplate.getTermByIndex(count - 1));
            }
        }
        builder.append(')');
        return addSourceItem(builder.toString());
    }
    
    /**
     * Set current source marker, set it's source document id and add to marker set
     * @param sourceMarker the sourceMarker to set
     */
    protected void setSourceMarker(SourceMarker sourceMarker)
    {
        this.sourceMarker = sourceMarker;
        sourceMarker.setSourceDocumentId(sourceDocumentId);
        sourceMarkerSet.add(sourceMarker);
        isSourceItemPending = true;
    }
}
