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
package au.com.cybersearch2.classy_logic.parser;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.query.QueryExecuter;
import au.com.cybersearch2.classy_logic.query.QueryExecuterAdapter;
import au.com.cybersearch2.classy_logic.query.QueryExecuterTest;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyinject.DI;

/**
 * ScopeQueryParserTest
 * @author Andrew Bowley
 * 29 Dec 2014
 */
public class ScopeQueryParserTest 
{
	static final String CITY_EVELATIONS =
	    "include \"named_cities.xpl\";\n" + 
	    "template high_city(string name, altitude ? altitude > 5000);\n" +
        "scope cities\n" +
	    "{\n" +
	    "  query high_cities (city : high_city);\n" + 
		"}\n";

	static final String[] HIGH_CITIES =
	{
		"high_city(name = addis ababa, altitude = 8000)",
		"high_city(name = denver, altitude = 5280)",
		"high_city(name = flagstaff, altitude = 6970)",
		"high_city(name = leadville, altitude = 10200)"
	};

	static final String AGRICULTURAL_LAND = 
		"include \"agriculture-land.xpl\";\n" +
		"include \"surface-land.xpl\";\n" +
		"template agri_10y(country ? Y2010 - Y1990 > 1.0, double Y1990, double Y2010);\n" +
		"template surface_area_increase(agri_10y.country, double surface_area = (agri_10y.Y2010 - agri_10y.Y1990)/100 * surface_area_Km2);\n" +
		"calc km2_to_mi2 (decimal mi2, mi2 = surface_area_increase.surface_area * 0.3861);" +
        "scope countries\n" +
	    "{\n" +
	    "  query more_agriculture(Data : agri_10y, surface_area : surface_area_increase);\n" + 
	    "  query more_agriculture_mi2(Data :agri_10y, surface_area : surface_area_increase)\n" + 
	    "  >> calc(km2_to_mi2);" +
		"}\n";
 
	static final String GREEK_CONSTRUCTION =
	
	    QueryExecuterTest.GREEK_CONSTRUCTION + 
		"template charge(city,  charge);\n" +
		"template customer(name, city);\n" +
		"template account(name ? customer.name == name, fee);\n" +
		"template delivery(city ? charge.city == city, freight);\n" +
		"scope greek_construction\n" +
	    "{\n" +
	    "  query greek_business(charge:charge, customer:customer)\n" + 
		"  >> (fee:account) >> (freight:delivery);" +
		"}\n"
	;

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

	static final String BIRDS = 
		" // Bird data is supplied as incomplete bird attributes by category: order, family or bird (species)\n" +	
		"include \"birds.xpl\";\n" +
        " // Templates to extract menu lists for each bird attribute\n" +
        "template order_nostrils (order, nostrils ? nostrils != \"\");\n" + 
        "template order_live (order, live ? live != \"\");\n" + 
        "template order_bill (order, bill ? bill != \"\");\n" + 
        "template order_feet (order, feet ? feet != \"\");\n" +
        "template order_eats (order, eats ? eats != \"\");\n" +
        "template family_size (family, order, size ? size != \"\");\n" +
        "template family_wings (family, order, wings ? wings != \"\");\n" +
        "template family_neck (family, order, neck ? neck  != \"\");\n" +
        "template family_color (family, order, color ? color != \"\");\n" +
        "template family_flight (family, order, flight ? flight != \"\");\n" +
        "template family_feed (family, order, feed ? feed != \"\");\n" +
        "template family_head (family, order, head ? head != \"\");\n" +
        "template family_tail (family, order, tail ? tail != \"\");\n" +
        "template family_bill (family, order, bill ? bill != \"\");\n" +
        "template family_eats (family, order, eats ? eats != \"\");\n" +
        "template bird_color (bird, family, order, color ? color != \"\");\n" +
        "template bird_eats (bird, family, order, eats ? eats != \"\");\n" +
        "template bird_flight (bird, family, order, flight ? flight != \"\");\n" +
        "template bird_size (bird, family, order, size ? size != \"\");\n" + 
        "template bird_tail (bird, family, order, tail ? tail != \"\");\n" +
        "template bird_throat (bird, family, order, throat ? throat != \"\");\n" +
        "template bird_voice (bird, family, order, voice ? voice != \"\");\n" +
		"scope birds\n" +
        "{\n" +
		" // Variables used for category queries - assigned values programatically\n" +
		"  string order;\n" +
		"  string family;\n" +
        " // Category templates\n" +
        "  template bird_by_family (family, bird);\n" +
        "  template family_by_order (order, family);\n" +
        " // Queries to extract menu lists for each bird attribute\n" +
        "  query order_nostrils (order:order_nostrils);\n" +
        "  query order_live (order:order_live);\n" +
        "  query order_bill (order:order_bill);\n" +
        "  query order_feet (order:order_feet);\n" +
        "  query order_eats (order:order_eats);\n" +
        "  query family_size (family:family_size);\n" +
        "  query family_wings (family:family_wings);\n" +
        "  query family_neck (family:family_neck);\n" +
        "  query family_color (family:family_color);\n" +
        "  query family_flight (family:family_flight);\n" +
        "  query family_feed (family:family_feed);\n" +
        "  query family_head (family:family_head);\n" +
        "  query family_tail (family:family_tail);\n" +
        "  query family_bill (family:family_bill);\n" +
        "  query family_eats (family:family_eats);\n" +
        "  query bird_color (bird:bird_color);\n" +
        "  query bird_eats (bird:bird_eats);\n" +
        "  query bird_flight (bird:bird_flight);\n" +
        "  query bird_size (bird:bird_size);\n" +
        "  query bird_tail (bird:bird_tail);\n" +
        "  query bird_throat (bird:bird_throat);\n" +
        "  query bird_voice (bird:bird_voice);\n" +
        " // Category queries\n" +
        "  query find_bird_by_family (bird:bird_by_family);\n" +
        "  query find_family_by_order (family:family_by_order);\n" +
		"}\n"
        ; 

