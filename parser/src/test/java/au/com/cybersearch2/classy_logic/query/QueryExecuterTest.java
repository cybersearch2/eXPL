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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.JavaTestResourceEnvironment;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.axiom.AxiomListSource;
import au.com.cybersearch2.classy_logic.axiom.EmptyAxiomSource;
import au.com.cybersearch2.classy_logic.compile.ListAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.LiteralListOperand;
import au.com.cybersearch2.classy_logic.expression.Orientation;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.TestBooleanOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.TemplateArchetype;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * QueryExecuterTest
 * @author Andrew Bowley
 * 30 Dec 2014
 */
public class QueryExecuterTest 
{

    class MultQueryTracer
    {
        int i;
        
        void trace(Axiom axiom)
        {
            //System.out.println(axiom.toString());
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
        "city(name=bilene, altitude=1718)",
        "city(name=addis ababa, altitude=8000)",
        "city(name=denver, altitude=5280)",
        "city(name=flagstaff, altitude=6970)",
        "city(name=jacksonville, altitude=8)",
        "city(name=leadville, altitude=10200)",
        "city(name=madrid, altitude=1305)",
        "city(name=richmond, altitude=19)",
        "city(name=spokane, altitude=1909)",
        "city(name=wichita, altitude=1305)"
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
        "charge(city=Athens, charge=23), customer(name=Acropolis Construction, city=Athens)",
        "charge(city=Sparta, charge=13), customer(name=Marathon Marble, city=Sparta)",
        "charge(city=Sparta, charge=13), customer(name=Agora Imports, city=Sparta)",
        "charge(city=Milos, charge=17), customer(name=Spiros Theodolites, city=Milos)"  
    };

    public static final String GREEK_CONSTRUCTION =
        "axiom charge() \n" +
        "  {\"Athens\", 23 }\n" +
        "  {\"Sparta\", 13 }\n" +
        "  {\"Milos\", 17};\n" +
        "axiom customer()\n" +
        "  {\"Marathon Marble\", \"Sparta\"}\n" +
        "  {\"Acropolis Construction\", \"Athens\"}\n" +
        "  {\"Agora Imports\", \"Sparta\"}\n" +
        "  {\"Spiros Theodolites\", \"Milos\"};\n" +
        "axiom fee (name, fee)\n" +
        "  {\"Marathon Marble\", 61}\n" +
        "  {\"Acropolis Construction\", 47}\n" +
        "  {\"Agora Imports\", 49}\n" +
        "  {\"Spiros Theodolites\", 57};\n" + 
        "axiom freight (city, freight) \n" +
        "  {\"Athens\", 5 }\n" +
        "  {\"Sparta\", 16 }\n" +
        "  {\"Milos\", 22};\n";

    static final String[] GREEK_FEE =
    {
        "account(name=Acropolis Construction, fee=47)",
        "account(name=Marathon Marble, fee=61)",
        "account(name=Agora Imports, fee=49)",
        "account(name=Spiros Theodolites, fee=57)"
    };
    
    static final String[] FEE_AND_FREIGHT =
    {
        "account(name=Acropolis Construction, fee=47)",
        "delivery(city=Athens, freight=5)",
        "account(name=Marathon Marble, fee=61)",
        "delivery(city=Sparta, freight=16)",
        "account(name=Agora Imports, fee=49)",
        "delivery(city=Sparta, freight=16)",
        "account(name=Spiros Theodolites, fee=57)",
        "delivery(city=Milos, freight=22)"
    };
    
    static final String[] SPARTA_FEE_AND_FREIGHT =
    {
        "account(name=Marathon Marble, fee=61)",
        "delivery(city=Sparta, freight=16)",
        "account(name=Agora Imports, fee=49)",
        "delivery(city=Sparta, freight=16)",
    };
    
