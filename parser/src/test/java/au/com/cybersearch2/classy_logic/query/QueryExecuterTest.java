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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import dagger.Module;
import dagger.Provides;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.TestBooleanOperand;
import au.com.cybersearch2.classy_logic.expression.TestEvaluator;
import au.com.cybersearch2.classy_logic.expression.TestIntegerOperand;
import au.com.cybersearch2.classy_logic.expression.TestStringOperand;
import au.com.cybersearch2.classy_logic.expression.TestVariable;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.parser.QueryParserTest;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;

/**
 * QueryExecuterTest
 * @author Andrew Bowley
 * 30 Dec 2014
 */
public class QueryExecuterTest 
{
	@Module(injects = ParserAssembler.ExternalAxiomSource.class)
	static class QueryExecuterModule implements ApplicationModule
	{
	    @Provides @Singleton ProviderManager provideProviderManager()
	    {
	    	return new ProviderManager();
	    }
	}
	
	class MultQueryTracer
	{
		int i;
		
		void trace(Axiom axiom)
		{
			System.out.println(axiom.toString());
			assertThat(axiom.toString()).isEqualTo(MULTI_QUERY_TRACE[i++]);
		}
	}
	
	List<Axiom> cityList;
	private List<Axiom> charges;
	private List<Axiom> customers;
	private List<Axiom> fees;
	private List<Axiom> freights;
	
	String[] CITY_NAME_HEIGHT =
	{
		"city(name = bilene, altitude = 1718)",
		"city(name = addis ababa, altitude = 8000)",
		"city(name = denver, altitude = 5280)",
		"city(name = flagstaff, altitude = 6970)",
		"city(name = jacksonville, altitude = 8)",
		"city(name = leadville, altitude = 10200)",
		"city(name = madrid, altitude = 1305)",
		"city(name = richmond, altitude = 19)",
		"city(name = spokane, altitude = 1909)",
		"city(name = wichita, altitude = 1305)"
	};

	String[] CITY_AXIOMS =
	{
		"city(bilene, 1718)",
		"city(addis ababa, 8000)",
		"city(denver, 5280)",
		"city(flagstaff, 6970)",
		"city(jacksonville, 8)",
		"city(leadville, 10200)",
		"city(madrid, 1305)",
		"city(richmond, 19)",
		"city(spokane, 1909)",
		"city(wichita, 1305)"
	};
	
	String[] GREEK_BUSINESS =
	{
		"charge(city = Athens, charge = 23), customer(name = Acropolis Construction, city = Athens)",
		"charge(city = Sparta, charge = 13), customer(name = Marathon Marble, city = Sparta)",
		"charge(city = Sparta, charge = 13), customer(name = Agora Imports, city = Sparta)",
		"charge(city = Milos, charge = 17), customer(name = Spiros Theodolites, city = Milos)"	
	};

	public static final String GREEK_CONSTRUCTION =
		"axiom charge : \n" +
		"  (\"Athens\", 23 ),\n" +
		"  (\"Sparta\", 13 ),\n" +
		"  (\"Milos\", 17);\n" +
		"axiom customer :\n" +
		"  (\"Marathon Marble\", \"Sparta\"),\n" +
		"  (\"Acropolis Construction\", \"Athens\"),\n" +
		"  (\"Agora Imports\", \"Sparta\"),\n" +
		"  (\"Spiros Theodolites\", \"Milos\");\n" +
		"axiom fee (name, fee):\n" +
		"  (\"Marathon Marble\", 61),\n" +
		"  (\"Acropolis Construction\", 47),\n" +
		"  (\"Agora Imports\", 49),\n" +
		"  (\"Spiros Theodolites\", 57);\n" + 
		"axiom freight (city, freight): \n" +
		"  (\"Athens\", 5 ),\n" +
		"  (\"Sparta\", 16 ),\n" +
		"  (\"Milos\", 22);\n";