    static final String[] BIRD_PROMPTS =
    {
        "voice: {loud trumpeting,muffled musical whistle,short whistle}",
        "bill: {flat,hooked,sharp hooked,short}",
        "neck: {long}",
        "feet: {curved talons,one long backward toe,webbed}",
        "eats: {birds,flying insects,insects,meat}",
        "tail: {forked,long rusty,narrow at tip,square}",
        "size: {large,medium,plump}",
        "flight: {agile,flap glide,flat,ponderous,powerful,v shaped}",
        "color: {dark,white}",
        "wings: {broad,long narrow,long pointed}",
        "feed: {on water surface,scavange}",
        "throat: {white}",
        "head: {large}",
        "nostrils: {external tubular}",
        "live: {at sea}"
    };

	static final String INSERT_SORT_XPL =
	   		"axiom unsorted : (12, 3, 1, 5, 8);\n" +
	        "scope sort_example\n" +
		    "{\n" +
             "  list<term> sort_list(unsorted);\n" +
    		"  calc insert_sort (\n" +
    		"  integer i,\n" +
    		"  {\n" +
    		"    integer j = i - 1, \n" +
    		"    integer temp = sort_list[i], \n" +
    		"    {\n" +
    		"      ? temp < sort_list[j],\n" +
       		"      sort_list[j + 1] = sort_list[j],\n" +
    		"      ? --j >= 0\n" +
    		"    },\n" +
     		"    sort_list[j + 1] = temp,\n" +
    		"    ? ++i < length(sort_list)\n" +
    		"  }\n" +
            "  ) (i = 1);\n" +
		    "  query insert_sort calc(insert_sort);\n" + 
			"}\n"
		    ;

    static final String FACTORIAL_CALCULATE_XPL =
    	 	"calc factorial (\n" +
    	 	"  integer i,\n" +
    		"  integer n,\n" +
    		"  decimal factorial,\n" +
		    "  {\n" +
    		"    factorial *= i,\n" +
     		"    ? i++ < n\n" +
		    "  }\n" +
    		")(factorial = 1, i = 1);\n" +
	        "scope factorial_example\n" +
		    "{\n" +
		    "  query factorial calc(factorial)(n = 4);\n" + 
			"}"
		    ;
 
	static final String GERMAN_CURRENCY_XPL =
			"axiom lexicon (language, Total):\n" +
	        "  (\"english\", \"Total\"),\n" +
	        "  (\"german\", \"Gesamtkosten\");\n" +
	        "local translate(lexicon);" +
			"template charge(currency amount);\n" +
			"calc charge_plus_gst(currency total = amount * 1.1);\n" +
			"calc format_total(string total_text = translate[Total] + \" + gst: \" + format(total));\n" +
			"scope german (language=\"de\", region=\"DE\")\n" +
			"{\n" +
			"  axiom item: (\"12.345,67 €\");\n" +
			"  query item_query(item : charge) >> calc(charge_plus_gst) >> calc(format_total);\n" +
	        "}";