    static final String[] MULTI_QUERY_TRACE =
    {
    
        "charge(Athens, 23)",
        "customer(Marathon Marble, Sparta)",
        "customer(Acropolis Construction, Athens)",
        "fee(name=Marathon Marble, fee=61)",
        "fee(name=Acropolis Construction, fee=47)",
        "freight(city=Athens, freight=5)",
        "customer(Agora Imports, Sparta)",
        "customer(Spiros Theodolites, Milos)",
        "charge(Sparta, 13)",
        "customer(Marathon Marble, Sparta)",
        "fee(name=Marathon Marble, fee=61)",
        "freight(city=Athens, freight=5)",
        "freight(city=Sparta, freight=16)",
        "sparta_only(charge.city=Sparta)",
        "customer(Acropolis Construction, Athens)",
        "customer(Agora Imports, Sparta)",
        "fee(name=Marathon Marble, fee=61)",
        "fee(name=Acropolis Construction, fee=47)",
        "fee(name=Agora Imports, fee=49)",
        "freight(city=Athens, freight=5)",
        "freight(city=Sparta, freight=16)",
        "sparta_only(charge.city=Sparta)",
        "customer(Spiros Theodolites, Milos)",
        "charge(Milos, 17)",
        "customer(Marathon Marble, Sparta)",
        "customer(Acropolis Construction, Athens)",
        "customer(Agora Imports, Sparta)",
        "customer(Spiros Theodolites, Milos)",
        "fee(name=Marathon Marble, fee=61)",
        "fee(name=Acropolis Construction, fee=47)",
        "fee(name=Agora Imports, fee=49)",
        "fee(name=Spiros Theodolites, fee=57)",
        "freight(city=Athens, freight=5)",
        "freight(city=Sparta, freight=16)",
        "freight(city=Milos, freight=22)"
    };
 
    
    @Before
    public void before() throws Exception
    {
        cityList = new ArrayList<Axiom>();
        AxiomArchetype axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("city"));
        cityList.add(axiomArchetype.itemInstance("bilene", 1718));
        cityList.add(axiomArchetype.itemInstance("addis ababa", 8000));
        cityList.add(axiomArchetype.itemInstance("denver", 5280));
        cityList.add(axiomArchetype.itemInstance("flagstaff", 6970));
        cityList.add(axiomArchetype.itemInstance("jacksonville", 8));
        cityList.add(axiomArchetype.itemInstance("leadville", 10200));
        cityList.add(axiomArchetype.itemInstance("madrid", 1305));
        cityList.add(axiomArchetype.itemInstance("richmond", 19));
        cityList.add(axiomArchetype.itemInstance( "spokane", 1909));
        cityList.add(axiomArchetype.itemInstance("wichita", 1305)); 
        charges = new ArrayList<Axiom>();
        axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("charge"));
        charges.add(axiomArchetype.itemInstance("Athens", new Integer(23)));
        charges.add(axiomArchetype.itemInstance("Sparta", new Integer(13)));
        charges.add(axiomArchetype.itemInstance("Milos", new Integer(17)));
        customers = new ArrayList<Axiom>();
        axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("customer"));
        customers.add(axiomArchetype.itemInstance("Marathon Marble", "Sparta"));
        customers.add(axiomArchetype.itemInstance("Acropolis Construction", "Athens"));
        customers.add(axiomArchetype.itemInstance("Agora Imports", "Sparta"));
        customers.add(axiomArchetype.itemInstance("Spiros Theodolites", "Milos"));
        fees = new ArrayList<Axiom>();
        axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("fee"));
        fees.add(axiomArchetype.itemInstance(new Parameter("name", "Marathon Marble"), new Parameter("fee", new Integer(61))));
        fees.add(axiomArchetype.itemInstance(new Parameter("name", "Acropolis Construction"), new Parameter("fee", new Integer(47))));
        fees.add(axiomArchetype.itemInstance(new Parameter("name", "Agora Imports"), new Parameter("fee", new Integer(49))));
        fees.add(axiomArchetype.itemInstance(new Parameter("name", "Spiros Theodolites"), new Parameter("fee", new Integer(57))));
        freights = new ArrayList<Axiom>();
        axiomArchetype = new AxiomArchetype(QualifiedName.parseGlobalName("freight"));
        freights.add(axiomArchetype.itemInstance(new Parameter("city", "Athens"), new Parameter("freight", new Integer(5))));
        freights.add(axiomArchetype.itemInstance(new Parameter("city", "Sparta"), new Parameter("freight", new Integer(16))));
        freights.add(axiomArchetype.itemInstance(new Parameter("city", "Milos"), new Parameter("freight", new Integer(22))));
    }

    @Test 
    public void test_cities() throws Exception
    {
        TemplateArchetype cityArchetype = new TemplateArchetype(parseTemplateName("city"));
        StringOperand name = new StringOperand(new QualifiedName("name", cityArchetype.getQualifiedName()));
        IntegerOperand altitude = new IntegerOperand(new QualifiedName("altitude", cityArchetype.getQualifiedName()));
        List<Operand> params = new ArrayList<Operand>();
        params.add(name);
        params.add(altitude);
        Template cities = cityArchetype.itemInstance(params);
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
        TemplateArchetype chargeArchetype = new TemplateArchetype(parseTemplateName("charge"));
        Variable city = new Variable(new QualifiedName("city", chargeArchetype.getQualifiedName()));
        Variable charge = new Variable(new QualifiedName("charge", chargeArchetype.getQualifiedName()));
        Template chargeTemplate = new Template(chargeArchetype, city, charge);
        chargeTemplate.setKey("charge");
        TemplateArchetype customerArchetype = new TemplateArchetype(parseTemplateName("customer"));
        Variable name = new Variable(new QualifiedName("name", customerArchetype.getQualifiedName()));
        Variable customerCity = new Variable(new QualifiedName("city", customerArchetype.getQualifiedName()));
        Evaluator expression = new Evaluator(customerCity, "==", city);
        Evaluator cityMatch = new Evaluator(new QualifiedName("city", customerArchetype.getQualifiedName()), expression, "&&", Orientation.unary_postfix);
        Template customerTemplate = new Template(customerArchetype, name, cityMatch);
        customerTemplate.setKey("customer");
        List<Template> templateList = new ArrayList<Template>();
        templateList.add(chargeTemplate);
        templateList.add(customerTemplate);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        //System.out.println(query.toString());
        //assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city?city==city)");
        int index = 0;
        while (query.execute())
            //System.out.println(query.toString());
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
        TemplateArchetype chargeArchetype = new TemplateArchetype(parseTemplateName("charge"));
        Variable city = new Variable(new QualifiedName("city", chargeArchetype.getQualifiedName()));
        Variable charge = new Variable(new QualifiedName("charge", chargeArchetype.getQualifiedName()));
        Template chargeTemplate = new Template(chargeArchetype, city, charge);
        chargeTemplate.getParserTask().run();
        chargeTemplate.setKey("charge");
        TemplateArchetype customerArchetype = new TemplateArchetype(parseTemplateName("customer"));
        Variable name = new Variable(new QualifiedName("name", customerArchetype.getQualifiedName()));
        Variable customerCity = new Variable(new QualifiedName("city", customerArchetype.getQualifiedName()));
        Evaluator expression = new Evaluator(customerCity, "==", city);
        Evaluator cityMatch = new Evaluator(new QualifiedName("city", customerArchetype.getQualifiedName()), expression, "&&", Orientation.unary_postfix);
        Template customerTemplate = new Template(customerArchetype, name, cityMatch);
        customerTemplate.getParserTask().run();
        customerTemplate.setKey("customer");
        List<Template> templateList = new ArrayList<Template>();
        templateList.add(chargeTemplate);
        templateList.add(customerTemplate);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        //while (query.execute())
        //   System.out.println(query.toString());
        TemplateArchetype accountArchetype = new TemplateArchetype(parseTemplateName("account"));
        StringOperand accountNameOperand = new StringOperand(new QualifiedName("name", accountArchetype.getQualifiedName()));
        Evaluator expression2 = new Evaluator(accountNameOperand, "==", name);
        Evaluator nameMatch = new Evaluator(accountNameOperand.getQualifiedName(), expression2, "&&", Orientation.unary_postfix);
        Variable fee = new Variable(new QualifiedName("fee", accountArchetype.getQualifiedName()));
        Template account = new Template(accountArchetype, nameMatch, fee);
        account.getParserTask().run();
        account.setKey("fee");
        query.chain(ensemble, Collections.singletonList(account));
        //System.out.println(query.toString());
        //assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city?city==city)");
        int index = 0;
        while (query.execute())
        {
            //System.out.println(query.getSolution().getAxiom("account").toString());
            assertThat(query.getSolution().getAxiom("account").toString()).isEqualTo(GREEK_FEE[index++]);
       }
    }
    
    @Test
    public void test_two_ChainQuery() throws Exception
    {
        InputStream stream = new ByteArrayInputStream(GREEK_CONSTRUCTION.getBytes());
        QueryParser queryParser = new QueryParser(stream);
        QueryProgram queryProgram = new QueryProgram();
        ParserContext context = new ParserContext(queryProgram);
        queryParser.input(context);
        final ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
        parserAssembler.getTemplateAssembler().createTemplate(parseTemplateName("charge"), false);
        Template chargeTemplate = parserAssembler.getTemplateAssembler().getTemplate(parseTemplateName("charge"));
        Variable city = new Variable(new QualifiedName("city", chargeTemplate.getQualifiedName()));
        Variable charge = new Variable(new QualifiedName("charge", chargeTemplate.getQualifiedName()));
        chargeTemplate.addTerm(city);
        chargeTemplate.addTerm(charge);
        chargeTemplate.getParserTask().run();
        chargeTemplate.setKey(charges.get(0).getName());
        parserAssembler.getTemplateAssembler().createTemplate(parseTemplateName("customer"), false);
        Template customerTemplate = parserAssembler.getTemplateAssembler().getTemplate(parseTemplateName("customer"));
        Variable name = new Variable(new QualifiedName("name", customerTemplate.getQualifiedName()));
        Variable customerCity = new Variable(new QualifiedName("city", customerTemplate.getQualifiedName()));
        Evaluator expression = new Evaluator(customerCity, "==", city);
        Evaluator cityMatch = new Evaluator(new QualifiedName("city", customerTemplate.getQualifiedName()), expression, "&&", Orientation.unary_postfix);
        customerTemplate.addTerm(name);
        customerTemplate.addTerm(cityMatch);
        customerTemplate.setKey("customer");
        customerTemplate.getParserTask().run();
        KeyName keyName1 = new KeyName("charge", "charge");
        QuerySpec querySpec = new QuerySpec("TEST");
        querySpec.addKeyName(keyName1);
        KeyName keyName2 = new KeyName("customer", "customer");
        querySpec.addKeyName(keyName2);
        QueryParams queryParams = new QueryParams(queryProgram.getGlobalScope(), querySpec);
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        //while (query.execute())
        //    System.out.println(query.toString());
        TemplateArchetype accountArchetype = new TemplateArchetype(parseTemplateName("account"));
        StringOperand accountNameOperand = new StringOperand(new QualifiedName("name", accountArchetype.getQualifiedName()));
        Evaluator expression2 = new Evaluator(accountNameOperand, "==", name);
        Evaluator nameMatch = new Evaluator(new QualifiedName("name", accountArchetype.getQualifiedName()), expression2, "&&", Orientation.unary_postfix);
        Variable fee = new Variable(new QualifiedName("fee", accountArchetype.getQualifiedName()));
        Template account = new Template(accountArchetype, nameMatch, fee);
        account.getParserTask().run();
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
        //while (query.execute())
        //    System.out.println(query.getSolution().getAxiom("account").toString());
        TemplateArchetype deliveryArchetype = new TemplateArchetype(parseTemplateName("delivery"));
        StringOperand deliveryCityOperand = new StringOperand(new QualifiedName("city", deliveryArchetype.getQualifiedName()));
        Evaluator expression3 = new Evaluator(deliveryCityOperand, "==", city);
        Evaluator cityMatch2 = new Evaluator(new QualifiedName("city", deliveryArchetype.getQualifiedName()), expression3, "&&", Orientation.unary_postfix);
        Variable freight = new Variable(new QualifiedName("freight", deliveryArchetype.getQualifiedName()));
        Template delivery = new Template(deliveryArchetype, cityMatch2, freight);
        delivery.getParserTask().run();
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
        TemplateArchetype chargeArchetype = new TemplateArchetype(chargeTemplateName);
        Template chargeTemplate = new Template(chargeArchetype, city, charge);
        chargeTemplate.getParserTask().run();
        chargeTemplate.setKey("charge");
        QualifiedName customerTemplateName = parseTemplateName("customer");
        Variable name = new Variable(QualifiedName.parseName("name", customerTemplateName));
        Variable city2 = new Variable(QualifiedName.parseName("city", customerTemplateName));
        TemplateArchetype customerArchetype = new TemplateArchetype(customerTemplateName);
        Template customerTemplate = new Template(customerArchetype, name, city2);
        customerTemplate.getParserTask().run();
        customerTemplate.setKey(customers.get(0).getName());
        List<Template> templateList = new ArrayList<Template>();
        templateList.add(chargeTemplate);
        templateList.add(customerTemplate);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        QualifiedName accountTemplateName = parseTemplateName("account");
        Evaluator expression = new Evaluator(new StringOperand(QualifiedName.parseName("name", accountTemplateName)), "==", name);
        Evaluator nameMatch = new Evaluator(QualifiedName.parseName("name", accountTemplateName), expression, "&&", Orientation.unary_postfix);
        Variable fee = new Variable(QualifiedName.parseName("fee", accountTemplateName));
        TemplateArchetype accountArchetype = new TemplateArchetype(accountTemplateName);
        Template account = new Template(accountArchetype, nameMatch, fee);
        account.getParserTask().run();
        account.setKey("fee");
        QualifiedName deliveryTemplateName = parseTemplateName("delivery");
        Evaluator expression2 = new Evaluator(new StringOperand(QualifiedName.parseName("city", deliveryTemplateName)), "==", city2);
        Evaluator cityMatch = new Evaluator(QualifiedName.parseName("city", deliveryTemplateName), expression2, "&&", Orientation.unary_postfix);
        Variable freight = new Variable(QualifiedName.parseName("freight", deliveryTemplateName));
        TemplateArchetype deliveryArchetype = new TemplateArchetype(deliveryTemplateName);
        Template delivery = new Template(deliveryArchetype, cityMatch, freight);
        delivery.getParserTask().run();
        delivery.setKey("freight");
        List<Template> chainTemplateList = new ArrayList<Template>(2);
        chainTemplateList.add(account);
        chainTemplateList.add(delivery);
        query.chain(ensemble, chainTemplateList);
        QualifiedName spartaOnlyName = QualifiedName.parseTemplateName("sparta_only");
        QualifiedName spartaLitListName = new QualifiedName("city", spartaOnlyName);
        Operand spartaLiteral = new LiteralListOperand(spartaLitListName, Collections.singletonList(new Parameter(Term.ANONYMOUS, "Sparta")));
        TemplateArchetype spartaOnlyArchetype = new TemplateArchetype(spartaOnlyName);
        Template spartaOnly = new Template(spartaOnlyArchetype, spartaLiteral);
        spartaOnly.getParserTask().run();
        spartaOnly.setKey("spartaOnly");
        query.chain(ensemble, Collections.singletonList(spartaOnly));
        //System.out.println(query.toString());
        assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
        Iterator<ChainQuery> it = query.chainQueryIterator();
        //System.out.println(it.next().toString());
        assertThat(it.next().toString()).isEqualTo("account(name?name==name, fee), delivery(city?city==city, freight)");
        //System.out.println(it.next().toString());
        assertThat(it.next().toString()).isEqualTo("sparta_only(city {Sparta})");
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
        TemplateArchetype chargeArchetype = new TemplateArchetype(parseTemplateName("charge"));
        Variable city = new Variable(new QualifiedName("city", chargeArchetype.getQualifiedName()));
        Variable charge = new Variable(new QualifiedName("charge", chargeArchetype.getQualifiedName()));
        Template chargeTemplate = new Template(chargeArchetype, city, charge);
        chargeTemplate.getParserTask().run();
        chargeTemplate.setKey("charge");
        TemplateArchetype customerArchetype = new TemplateArchetype(parseTemplateName("customer"));
        Variable name = new Variable(new QualifiedName("name", customerArchetype.getQualifiedName()));
        Variable customerCity = new Variable(new QualifiedName("city", customerArchetype.getQualifiedName()));
        Evaluator expression = new Evaluator(customerCity, "==", city);
        Evaluator cityMatch = new Evaluator(new QualifiedName("city", customerArchetype.getQualifiedName()), expression, "&&", Orientation.unary_postfix);
        Template customerTemplate = new Template(customerArchetype, name, cityMatch);
        customerTemplate.getParserTask().run();
        customerTemplate.setKey("customer");
        List<Template> templateList = new ArrayList<Template>();
        templateList.add(chargeTemplate);
        templateList.add(customerTemplate);
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        TemplateArchetype accountArchetype = new TemplateArchetype(parseTemplateName("account"));
        Evaluator expression2 = new Evaluator(new TestBooleanOperand("True", Boolean.TRUE), "==", new TestBooleanOperand("True2", Boolean.TRUE));
        Evaluator invoice = new Evaluator(new QualifiedName("name", accountArchetype.getQualifiedName()), expression2, "||", Orientation.unary_postfix);
        Variable fee = new Variable(new QualifiedName("fee", accountArchetype.getQualifiedName()));
        Template account = new Template(accountArchetype, invoice, fee);
        account.getParserTask().run();
        account.setKey("fee");
        query.chain(ensemble, Collections.singletonList(account));
        assertThat(query.execute()).isFalse();
        List<LogicQuery> logicQueryList = query.logicQueryList;
        assertThat(logicQueryList.get(0).getQueryStatus()).isEqualTo(QueryStatus.complete);
        assertThat(query.execute()).isFalse();
    }
    
    @Test 
    public void test_query_logic_axiom_listener() throws Exception
    {
        TemplateArchetype cityArchetype = new TemplateArchetype(parseTemplateName("city"));
        StringOperand name = new StringOperand(new QualifiedName("name", cityArchetype.getQualifiedName()));
        IntegerOperand altitude = new IntegerOperand(new QualifiedName("altitude", cityArchetype.getQualifiedName()));
        Template cities = new Template(cityArchetype, name, altitude);
        cities.getParserTask().run();
        AxiomListener axiomListener = new AxiomListener(){
            int i;
            @Override
            public void onNextAxiom(QualifiedName qname, Axiom axiom) 
            {
                //System.out.println(axiom.toString());
                assertThat(axiom.toString()).isEqualTo(CITY_AXIOMS[i++]);
            }};
        Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
        ParserAssembler parserAssembler = mock(ParserAssembler.class);
        Scope scope = mock(Scope.class);
        ListAssembler listAssembler = mock(ListAssembler.class);
        when(parserAssembler.getListAssembler()).thenReturn(listAssembler);
        when(scope.getParserAssembler()).thenReturn(parserAssembler);
        when(listAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
        axiomListenerMap.put(QualifiedName.parseName("city"), Collections.singletonList(axiomListener));
        QuerySpec querySpec = new QuerySpec("Test");
        KeyName keyName1 = new KeyName("city", "city");
        when(scope.findAxiomSource(keyName1.getAxiomKey())).thenReturn(new AxiomListSource(cityList));
        when(scope.getTemplate(keyName1.getTemplateName())).thenReturn(cities);
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
        TemplateArchetype s1Archetype = new TemplateArchetype(chargeTemplateName);
        Template chargeTemplate = new Template(s1Archetype, city, charge);
        chargeTemplate.getParserTask().run();
        chargeTemplate.setKey("charge");
        QualifiedName chargeAxiomName = new QualifiedName("charge");
        QualifiedName customerTemplateName = parseTemplateName("customer");
        Variable name = new Variable(QualifiedName.parseName("name", customerTemplateName));
        Variable city2 = new Variable(QualifiedName.parseName("city", customerTemplateName));
        TemplateArchetype s2Archetype = new TemplateArchetype(customerTemplateName);
        Template customerTemplate = new Template(s2Archetype, name, city2);
        customerTemplate.getParserTask().run();
        customerTemplate.setKey("customer");
        QualifiedName customerAxiomName = new QualifiedName("customer");
        List<Template> templateList = new ArrayList<Template>();
        templateList.add(chargeTemplate);
        templateList.add(customerTemplate);
        final MultQueryTracer multiQueryTracer = new MultQueryTracer();
        AxiomListener axiomListener1 = new AxiomListener(){
            @Override
            public void onNextAxiom(QualifiedName qname, Axiom axiom) 
            {
                multiQueryTracer.trace(axiom);
            }};
        AxiomListener axiomListener2 = new AxiomListener(){
            @Override
            public void onNextAxiom(QualifiedName qname, Axiom axiom) 
            {
                multiQueryTracer.trace(axiom);
            }};
        AxiomListener axiomListener3 = new AxiomListener(){
            @Override
            public void onNextAxiom(QualifiedName qname, Axiom axiom) 
            {
                multiQueryTracer.trace(axiom);
            }};
        AxiomListener axiomListener4 = new AxiomListener(){
            @Override
            public void onNextAxiom(QualifiedName qname, Axiom axiom) 
            {
                multiQueryTracer.trace(axiom);
            }};
        AxiomListener axiomListener5 = new AxiomListener(){
            @Override
            public void onNextAxiom(QualifiedName qname, Axiom axiom) 
            {
                multiQueryTracer.trace(axiom);
            }};
        QualifiedName feeTemplateName = QualifiedName.parseName("fee");
        QualifiedName freightTemplateName = QualifiedName.parseName("freight");     
        Map<QualifiedName, List<AxiomListener>> axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
        axiomListenerMap.put(QualifiedName.parseName("charge"), Collections.singletonList(axiomListener1));
        axiomListenerMap.put(QualifiedName.parseName("customer"), Collections.singletonList(axiomListener2));
        axiomListenerMap.put(feeTemplateName, Collections.singletonList(axiomListener3));
        axiomListenerMap.put(freightTemplateName, Collections.singletonList(axiomListener4));
        axiomListenerMap.put(QualifiedName.parseName("sparta_only"), Collections.singletonList(axiomListener5));

        ParserAssembler parserAssembler = mock(ParserAssembler.class);
        Scope scope = mock(Scope.class);
        when(scope.getParserAssembler()).thenReturn(parserAssembler);
        when(scope.findAxiomSource(chargeAxiomName)).thenReturn(new AxiomListSource(charges));
        when(scope.findAxiomSource(customerAxiomName)).thenReturn(new AxiomListSource(customers));
        KeyName keyName1 = new KeyName("charge", "charge");
        when(scope.getTemplate(keyName1.getTemplateName())).thenReturn(chargeTemplate);
        KeyName keyName2 = new KeyName("customer", "customer");
        when(scope.getTemplate(keyName2.getTemplateName())).thenReturn(customerTemplate);
        ListAssembler listAssembler = mock(ListAssembler.class);
        when(parserAssembler.getListAssembler()).thenReturn(listAssembler);
        when(listAssembler.getAxiomListenerMap()).thenReturn(axiomListenerMap);
        QuerySpec querySpec = new QuerySpec("Test");
        querySpec.addKeyName(keyName1);
        querySpec.addKeyName(keyName2);
        QueryParams queryParams = new QueryParams(scope, querySpec);
        queryParams.initialize();
        QueryExecuter query = new QueryExecuter(queryParams);
        QualifiedName accountTemplateName = parseTemplateName("account");
        Evaluator expression = new Evaluator(new StringOperand(QualifiedName.parseName("name", accountTemplateName)), "==", name);
        Evaluator nameMatch = new Evaluator(QualifiedName.parseName("name", accountTemplateName), expression, "&&", Orientation.unary_postfix);
        Variable fee = new Variable(QualifiedName.parseName("fee", accountTemplateName));
        TemplateArchetype accountArchetype = new TemplateArchetype(accountTemplateName);
        Template account = new Template(accountArchetype, nameMatch, fee);
        account.getParserTask().run();
        account.setKey("fee");
        QualifiedName feeAxiomName = new QualifiedName("fee");
        when(scope.findAxiomSource(feeAxiomName)).thenReturn(new AxiomListSource(fees));
        QualifiedName deliveryTemplateName = parseTemplateName("delivery");
        Evaluator expression2 = new Evaluator(new StringOperand(QualifiedName.parseName("city", deliveryTemplateName)), "==", city2);
        Evaluator cityMatch = new Evaluator(QualifiedName.parseName("city", deliveryTemplateName), expression2, "&&", Orientation.unary_postfix);
        Variable freight = new Variable(QualifiedName.parseName("freight", deliveryTemplateName));
        TemplateArchetype deliveryArchetype = new TemplateArchetype(deliveryTemplateName);
        Template delivery = new Template(deliveryArchetype, cityMatch, freight);
        delivery.getParserTask().run();
        delivery.setKey("freight");
        QualifiedName freightAxiomName = new QualifiedName("freight");
        when(scope.findAxiomSource(freightAxiomName)).thenReturn(new AxiomListSource(freights));
        List<Template> chainTemplateList = new ArrayList<Template>(2);
        chainTemplateList.add(account);
        chainTemplateList.add(delivery);
        query.chain(ensemble, chainTemplateList);
        QualifiedName spartaOnlyName = QualifiedName.parseTemplateName("sparta_only");
        QualifiedName spartaLitListName = new QualifiedName("city", spartaOnlyName);
        Operand spartaLiteral = new LiteralListOperand(spartaLitListName, Collections.singletonList(new Parameter(Term.ANONYMOUS, "Sparta")));
        TemplateArchetype spartaOnlyArchetype = new TemplateArchetype(spartaOnlyName);
        Template spartaOnly = new Template(spartaOnlyArchetype, spartaLiteral);
        spartaOnly.getParserTask().run();
        spartaOnly.setKey("spartaOnly");
        query.chain(ensemble, Collections.singletonList(spartaOnly));
        //System.out.println(query.toString());
        assertThat(query.toString()).isEqualTo("charge(city, charge), customer(name, city)");
        Iterator<ChainQuery> it = query.chainQueryIterator();
        assertThat(it.next().toString()).isEqualTo("account(name?name==name, fee), delivery(city?city==city, freight)");
        assertThat(it.next().toString()).isEqualTo("sparta_only(city {Sparta})");
        while (it.hasNext())
            System.out.println(">>" + it.next().toString());
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
        return new QualifiedTemplateName(QualifiedName.EMPTY, name);
    }

    protected static QualifiedName parseVariableName(String template, String name)
    {
        return QualifiedName.parseName(name, new QualifiedTemplateName(QualifiedName.EMPTY, template));
    }
    
    public static ParserAssembler openScript(String script) throws ParseException
    {
        InputStream stream = new ByteArrayInputStream(script.getBytes());
        QueryParser queryParser = new QueryParser(stream);
        queryParser.enable_tracing();
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.setResourceBase(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
        ParserContext context = new ParserContext(queryProgram);
        queryParser.input(context);
        return queryProgram.getGlobalScope().getParserAssembler();
    }
    
}