	static final String[] FEE_AND_FREIGHT =
	{
		"account(name = Acropolis Construction, fee = 47)",
		"delivery(city = Athens, freight = 5)",
		"account(name = Marathon Marble, fee = 61)",
		"delivery(city = Sparta, freight = 16)",
		"account(name = Agora Imports, fee = 49)",
		"delivery(city = Sparta, freight = 16)",
		"account(name = Spiros Theodolites, fee = 57)",
		"delivery(city = Milos, freight = 22)"
	};
	
	static final String[] SPARTA_FEE_AND_FREIGHT =
	{
		"account(name = Marathon Marble, fee = 61)",
		"delivery(city = Sparta, freight = 16)",
		"account(name = Agora Imports, fee = 49)",
		"delivery(city = Sparta, freight = 16)",
	};
	
    static final String[] MULTI_QUERY_TRACE =
	{
	
    	"charge(Athens, 23)",
    	"customer(Marathon Marble, Sparta)",
    	"customer(Acropolis Construction, Athens)",
    	"fee(name = Marathon Marble, fee = 61)",
    	"fee(name = Acropolis Construction, fee = 47)",
    	"freight(city = Athens, freight = 5)",
    	"customer(Agora Imports, Sparta)",
    	"customer(Spiros Theodolites, Milos)",
    	"charge(Sparta, 13)",
    	"customer(Marathon Marble, Sparta)",
    	"fee(name = Marathon Marble, fee = 61)",
    	"freight(city = Athens, freight = 5)",
    	"freight(city = Sparta, freight = 16)",
    	"sparta_only(charge.city = Sparta)",
    	"customer(Acropolis Construction, Athens)",
    	"customer(Agora Imports, Sparta)",
    	"fee(name = Marathon Marble, fee = 61)",
    	"fee(name = Acropolis Construction, fee = 47)",
    	"fee(name = Agora Imports, fee = 49)",
    	"freight(city = Athens, freight = 5)",
    	"freight(city = Sparta, freight = 16)",
    	"sparta_only(charge.city = Sparta)",
    	"customer(Spiros Theodolites, Milos)",
    	"charge(Milos, 17)",
    	"customer(Marathon Marble, Sparta)",
    	"customer(Acropolis Construction, Athens)",
    	"customer(Agora Imports, Sparta)",
    	"customer(Spiros Theodolites, Milos)",
    	"fee(name = Marathon Marble, fee = 61)",
    	"fee(name = Acropolis Construction, fee = 47)",
    	"fee(name = Agora Imports, fee = 49)",
    	"fee(name = Spiros Theodolites, fee = 57)",
    	"freight(city = Athens, freight = 5)",
    	"freight(city = Sparta, freight = 16)",
    	"freight(city = Milos, freight = 22)"
	};
	
	@Before
	public void before() throws Exception
	{
		cityList = new ArrayList<Axiom>();
		cityList.add(new Axiom("city", "bilene", 1718));
		cityList.add(new Axiom("city", "addis ababa", 8000));
		cityList.add(new Axiom("city", "denver", 5280));
		cityList.add(new Axiom("city", "flagstaff", 6970));
		cityList.add(new Axiom("city", "jacksonville", 8));
		cityList.add(new Axiom("city", "leadville", 10200));
		cityList.add(new Axiom("city", "madrid", 1305));
		cityList.add(new Axiom("city", "richmond", 19));
		cityList.add(new Axiom("city", "spokane", 1909));
		cityList.add(new Axiom("city", "wichita", 1305)); 
		charges = new ArrayList<Axiom>();
		charges.add(new Axiom("charge", "Athens", new Integer(23)));
		charges.add(new Axiom("charge", "Sparta", new Integer(13)));
		charges.add(new Axiom("charge", "Milos", new Integer(17)));
		customers = new ArrayList<Axiom>();
		customers.add(new Axiom("customer", "Marathon Marble", "Sparta"));
		customers.add(new Axiom("customer", "Acropolis Construction", "Athens"));
		customers.add(new Axiom("customer", "Agora Imports", "Sparta"));
		customers.add(new Axiom("customer", "Spiros Theodolites", "Milos"));
		fees = new ArrayList<Axiom>();
		fees.add(new Axiom("fee", new Parameter("name", "Marathon Marble"), new Parameter("fee", new Integer(61))));
		fees.add(new Axiom("fee", new Parameter("name", "Acropolis Construction"), new Parameter("fee", new Integer(47))));
		fees.add(new Axiom("fee", new Parameter("name", "Agora Imports"), new Parameter("fee", new Integer(49))));
		fees.add(new Axiom("fee", new Parameter("name", "Spiros Theodolites"), new Parameter("fee", new Integer(57))));
		freights = new ArrayList<Axiom>();
		freights.add(new Axiom("freight", new Parameter("city", "Athens"), new Parameter("freight", new Integer(5))));
		freights.add(new Axiom("freight", new Parameter("city", "Sparta"), new Parameter("freight", new Integer(16))));
		freights.add(new Axiom("freight", new Parameter("city", "Milos"), new Parameter("freight", new Integer(22))));
		new DI(new QueryExecuterModule());
	}