    @Before
    public void setup() throws Exception
    {
        new DI(new QueryParserModule()).validate();
    }


    @Test
    public void test_german_currency_Format() throws ParseException
    {
		InputStream stream = new ByteArrayInputStream(GERMAN_CURRENCY_XPL.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		queryParser.input(queryProgram);
		queryProgram.executeQuery("german", "item_query", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("format_total").toString());
				//System.out.println(solution.getAxiom("charge_plus_gst").toString());
				assertThat(solution.getAxiom("charge_plus_gst").toString()).isEqualTo("charge_plus_gst(total = 13580.24)");
				assertThat(solution.getAxiom("format_total").toString()).isEqualTo("format_total(total_text = Gesamtkosten + gst: 13.580,24 EUR)");
				return true;
			}});
    }
    
    @Test
	public void test_factorial() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		openScript(FACTORIAL_CALCULATE_XPL, queryProgram);
		Scope sortScope = queryProgram.getScope("factorial_example");
		assertThat(sortScope).isNotNull();
		SolutionHandler solutionHandler = new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution);
				//System.out.println(solution.getAxiom("factorial").toString());
				assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(i = 5, n = 4, factorial = 24, factorial.1 = true)");
				return true;
			}};
		queryProgram.executeQuery("factorial_example", "factorial", solutionHandler);
	}
	

	@Test
	public void test_insert_sort() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		openScript(INSERT_SORT_XPL, queryProgram);
		Scope sortScope = queryProgram.getGlobalScope();
		assertThat(sortScope).isNotNull();
		SolutionHandler solutionHandler = new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("insert_sort").toString());
				assertThat(solution.getAxiom("insert_sort").toString()).isEqualTo("insert_sort(i = 5, insert_sort.1 = true)");
				return true;
			}};
		queryProgram.executeQuery("sort_example", "insert_sort", solutionHandler);
		Axiom sortAxiom = sortScope.getParserAssembler().getAxiomSource("unsorted").iterator().next();
		//System.out.println(sortAxiom.toString());
		assertThat(sortAxiom.toString()).isEqualTo("unsorted(1, 3, 5, 8, 12)");
	}
	
	@Test
	public void test_city_elevation() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		openScript(CITY_EVELATIONS, queryProgram);
		Scope cityScope = queryProgram.getScope("cities");
		Scope globalScope = queryProgram.getGlobalScope();
		assertThat(cityScope).isNotNull();
		ParserAssembler parserAssembler = cityScope.getParserAssembler();
		assertThat(parserAssembler).isNotNull();
		QuerySpec highCitiesSpec = cityScope.getQuerySpec("high_cities");
		assertThat(highCitiesSpec).isNotNull();
		List<KeyName> keynameList = highCitiesSpec.getKeyNameList();
		assertThat(keynameList).isNotNull();
		assertThat(keynameList.size()).isEqualTo(1);
		KeyName keyName = keynameList.get(0);
		assertThat(keyName.getAxiomKey()).isEqualTo("city");
		assertThat(keyName.getTemplateName()).isEqualTo("high_city");
		assertThat(globalScope.getParserAssembler().getTemplate("high_city")).isNotNull();
		assertThat(parserAssembler.getAxiomSource("city")).isNull();
		assertThat(queryProgram.getGlobalScope().getParserAssembler().getAxiomSource("city")).isNotNull();
		SolutionHandler solutionHandler = new SolutionHandler(){
            int index = 0;
			@Override
			public boolean onSolution(Solution solution) {
				assertThat(solution.getAxiom("high_city").toString()).isEqualTo(HIGH_CITIES[index++]);
				return true;
			}};
		queryProgram.executeQuery("cities", "high_cities", solutionHandler);
	}

	@Test
	public void test_agricultural_land() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		openScript(AGRICULTURAL_LAND, queryProgram);
		SolutionHandler solutionHandler = new SolutionHandler(){
	 	    File surfaceAreaList = new File("src/test/resources", "surface-area.lst");
	  	    LineNumberReader reader = new LineNumberReader(new FileReader(surfaceAreaList));
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("surface_area").toString());
 	 	    	String line = "";
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
 	    		assertThat(solution.getAxiom("surface_area_increase").toString()).isEqualTo(line);
				return true;
			}};
		queryProgram.executeQuery("countries", "more_agriculture", solutionHandler);
		solutionHandler = new SolutionHandler(){
	 	    File surfaceAreaList = new File("src/test/resources", "surface_area_mi2.lst");
	  	    LineNumberReader reader = new LineNumberReader(new FileReader(surfaceAreaList));
			@Override
			public boolean onSolution(Solution solution) 
			{
				//System.out.println(solution.getString("surface_area", "Data.country") + " " + solution.getString("km2_to_mi2", "mi2") + " mi2");
 	 	    	String line = "";
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
 	    		assertThat(solution.getString("surface_area_increase", "agri_10y.country") + " " + solution.getString("km2_to_mi2", "mi2") + " mi2").isEqualTo(line);
				return true;
			}};
		queryProgram.executeQuery("countries", "more_agriculture_mi2", solutionHandler);
	}

	@Test 
	public void test_greek_business() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		openScript(GREEK_CONSTRUCTION, queryProgram);
		SolutionHandler solutionHandler = new SolutionHandler(){
        int index = 0;
			@Override
			public boolean onSolution(Solution solution) 
			{
				assertThat(solution.getAxiom("account").toString()).isEqualTo(FEE_AND_FREIGHT[index++]);
				assertThat(solution.getAxiom("delivery").toString()).isEqualTo(FEE_AND_FREIGHT[index++]);
				return true;
			}};
			//executeQuery(queryProgram, "greek_construction", "greek_business", solutionHandler);
		queryProgram.executeQuery("greek_construction", "greek_business", solutionHandler);
	}

	@Test 
	public void test_birds() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		openScript(BIRDS, queryProgram);
		Scope scope =queryProgram.getScope("birds");
		Map<String, QuerySpec> querySpecMap = scope.getQuerySpecMap();
		Pattern pattern = Pattern.compile("([^_]+)_([^_]+)");
		Set<String> keywordSet = new TreeSet<String>();
		Set<String> orderSet = new TreeSet<String>();
		Set<String> familySet = new TreeSet<String>();
		Set<String> birdSet = new TreeSet<String>();
		final Map<String, List<Axiom>> promptMap = new HashMap<String, List<Axiom>>();
		// Get menu prompts as lists of Axioms mapped by bird attribute (keyword)
		for (Entry<String, QuerySpec> entry: querySpecMap.entrySet())
		{
			final String templateName = entry.getKey();
			Matcher matcher = pattern.matcher(templateName);
			if (!matcher.find())
				continue;
			final String category = matcher.group(1);
			final String keyword = matcher.group(2);
			if ("order".equals(category))
				orderSet.add(keyword);
			else if ("family".equals(category))
				familySet.add(keyword);
			else if ("bird".equals(category))
				birdSet.add(keyword);
			else
				continue;
			if (!keywordSet.contains(keyword))
			{
				keywordSet.add(keyword);
				promptMap.put(keyword, new ArrayList<Axiom>());
			}
			SolutionHandler solutionHandler = new SolutionHandler(){

				@Override
				public boolean onSolution(Solution solution) {
					promptMap.get(keyword).add(solution.getAxiom(templateName));
					return true;
				}};
			queryProgram.executeQuery(scope.getName(), entry.getKey(), solutionHandler);
		}
		// Build menu prompt sets and compare with expected result
		int index = 0;
		for (Entry<String, List<Axiom>> entry: promptMap.entrySet())
		{
			List<Axiom> axiomList = entry.getValue();
			// Use Set to eliminate duplicates and sort in alpha order
			Set<String> promptSet = new TreeSet<String>();
			for (Axiom axiom: axiomList)
			{
				String keyword = entry.getKey();
				Term promptTerm = axiom.getTermByName(keyword);
				if (promptTerm.getValue() != null)
					promptSet.add(promptTerm.getValue().toString());
			}
			boolean firstTime = true;
			StringBuilder builder = new StringBuilder(entry.getKey() + ": ");
			for (String prompt: promptSet)
			{
				if (firstTime)
				{
					firstTime = false;
					builder.append('{');
				}
				else
					builder.append(',');
				builder.append(prompt);
			}
			if (!firstTime)
				builder.append('}');
			//System.out.println(builder.toString());
			assertThat(BIRD_PROMPTS[index++]).isEqualTo(builder.toString());
		}
		// Perform search on every menu item and compare with expected results
		BirdsResultsChecker birdsResultsChecker = new BirdsResultsChecker("scope_birds_results.lst");
		for (String keyword: keywordSet)
		{
			listBirds(promptMap, keyword, birdsResultsChecker);
		}
	}

	private void listBirds(Map<String, List<Axiom>> promptMap, final String keyword, final BirdsResultsChecker birdsResultsChecker) throws ParseException
	{
		List<Axiom> axiomList = promptMap.get(keyword);
		if (axiomList == null)
			return;
		for (Axiom axiom: axiomList)
		{
			final String attribute = axiom.getTermByName(keyword).getValue().toString();
			if (("bird_" + keyword).equals(axiom.getName()))
				birdsResultsChecker.checkNextResult(axiom.getTermByName("bird").getValue().toString() + " " + keyword + " " + attribute);
			else 
			{
				QueryProgram queryProgram = new QueryProgram();
				openScript(BIRDS, queryProgram);

				Scope scope = queryProgram.getScope("birds");
				ParserAssembler parserAssembler = scope.getParserAssembler();
				SolutionHandler solutionHandler = new SolutionHandler(){

					@Override
					public boolean onSolution(Solution solution) {
						if (solution.keySet().contains("family_by_order"))
					    	birdsResultsChecker.checkNextResult("family " + solution.getAxiom("family_by_order").getTermByName("family").getValue().toString() + " " + keyword + " " + attribute);
					    else if (solution.keySet().contains("bird_by_family"))
							birdsResultsChecker.checkNextResult(solution.getAxiom("bird_by_family").getTermByName("bird").getValue().toString() + " " + keyword + " " + attribute);
					    return true;
					}};
				if (("order_" + keyword).equals(axiom.getName()))
				{
					Operand orderOperand = parserAssembler.getOperandMap().get("order");
					orderOperand.assign(axiom.getTermByName("order").getValue().toString());
					queryProgram.executeQuery(scope.getName(), "find_family_by_order", solutionHandler);
				}
				else
				{
					Operand familyOperand = parserAssembler.getOperandMap().get("family");
					familyOperand.assign(axiom.getTermByName("family").getValue().toString());
					queryProgram.executeQuery(scope.getName(), "find_bird_by_family", solutionHandler);
				}
			}
		}
	}

	public void executeQuery(QueryProgram queryProgram, String scopeName, String queryName, SolutionHandler solutionHandler)
	{
		if (scopeName.equalsIgnoreCase(QueryProgram.GLOBAL_SCOPE))
			throw new IllegalArgumentException("Global scope does not support executeQuery");
		Scope scope = queryProgram.getScope(scopeName);
		if (scope == null)
			throw new IllegalArgumentException("Scope \"" + scopeName + "\" does not exist");
		QuerySpec querySpec = scope.getQuerySpec(queryName);
		if (querySpec == null)
			throw new IllegalArgumentException("Query \"" + queryName + "\" does not exist");
		QueryParams queryParams = new QueryParams(scope, querySpec);
		Parameter term1 = (Parameter) queryParams.getTemplateList().get(1).getTermByName("name");
		Parameter term2 = null;
		QueryExecuterAdapter adapter = new QueryExecuterAdapter(queryParams.getAxiomCollection(), queryParams.getTemplateList());
		QueryExecuter headQuery = new QueryExecuter(new QueryParams(adapter.getScope(), adapter.getQuerySpec()));
		if (querySpec.getQueryChainList() != null)
			for (QuerySpec chainQuerySpec: querySpec.getQueryChainList())
			{
				 queryParams = new QueryParams(scope, chainQuerySpec);
				 if (term2 == null)
					 term2 = (Parameter) queryParams.getTemplateList().get(0).getTermByName("name");
				 headQuery.chain(queryParams.getAxiomCollection(), queryParams.getTemplateList());
			}
		while (headQuery.execute())
		{
			System.out.println("Name1 id = " + term1.getId() + ", Name2 id = " + term2.getId());
			//assertThat(term1).isEqualTo(term2);
			if (!solutionHandler.onSolution(headQuery.getSolution()))
				break;
		}
	}

	private void openScript(String script, QueryProgram queryProgram) throws ParseException
	{
		InputStream stream = new ByteArrayInputStream(script.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		queryParser.enable_tracing();
		queryParser.input(queryProgram);
	}
}
