package au.com.cybersearch2.classy_logic.interfaces;

/**
 * Collection of axiom sources
 * AxiomCollection
 * @author Andrew Bowley
 * 16 Feb 2015
 */
public interface AxiomCollection 
{
    /**
     * Returns axiom source referenced by name
     * @param name
     * @return AxiomSource object
     */
	AxiomSource getAxiomSource(String name);
	
	/**
	 * Returns flag set true if no axiom sources in this collection
	 * @return boolean
	 */
    boolean isEmpty();
}