	@Test
	public void test_xpl() throws ParseException
	{
		QueryParserTest.openScript(GREEK_CONSTRUCTION);
	}
	
    @Test 
    public void test_cities() throws Exception
    {
	    StringOperand name = new TestStringOperand("name");
	    IntegerOperand altitude = new TestIntegerOperand("altitude");
	    Template cities = new Template(parseTemplateName("city"), name, altitude);
	    AxiomCollection ensemble = new AxiomCollection(){

			@Override
			public AxiomSource getAxiomSource(String name) {
				return new AxiomListSource(cityList);
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, Collections.singletonList(cities));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
    	assertThat(query.toString()).isEqualTo("city(name, altitude)");
    	int index = 0;
	    while (query.execute())
	    	assertThat(query.toString()).isEqualTo(CITY_NAME_HEIGHT[index++]);
	    assertThat(query.execute()).isFalse();
    }
 
	@Test
	public void test_greek_business() throws Exception
	{
    	AxiomCollection ensemble = new AxiomCollection()
    	{
			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if ("charge".equals(name))
					return new AxiomListSource(charges);
				else
					return new AxiomListSource(customers);
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
	    Variable city = new TestVariable("city");
	    Variable charge = new TestVariable("charge");
	    Variable name = new TestVariable("name");
	    Template s1 = new Template(parseTemplateName("charge"), city, charge);
	    s1.setKey(charges.get(0).getName());
	    Template s2 = new Template(parseTemplateName("customer"), name, city);
	    s2.setKey(customers.get(0).getName());
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(s1);
	    templateList.add(s2);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
    	assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
       	int index = 0;
	    while (query.execute())
	    	assertThat(query.toString()).isEqualTo(GREEK_BUSINESS[index++]);
	}

    @Test
	public void test_chainQuery() throws Exception
	{
    	AxiomCollection ensemble = new AxiomCollection()
    	{
			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if ("charge".equals(name))
					return new AxiomListSource(charges);
				else if ("customer".equals(name))
					return new AxiomListSource(customers);
				else
					return new AxiomListSource(fees);
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
	    Variable city = new TestVariable("city");
	    Variable charge = new TestVariable("charge");
	    Variable name = new TestVariable("name");
	    Template s1 = new Template(parseTemplateName("charge"), city, charge);
	    s1.setKey(charges.get(0).getName());
	    Template s2 = new Template(parseTemplateName("customer"), name, city);
	    s2.setKey(customers.get(0).getName());
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(s1);
	    templateList.add(s2);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
	    Evaluator expression = new TestEvaluator(new TestStringOperand("customer.name"), "==", new TestStringOperand("name"));
	    Evaluator invoice = new TestEvaluator("name", expression, "&&");
	    Variable fee = new TestVariable("fee");
	    Template account = new Template(parseTemplateName("account"), invoice, fee);
	    account.setKey("fee");
	    query.chain(ensemble, Collections.singletonList(account));
	    //System.out.println(query.toString());
    	assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
       	int index = 0;
	    while (query.execute())
	    	assertThat(query.toString()).isEqualTo(GREEK_BUSINESS[index++]);
	    	//System.out.println(query.toString());
	}

    @Test
	public void test_two_ChainQuery() throws Exception
	{
		InputStream stream = new ByteArrayInputStream(GREEK_CONSTRUCTION.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
	    final ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
        QuerySpec querySpec = new QuerySpec("TEST");
 	    Variable city = new TestVariable("city");
	    Variable charge = new TestVariable("charge");
	    Variable name = new TestVariable("name");
	    parserAssembler.createTemplate(parseTemplateName("charge"), false);
	    Template s1 = parserAssembler.getTemplate(parseTemplateName("charge"));
	    s1.addTerm(city);
	    s1.addTerm(charge);
	    s1.setKey(charges.get(0).getName());
		KeyName keyName1 = new KeyName(s1.getKey(), "charge");
		querySpec.addKeyName(keyName1);
		parserAssembler.createTemplate(parseTemplateName("customer"), false);
	    Template s2 = parserAssembler.getTemplate(parseTemplateName("customer"));
	    s2.addTerm(name);
	    s2.addTerm(city);
	    s2.setKey(customers.get(0).getName());
		KeyName keyName2 = new KeyName(s2.getKey(), "customer");
		querySpec.addKeyName(keyName2);
        QueryParams queryParams = new QueryParams(queryProgram.getGlobalScope(), querySpec);
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
	    Evaluator expression = new TestEvaluator(new TestStringOperand("name"), "==", name);
	    Evaluator nameMatch = new TestEvaluator("name", expression, "&&");
	    Variable fee = new TestVariable("fee");
	    Template account = new Template(parseTemplateName("account"), nameMatch, fee);
	    account.setKey("fee");
	    AxiomCollection axiomEnsemble = new AxiomCollection(){

			@Override
			public AxiomSource getAxiomSource(String axiomName) {
				return parserAssembler.getAxiomSource(QualifiedName.parseName(axiomName));
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};

	    query.chain(axiomEnsemble, Collections.singletonList(account));
	    Evaluator expression2 = new TestEvaluator(new TestStringOperand("city"), "==", city);
	    Evaluator cityMatch = new TestEvaluator("city", expression2, "&&");
	    Variable freight = new TestVariable("freight");
	    Template delivery = new Template(parseTemplateName("delivery"), cityMatch, freight);
	    delivery.setKey("freight");
	    query.chain(axiomEnsemble, Collections.singletonList(delivery));
       	int index = 0;
	    while (query.execute())
	    {
	    	//System.out.println(query.getSolution().getAxiom("account").toString());
	    	//System.out.println(query.getSolution().getAxiom("delivery").toString());
			assertThat(query.getSolution().getAxiom("account").toString()).isEqualTo(FEE_AND_FREIGHT[index++]);
			assertThat(query.getSolution().getAxiom("delivery").toString()).isEqualTo(FEE_AND_FREIGHT[index++]);
	    }
	}

    @Test
	public void test_two_ChainQuery_multi_Test() throws Exception
	{
    	AxiomCollection ensemble = new AxiomCollection()
    	{
			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if ("charge".equals(name))
					return new AxiomListSource(charges);
				else if ("customer".equals(name))
					return new AxiomListSource(customers);
				else if ("fee".equals(name))
					return new AxiomListSource(fees);
				else if ("freight".equals(name))
					return new AxiomListSource(freights);
				return new EmptyAxiomSource();
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
        QualifiedName chargeTemplateName = parseTemplateName("charge");
        Variable city = new Variable(QualifiedName.parseName("city", chargeTemplateName));
        Variable charge = new Variable(QualifiedName.parseName("charge", chargeTemplateName));
        Template s1 = new Template(chargeTemplateName, city, charge);
        s1.setKey(charges.get(0).getName());
        QualifiedName customerTemplateName = parseTemplateName("customer");
        Variable name = new Variable(QualifiedName.parseName("name", customerTemplateName));
        Variable city2 = new Variable(QualifiedName.parseName("city", customerTemplateName));
        Template s2 = new Template(customerTemplateName, name, city2);
	    s2.setKey(customers.get(0).getName());
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(s1);
	    templateList.add(s2);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        QualifiedName accountTemplateName = parseTemplateName("account");
        Evaluator expression = new Evaluator(new StringOperand(QualifiedName.parseName("name", accountTemplateName)), "==", name);
        Evaluator nameMatch = new Evaluator(QualifiedName.parseName("name", accountTemplateName), expression, "&&");
        Variable fee = new Variable(QualifiedName.parseName("fee", accountTemplateName));
        Template account = new Template(accountTemplateName, nameMatch, fee);
        account.setKey("fee");
        QualifiedName deliveryTemplateName = parseTemplateName("delivery");
        Evaluator expression2 = new Evaluator(new StringOperand(QualifiedName.parseName("city", deliveryTemplateName)), "==", city2);
        Evaluator cityMatch = new Evaluator(QualifiedName.parseName("city", deliveryTemplateName), expression2, "&&");
        Variable freight = new Variable(QualifiedName.parseName("freight", deliveryTemplateName));
        Template delivery = new Template(deliveryTemplateName, cityMatch, freight);
	    delivery.setKey("freight");
	    List<Template> chainTemplateList = new ArrayList<Template>(2);
	    chainTemplateList.add(account);
	    chainTemplateList.add(delivery);
	    query.chain(ensemble, chainTemplateList);
	    QualifiedName qualifiedChargeCityName = new QualifiedName("city", chargeTemplateName);
	    Template spartaOnly = new Template(parseTemplateName("sparta_only"), new StringOperand(qualifiedChargeCityName, "Sparta"));
	    spartaOnly.setKey("spartaOnly");
	    query.chain(ensemble, Collections.singletonList(spartaOnly));
	    //System.out.println(query.toString());
    	assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
	    Iterator<ChainQuery> it = query.chainQueryIterator();
        //System.out.println(it.next().toString());
	    assertThat(it.next().toString()).isEqualTo("account(name?name==name, fee), delivery(city?city==city, freight)");
        //System.out.println(it.next().toString());
	    assertThat(it.next().toString()).isEqualTo("sparta_only(charge.city = Sparta)");
	    //while (it.hasNext())
		//    System.out.println(">>" + it.next().toString());
	    assertThat(query.execute()).isTrue();
		assertThat(query.getSolution().getAxiom("account").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[0]);
		assertThat(query.getSolution().getAxiom("delivery").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[1]);
	    assertThat(query.execute()).isTrue();
		assertThat(query.getSolution().getAxiom("account").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[2]);
		assertThat(query.getSolution().getAxiom("delivery").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[3]);
		assertThat(query.execute()).isFalse();
	}

    @Test
	public void test_chainQuery_short_circuit() throws Exception
	{
    	AxiomCollection ensemble = new AxiomCollection()
    	{
			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if ("charge".equals(name))
					return new AxiomListSource(charges);
				else if ("customer".equals(name))
					return new AxiomListSource(customers);
				else
					return new AxiomListSource(fees);
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
	    Variable city = new TestVariable("city");
	    Variable charge = new TestVariable("charge");
	    Variable name = new TestVariable("name");
        QualifiedName chargeTemplateName = parseTemplateName("charge");
	    Template s1 = new Template(chargeTemplateName, city, charge);
	    s1.setKey(charges.get(0).getName());
	    Template s2 = new Template(parseTemplateName("customer"), name, city);
	    s2.setKey(customers.get(0).getName());
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(s1);
	    templateList.add(s2);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
	    Evaluator expression = new TestEvaluator(new TestBooleanOperand("True", Boolean.TRUE), "==", new TestBooleanOperand("True2", Boolean.TRUE));
	    Evaluator invoice = new TestEvaluator("name", expression, "||");
	    Variable fee = new TestVariable("fee");
	    Template account = new Template(parseTemplateName("account"), invoice, fee);
	    account.setKey("fee");
	    query.chain(ensemble, Collections.singletonList(account));
	    //System.out.println(query.toString());
    	assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
	    assertThat(query.execute()).isFalse();
	    List<LogicQuery> logicQueryList = query.logicQueryList;
	    assertThat(logicQueryList.get(0).getQueryStatus()).isEqualTo(QueryStatus.complete);
	    assertThat(query.execute()).isFalse();
	}

    @Test 
    public void test_query_logic_axiom_listener() throws Exception
    {
	    StringOperand name = new TestStringOperand("name");
	    IntegerOperand altitude = new TestIntegerOperand("altitude");
	    Template cities = new Template(parseTemplateName("city"), name, altitude);
        AxiomListener axiomListener = new AxiomListener(){
        	int i;
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				//System.out.println(axiom.toString());
				assertThat(axiom.toString()).isEqualTo(CITY_AXIOMS[i++]);
			}};
		Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		ParserAssembler parserAssembler = mock(ParserAssembler.class);
		Scope scope = mock(Scope.class);
		when(scope.getParserAssembler()).thenReturn(parserAssembler);
		when(parserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		when(scope.findAxiomSource("city")).thenReturn(new AxiomListSource(cityList));
		when(scope.getTemplate("city")).thenReturn(cities);
		axiomListenerMap.put(QualifiedName.parseName("city"), Collections.singletonList(axiomListener));
		QuerySpec querySpec = new QuerySpec("Test");
		KeyName keyName1 = new KeyName("city", "city");
		querySpec.addKeyName(keyName1);
        QueryParams queryParams = new QueryParams(scope, querySpec);
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
    	assertThat(query.toString()).isEqualTo("city(name, altitude)");
    	int index = 0;
	    while (query.execute())
	    	assertThat(query.toString()).isEqualTo(CITY_NAME_HEIGHT[index++]);
	    assertThat(query.execute()).isFalse();
    }

    @Test
	public void test_two_ChainQuery_multi_axiom_listener_Test() throws Exception
	{
    	AxiomCollection ensemble = new AxiomCollection()
    	{
			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if ("charge".equals(name))
					return new AxiomListSource(charges);
				else if ("customer".equals(name))
					return new AxiomListSource(customers);
				else if ("fee".equals(name))
					return new AxiomListSource(fees);
				else if ("freight".equals(name))
					return new AxiomListSource(freights);
				return null;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}};
	    QualifiedName chargeTemplateName = parseTemplateName("charge");
	    Variable city = new Variable(QualifiedName.parseName("city", chargeTemplateName));
	    Variable charge = new Variable(QualifiedName.parseName("charge", chargeTemplateName));
	    Template s1 = new Template(chargeTemplateName, city, charge);
	    s1.setKey(charges.get(0).getName());
        QualifiedName customerTemplateName = parseTemplateName("customer");
        Variable name = new Variable(QualifiedName.parseName("name", customerTemplateName));
        Variable city2 = new Variable(QualifiedName.parseName("city", customerTemplateName));
	    Template s2 = new Template(customerTemplateName, name, city2);
	    s2.setKey(customers.get(0).getName());
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(s1);
	    templateList.add(s2);
	    final MultQueryTracer multiQueryTracer = new MultQueryTracer();
        AxiomListener axiomListener1 = new AxiomListener(){
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				multiQueryTracer.trace(axiom);
			}};
        AxiomListener axiomListener2 = new AxiomListener(){
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				multiQueryTracer.trace(axiom);
			}};
        AxiomListener axiomListener3 = new AxiomListener(){
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				multiQueryTracer.trace(axiom);
			}};
        AxiomListener axiomListener4 = new AxiomListener(){
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				multiQueryTracer.trace(axiom);
			}};
        AxiomListener axiomListener5 = new AxiomListener(){
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				multiQueryTracer.trace(axiom);
			}};
		Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		axiomListenerMap.put(QualifiedName.parseName("charge"), Collections.singletonList(axiomListener1));
		axiomListenerMap.put(QualifiedName.parseName("customer"), Collections.singletonList(axiomListener2));
		axiomListenerMap.put(QualifiedName.parseName("fee"), Collections.singletonList(axiomListener3));
		axiomListenerMap.put(QualifiedName.parseName("freight"), Collections.singletonList(axiomListener4));
		axiomListenerMap.put(QualifiedName.parseName("sparta_only"), Collections.singletonList(axiomListener5));

		ParserAssembler parserAssembler = mock(ParserAssembler.class);
		Scope scope = mock(Scope.class);
		when(scope.getParserAssembler()).thenReturn(parserAssembler);
		when(scope.findAxiomSource("charge")).thenReturn(new AxiomListSource(charges));
		when(scope.findAxiomSource("customer")).thenReturn(new AxiomListSource(customers));
		when(scope.findAxiomSource("fee")).thenReturn(new AxiomListSource(fees));
		when(scope.findAxiomSource("freight")).thenReturn(new AxiomListSource(freights));
		when(scope.getTemplate("charge")).thenReturn(s1);
		when(scope.getTemplate("customer")).thenReturn(s2);
		when(parserAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
		QuerySpec querySpec = new QuerySpec("Test");
		KeyName keyName1 = new KeyName(s1.getKey(), "charge");
		querySpec.addKeyName(keyName1);
		KeyName keyName2 = new KeyName(s2.getKey(), "customer");
		querySpec.addKeyName(keyName2);
        QueryParams queryParams = new QueryParams(scope, querySpec);
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        QualifiedName accountTemplateName = parseTemplateName("account");
	    Evaluator expression = new Evaluator(new StringOperand(QualifiedName.parseName("name", accountTemplateName)), "==", name);
	    Evaluator nameMatch = new Evaluator(QualifiedName.parseName("name", accountTemplateName), expression, "&&");
	    Variable fee = new Variable(QualifiedName.parseName("fee", accountTemplateName));
	    Template account = new Template(accountTemplateName, nameMatch, fee);
	    account.setKey("fee");
	    QualifiedName deliveryTemplateName = parseTemplateName("delivery");
	    Evaluator expression2 = new Evaluator(new StringOperand(QualifiedName.parseName("city", deliveryTemplateName)), "==", city2);
	    Evaluator cityMatch = new Evaluator(QualifiedName.parseName("city", deliveryTemplateName), expression2, "&&");
	    Variable freight = new Variable(QualifiedName.parseName("freight", deliveryTemplateName));
	    Template delivery = new Template(deliveryTemplateName, cityMatch, freight);
	    delivery.setKey("freight");
	    List<Template> chainTemplateList = new ArrayList<Template>(2);
	    chainTemplateList.add(account);
	    chainTemplateList.add(delivery);
	    query.chain(ensemble, chainTemplateList);
        QualifiedName qualifiedChargeCityName = new QualifiedName("city", chargeTemplateName);
	    Template spartaOnly = new Template(parseTemplateName("sparta_only"), new StringOperand(qualifiedChargeCityName, "Sparta"));
	    spartaOnly.setKey("spartaOnly");
	    query.chain(ensemble, Collections.singletonList(spartaOnly));
	    //System.out.println(query.toString());
    	assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
	    Iterator<ChainQuery> it = query.chainQueryIterator();
	    assertThat(it.next().toString()).isEqualTo("account(name?name==name, fee), delivery(city?city==city, freight)");
	    assertThat(it.next().toString()).isEqualTo("sparta_only(charge.city = Sparta)");
	    //while (it.hasNext())
		//    System.out.println(">>" + it.next().toString());
	    assertThat(query.execute()).isTrue();
		assertThat(query.getSolution().getAxiom("account").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[0]);
		assertThat(query.getSolution().getAxiom("delivery").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[1]);
	    assertThat(query.execute()).isTrue();
		assertThat(query.getSolution().getAxiom("account").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[2]);
		assertThat(query.getSolution().getAxiom("delivery").toString()).isEqualTo(SPARTA_FEE_AND_FREIGHT[3]);
		assertThat(query.execute()).isFalse();
	}

    protected static QualifiedName parseTemplateName(String name)
    {
        return new QualifiedName(QualifiedName.EMPTY, name, QualifiedName.EMPTY);
    }
}
