/**
 * 
 */
package au.com.cybersearch2.classy_logic.parser;

import au.com.cybersearch2.classy_logic.PersistenceWorker;
import au.com.cybersearch2.classy_logic.jpa.City;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classyjpa.persist.PersistenceContext;
import au.com.cybersearch2.classytask.Executable;

/**
 * @author andrew
 *
 */
public class CityPersistenceService extends PersistenceWorker<City> 
{
	private QueryParserTest owner;
	
	public CityPersistenceService(PersistenceContext persistenceContext, QueryParserTest owner) 
	{
		super(QueryParserTest.PU_NAME, persistenceContext);
		this.owner = owner;
	}

	@Override
	public Executable doWork(PersistenceWork persistenceWork) 
	{
		return owner.getExecutable(persistenceWork);
	}
	
}
