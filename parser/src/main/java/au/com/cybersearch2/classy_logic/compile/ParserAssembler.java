/**
 * 
 */
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.AxiomListSource;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classyinject.DI;


/**
 * ParserAssembler
 * Collects and organizes information gathered compiling an XPL script.
 * Contained are the details gathered from parsing all statements within a specified scoped.
 * @author Andrew Bowley
 *
 * @since 15/10/2010
 */
public class ParserAssembler implements LocaleListener
{
	public static class ExternalAxiomSource
	{
		@Inject
		ProviderManager providerManager;
		public ExternalAxiomSource()
		{
            DI.inject(this); 
		}

		public AxiomProvider getAxiomProvider(String name)
		{
			return providerManager.getAxiomProvider(name);
		}
		
		public AxiomSource getAxiomSource(String name, String axiomName,
				List<String> axiomTermNameList) 
		{
	        return providerManager.getAxiomSource(name, axiomName, axiomTermNameList);
		}
	}
	
	/** Scope */
    protected Scope scope;
    /** The operands, which are terms placed in expressions */
	protected OperandMap operandMap;
	/** The axioms which are declared within the enclosing scope */ 
	protected Map<String, List<Axiom>> axiomListMap;
	/** Container for axioms under construction */
	protected Map<String, Axiom> axiomMap;
	/** Axiom term names */
	protected Map<String, List<String>> axiomTermNameMap;
	/** The templates */
	protected Map<String, Template> templateMap;
	/** The axiom listeners, all belonging to list variables */
	protected Map<String, List<AxiomListener>> axiomListenerMap;
	/** Maps qualified axiom name to resource name */
	protected Map<String, String> axiomResourceMap;
	protected List<LocaleListener> localeListenerList;
	/** Axioms which live only for current scope */
	protected Map<String, Axiom> scopeAxiomMap;

	/** Axiom provider connects to persistence back end */
	ExternalAxiomSource externalAxiomSource;
	
	/**
	 * Construct a ParserAssembler object 
	 * @param scope The name of the enclosing scope 
	 */
	public ParserAssembler(Scope scope)
	{
		this.scope = scope;
		operandMap = new OperandMap();
	    axiomListMap = new HashMap<String, List<Axiom>>();
	    axiomMap = new HashMap<String, Axiom>();
	    axiomTermNameMap = new HashMap<String, List<String>>();
	    templateMap = new HashMap<String, Template>();
	    axiomListenerMap = new HashMap<String, List<AxiomListener>>();
	    axiomResourceMap = new HashMap<String, String>();
	    localeListenerList = new ArrayList<LocaleListener>();
	}
	
	/**
	 * Construct a ParserAssembler object for the Golbal scope 
	 */
	public ParserAssembler()
	{
		this(null);
	}

	/**
	 * Returns object containing all operands and item lists
	 * @return OperandMap object
	 */
    public OperandMap getOperandMap()
    {
    	return operandMap;
    }

    public Scope getScope()
    {
    	return scope;
    }
    
    public Locale getScopeLocale()
    {
    	return scope == null ? Locale.getDefault(Category.FORMAT) : scope.getLocale();
    }
    
    /**
     * Add contents of another ParserAssembler to this object
     * @param parserAssembler Other ParserAssembler object
     */
	public void addAll(ParserAssembler parserAssembler) 
	{
		operandMap.putAll(parserAssembler.operandMap);
		axiomListMap.putAll(parserAssembler.axiomListMap);
		axiomMap.putAll(parserAssembler.axiomMap);
		axiomTermNameMap.putAll(parserAssembler.axiomTermNameMap);
		templateMap.putAll(parserAssembler.templateMap);
		axiomListenerMap.putAll(parserAssembler.getAxiomListenerMap());
		axiomResourceMap.putAll(parserAssembler.axiomResourceMap);
	}

	/**
	 * Add a new template to this ParserAssembler
	 * @param templateName
	 */
	public Template createTemplate(String templateName)
	{
		Template template = new Template(templateName);
		templateMap.put(templateName, template);
		return template;
	}

	/**
	 * Add a term to a template
	 * @param templateName
	 * @param term Operand object
	 */
	public void addTemplate(String templateName, Operand term)
	{
		Template template = templateMap.get(templateName);
		template.addTerm((Term)term);
	}

	/**
	 * Set template properties - applies only to Calculator
	 * @param templateName
	 * @param properties
	 * @see au.com.cybersearch2.classy_logic.pattern.Template#initialize()
	 */
	public void addTemplate(String templateName, Map<String, Object> properties)
	{
		Template template = templateMap.get(templateName);
		template.addProperties(properties);
	}

	/**
	 * Returns template with specified name
	 * @param name
	 * @return Template object or null if template not found
	 */
	public Template getTemplate(String name)
    {
	    return templateMap.get(name);
    }

	/**
	 * Add a new axiom to this ParserAssembler
	 * @param axiomName
	 */
	public void createAxiom(String axiomName)
	{
		List<Axiom> axiomList = new ArrayList<Axiom>();
		axiomListMap.put(axiomName, axiomList);
	}

	/**
	 * Add a term to axiom under construction
	 * @param axiomName
	 * @param term Term object
	 */
	public void addAxiom(String axiomName, Term term)
	{
		Axiom axiom = axiomMap.get(axiomName);
		if (axiom == null)
		{   // No axiom currently under construction, so create one.
			axiom = new Axiom(axiomName);
			axiomMap.put(axiomName, axiom);
		}
		// Use declared term name, if specified
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList != null)
			axiom.addTerm(term, termNameList);
		else
			axiom.addTerm(term);
	}

	public void addScopeAxiom(Axiom axiom)
	{
		if (scopeAxiomMap == null)
			scopeAxiomMap = new HashMap<String, Axiom>();
		String axiomName = axiom.getName();
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList != null)
		{	
			Axiom termNameAxiom = new Axiom(axiomName);
			int index = 0;
		    for (String termName: termNameList)
		    {
		    	if (index == axiom.getTermCount())
		    		break;
		    	termNameAxiom.addTerm(new Parameter(termName, axiom.getTermByIndex(index++).getValue()));
		    }
			scopeAxiomMap.put(axiomName, termNameAxiom);
		}
		else
			scopeAxiomMap.put(axiomName, axiom);
	}
	
	/**
	 * Add name to list of axiom term names
	 * @param axiomName
	 * @param termName
	 */
	public void addAxiomTermName(String axiomName, String termName)
	{
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList == null)
		{
			termNameList = new ArrayList<String>();
			axiomTermNameMap.put(axiomName, termNameList);
		}
		termNameList.add(termName);
	}
	
	/**
	 * Get axiom term name by position
	 * @param axiomName
	 * @param position 
	 */
	public String getAxiomTermName(String axiomName, int position)
	{
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList == null)
		    return null;
		return termNameList.get(position);
	}
	
	/**
	 * Returns list of axiom term names
	 * @param axiomName
	 */
	public List<String> getAxiomTermNameList(String axiomName)
	{
		return axiomTermNameMap.get(axiomName);
	}
	
	/**
	 * Transfer axiom under construction to the list of axioms with same name
	 * @param axiomName
	 */
	public Axiom saveAxiom(String axiomName)
	{
		Axiom axiom = axiomMap.get(axiomName);
		List<Axiom> axiomList = axiomListMap.get(axiomName);
		axiomList.add(axiom);
		axiomMap.remove(axiomName);
		return axiom;
	}

	/**
	 * Set resource properties for axiom and remove internal reference
	 * @param resourceName Resource name
	 * @param axiomName Name of axiom to which properties apply
	 * @param properties Properties specific to the resource. May be empty.
	 */
	public AxiomProvider setResourceProperties(String resourceName, String axiomName, Map<String, Object> properties)
	{
		// Note resource references axiom by qualified name, which prepends scope name
		String qualifiedAxiomName = getQualifiedName(axiomName);
		AxiomProvider axiomProvider = getAxiomProvider(resourceName);
		axiomProvider.setResourceProperties(qualifiedAxiomName, properties);
		// Preserve mapping of qualified axiom name to resource name
		axiomResourceMap.put(qualifiedAxiomName, resourceName);
		// Remove entry from axiomListMap so the axiom is not regarded as internal
		axiomListMap.remove(axiomName);
		return axiomProvider;
	}

	/**
	 * Remove internal reference for axiom to be supplied as a parameter
	 * @param axiomName Name of axiom to which properties apply
	 */
	public void setParameter( String axiomName)
	{
		// Remove entry from axiomListMap so the axiom is not regarded as internal
		axiomListMap.remove(axiomName);
	}

	/**
	 * Returns axiom source for specified axiom name
	 * @param axiomName
	 * @return AxiomSource object
	 */
    public AxiomSource getAxiomSource(String axiomName)
    {
    	if (scopeAxiomMap != null)
    	{
    		Axiom axiom = scopeAxiomMap.get(axiomName);
    		if (axiom != null)
    			return new SingleAxiomSource(axiom);
    	}
       	List<Axiom> axiomList = axiomListMap.get(axiomName);
    	if (axiomList != null)
    		return new AxiomListSource(axiomList);
    	String qualifiedName = getQualifiedName(axiomName);
    	String resourceName = axiomResourceMap.get(qualifiedName);
    	if (resourceName == null)
    		return null;
    	List<String> axiomTermNameList = axiomTermNameMap.get(qualifiedName);
    	if (axiomTermNameList == null)
    	    axiomTermNameList = Collections.emptyList();
     	return getAxiomProvider(resourceName).getAxiomSource( qualifiedName, axiomTermNameList); 
    }

    /**
     * Returns object containing all axiom listeners belonging to this scope
     * @return  Unmodifiable AxiomListener map object
     */
	public Map<String, List<AxiomListener>> getAxiomListenerMap()
	{
		return Collections.unmodifiableMap(axiomListenerMap);
	}

	/**
	 * Register axiom term list by adding it's axiom listener to this ParserAssembler object
	 * @param axiomTermList The term list
	 */
	public void registerAxiomTermList(AxiomTermList axiomTermList)
	{
		AxiomListener axiomListener = axiomTermList.getAxiomListener();
		String key = axiomTermList.getKey();
		List<AxiomListener> axiomListenerList = getAxiomListenerList(key);
		axiomListenerList.add(axiomListener);
		axiomTermList.setAxiomTermNameList(axiomTermNameMap.get(key));
	}

	/**
	 * Register axiom term list for local axiom
	 * @param axiomTermList The term list
	 */
	public void registerLocalList(AxiomTermList axiomTermList)
	{
		String key = axiomTermList.getKey();
		axiomTermList.setAxiomTermNameList(axiomTermNameMap.get(axiomTermList.getKey()));
		scope.addLocalAxiomListener(key, axiomTermList.getAxiomListener());
	}

	/**
	 * Register locale listener to be notified when the scope changes
	 * @param localeListener LocaleListener object
	 */
	public void registerLocaleListener(LocaleListener localeListener)
	{
		localeListenerList.add(localeListener);
	}

	/**
	 * Notify all locale listeners that the scope has changed
	 * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
	 */
	@Override
	public void onScopeChange(Scope scope) 
	{
		if (scopeAxiomMap != null)
			scopeAxiomMap.clear();
		for (LocaleListener localeListener: localeListenerList)
			localeListener.onScopeChange(scope);
	}

	/**
	 * Returns list of axiom listeners for specified key
	 * @param key
	 * @return List containing AxiomListener objects 
	 */
	protected List<AxiomListener> getAxiomListenerList(String key)
	{
		List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
		if (axiomListenerList == null)
		{
			axiomListenerList = new ArrayList<AxiomListener>();
			axiomListenerMap.put(key, axiomListenerList);
		}
		return axiomListenerList;
	}
	
	/**
	 * Register axiom list by adding it's axiom listener to this ParserAssembler object
	 * @param axiomList The axiom list
	 */
	public void registerAxiomList(AxiomList axiomList) 
	{
		AxiomListener axiomListener = axiomList.getAxiomListener();
		String key = axiomList.getKey();
		List<AxiomListener> axiomListenerList = getAxiomListenerList(key);
		axiomListenerList.add(axiomListener);
		List<String> axiomTermNameList = axiomTermNameMap.get(key);
		if (axiomTermNameList == null)
		{
			Template template = templateMap.get(key);
			if (template != null)
			{
				axiomTermNameList = new ArrayList<String>();
				for (int i = 0; i < template.getTermCount(); i++)
				{
					Term term = template.getTermByIndex(i);
					if (term.getName().isEmpty())
						break;
					axiomTermNameList.add(term.getName());
				}
				if (axiomTermNameList.size() == 0)
					axiomTermNameList = null;
			}
		}
		axiomList.setAxiomTermNameList(axiomTermNameList);
	}

	/**
	 * Register axiom list by adding it's axiom listener to this ParserAssembler object
	 * @param axiomKey The name of the axioms inserted into the list
	 * @param axiomListener The axiom listener
	 */
	public void registerAxiomListener(String axiomKey, AxiomListener axiomListener) 
	{
		List<AxiomListener> axiomListenerList = getAxiomListenerList(axiomKey);
		axiomListenerList.add(axiomListener);
	}

    /**
     * Add all axioms in a collection to this object.
     * Intended only for use in testing.
     * @param keyList List of axiom names
     * @param axiomCollection The axiom collection
     */
    public void addAxiomCollection(List<String> keyList, AxiomCollection axiomCollection)
    {
    	for (String key: keyList)
    	{
    		AxiomSource axiomSource = axiomCollection.getAxiomSource(key);
    		Iterator<Axiom> iterator = axiomSource.iterator();
    		Axiom axiom = null;
    		List<Axiom> axiomList = new ArrayList<Axiom>();
    		while (iterator.hasNext())
    		{
    			axiom = iterator.next();
    			axiomList.add(axiom);
    		}
    		axiomListMap.put(key, axiomList);
    		if ((axiom != null) && 
    			(axiom.getTermCount() > 0) && 
    			(!Term.ANONYMOUS.equals(axiom.getTermByIndex(0).getName())))
    		{
    			List<String> termNameList = new ArrayList<String>();
    			for (int i = 0; i < axiom.getTermCount(); ++i)
    			{
    				String name = axiom.getTermByIndex(i).getName();
    				if (Term.ANONYMOUS.equals(name))
    					break;
    				termNameList.add(name);
    			}
    			axiomTermNameMap.put(key, termNameList);
    		}
    		
    	}
    }

    /**
     * Add templates to this object.
     * Intended only for use in testing.
     * @param templateList List of templates
     */
    public void addTemplateList(List<Template> templateList)
    {
    	for (Template template: templateList)
    		templateMap.put(template.getName(), template);
    }
    
	/**
	 * Returns the given name prepended with the scope name and a dot 
	 * unless this ParserAssembler is enclosing the Global scope.
	 * Required for axiomProvider which is possibly shared by more than one scope.
	 * @param name Simple name
	 * @return Qualified name
	 */
	protected String getQualifiedName(String name)
	{
		return QueryProgram.GLOBAL_SCOPE.equals(scope.getName()) ? name : (scope.getName() + "." + name);
	}

	/**
	 * Create new template and add to head template chain
	 * @param templateName Name of head template
	 * @param chainName Name of template to add to chain
	 */
	public void chainTemplate(String templateName, String chainName) 
	{
		Template template = getTemplate(templateName);
		while (template.getNext() != null)
			template = template.getNext();
		Template chainTemplate = new Template(chainName);
		template.setNext(chainTemplate);
		templateMap.put(chainName, chainTemplate);
	}

	/**
	 * Returns axiom provider specified by resource name
	 * @param resourceName
	 * @return AxiomProvider object
	 */
	protected AxiomProvider getAxiomProvider(String resourceName) 
	{
    	if (externalAxiomSource == null)
    		externalAxiomSource = new ExternalAxiomSource();
		return externalAxiomSource.getAxiomProvider(resourceName);
	}

}
