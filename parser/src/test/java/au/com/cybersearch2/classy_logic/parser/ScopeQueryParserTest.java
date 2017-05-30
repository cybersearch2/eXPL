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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.JavaTestResourceEnvironment;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.query.QueryExecuterAdapter;
import au.com.cybersearch2.classy_logic.query.QueryExecuterTest;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.query.TestQueryExecuter;
import au.com.cybersearch2.classy_logic.terms.Parameter;

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
		"high_city(name=addis ababa, altitude=8000)",
		"high_city(name=denver, altitude=5280)",
		"high_city(name=flagstaff, altitude=6970)",
		"high_city(name=leadville, altitude=10200)"
	};

	static final String AGRICULTURAL_LAND = 
		"include \"agriculture-land.xpl\";\n" +
		"include \"surface-land.xpl\";\n" +
		"template agri_10y(double Y1990, double Y2010, country ? Y2010 - Y1990 > 1.0);\n" +
		"calc surface_area_increase\n" +
		"(\n" +
		"  country ? country == agri_10y.country,\n" +
		". double surface_area_Km2,\n" +
		"  double surface_area=(agri_10y.Y2010 - agri_10y.Y1990)/100 * surface_area_Km2\n" +
		");\n" +
		"calc km2_to_mi2 (decimal mi2, mi2=surface_area_increase.surface_area * 0.3861);" +
        "scope countries\n" +
	    "{\n" +
	    "  query more_agriculture(Data : agri_10y, surface_area : surface_area_increase);\n" + 
	    "  query more_agriculture_mi2(Data :agri_10y, surface_area : surface_area_increase)\n" + 
	    "  >> (km2_to_mi2);" +
		"}\n";
 
	static final String GREEK_CONSTRUCTION =
	
	    QueryExecuterTest.GREEK_CONSTRUCTION + 
		"template customer(name, city);\n" +
        "template charge(city ? city == customer.city,  charge);\n" +
		"template account(name ? name == customer.name, fee);\n" +
		"template delivery(city ? city == charge.city, freight);\n" +
		"scope greek_construction\n" +
	    "{\n" +
	    "  query greek_business(customer:customer, charge:charge)\n" + 
		"  >> (fee:account) >> (freight:delivery);" +
		"}\n"
	;

	static final String[] FEE_AND_FREIGHT =
	{
        "account(name=Marathon Marble, fee=61)",
        "delivery(city=Sparta, freight=16)",
		"account(name=Acropolis Construction, fee=47)",
		"delivery(city=Athens, freight=5)",
		"account(name=Agora Imports, fee=49)",
		"delivery(city=Sparta, freight=16)",
		"account(name=Spiros Theodolites, fee=57)",
		"delivery(city=Milos, freight=22)"
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
        "bill: {flat,hooked,sharp hooked,short}",
        "color: {dark,white}",
        "eats: {birds,flying insects,insects,meat}",
        //"family: ",
        "feed: {on water surface,scavange}",
        "feet: {curved talons,one long backward toe,webbed}",
        "flight: {agile,flap glide,flat,ponderous,powerful,v shaped}",
        "head: {large}",
        "live: {at sea}",
        "neck: {long}",
        "nostrils: {external tubular}",
        //"order: ",
        "size: {large,medium,plump}",
        "tail: {forked,long rusty,narrow at tip,square}",
        "throat: {white}",
        "voice: {loud trumpeting,muffled musical whistle,short whistle}",
        "wings: {broad,long narrow,long pointed}"
    };

	static final String INSERT_SORT_XPL =
	   		"axiom unsorted() {12, 3, 1, 5, 8};\n" +
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
		    "  query insert_sort (insert_sort);\n" + 
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
		    "  query factorial(factorial)(n = 4);\n" + 
			"}"
		    ;
 
	static final String GERMAN_CURRENCY_XPL =
			"axiom lexicon (Total);\n" +
		    "axiom german.lexicon (Total)\n" +
	        "  {\"Gesamtkosten\"};\n" +
	        "local translate(lexicon);" +
			"template charge(currency amount);\n" +
			"calc charge_plus_gst(currency total = charge.amount * 1.1);\n" +
			"calc format_total(string total_text = translate^Total + \" + gst: \" + format(charge_plus_gst.total));\n" +
			"scope german (language=\"de\", region=\"DE\")\n" +
			"{\n" +
			"  axiom item() {\"12.345,67 €\"};\n" +
			"  query item_query(item : charge) >> (charge_plus_gst) >> (format_total);\n" +
	        "}";

    static final String GERMAN_COLORS =
            "axiom lexicon (aqua, black, blue, white);\n" +
            "axiom german.lexicon (aqua, black, blue, white)\n" +
            "  {\"Wasser\", \"schwarz\", \"blau\", \"weiß\"};\n" +
            "local colors(lexicon);" +
            "choice swatch (name, red, green, blue)\n" +
            "{colors^aqua, 0, 255, 255}\n" +
            "{colors^black, 0, 0, 0}\n" +
            "{colors^blue, 0, 0, 255}\n" +
            "{colors^white, 255, 255, 255};\n" +
            "axiom shade (name) : parameter;\n" +
            "scope german (language=\"de\", region=\"DE\")\n" +
            "{\n" +
            "  query color_query (shade : swatch);\n" +
            "}";

    static final String GERMAN_SCOPE_XPL =
            "scope german (language=\"de\", region=\"DE\")\n" +
            "{\n" +
            "  calc format_summary(string summary = " +
            "  \"language = \" + scope^language + " +
            "  \", region = \" + scope^region);\n" +
            "  query properties_query(format_summary);\n" +
            "}";

    static final String MEGA_CITY3 = 
            "include \"mega_city.xpl\";\n" +
            "template city (Rank, Megacity, Continent, Country, decimal Population);\n" +
            "scope german (language=\"de\", region=\"DE\")\n" +        
            "{\n" +
            "  choice population_group\n" +
            "  (Population,                   Group)\n" +
            "  {Population >= \"30.000.000\", \"Mega\"}\n" +
            "  {Population >= \"20.000.000\", \"Huge\"}\n" +
            "  {Population <  \"20.000.000\", \"Large\"};\n" +
            "  list city_group_list(german.population_group);\n" +
            "  query group_query (mega_city:city) >> (city:population_group);\n" +
            "}\n";


    @Before
    public void setup() throws Exception
    {
    }

    @Test
    public void test_choice_string_colors()
    { 
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(GERMAN_COLORS);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams("german", "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", "Wasser"))); // aqua
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                //System.out.println(solution.getAxiom("swatch").toString());
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(name=Wasser, red=0, green=255, blue=255, swatch=0)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        queryParams = queryProgram.getQueryParams("german", "color_query");
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", "blau"))); // blue
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(name=blau, red=0, green=0, blue=255, swatch=2)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        // Test choice short circuit on no match
        queryParams  = queryProgram.getQueryParams("german", "color_query");
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", "Orange"))); // orange
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").getTermCount()).isEqualTo(0);
                return true;
            }});
        queryProgram.executeQuery(queryParams);
    }
    

    @Test
    public void test_mega_cities3() throws IOException
    {
        QueryProgram queryProgram = new QueryProgram(new TestAxiomProvider());
        queryProgram.parseScript(MEGA_CITY3);
        //queryProgram.executeQuery("german.group_query", new SolutionHandler(){
        //  @Override
        //  public boolean onSolution(Solution solution) {
        //      System.out.println(solution.getAxiom("german.population_group").toString());
        //      return true;
        //  }});
        Result result = queryProgram.executeQuery("german.group_query");
        Iterator<Axiom> iterator = result.getIterator(QualifiedName.parseName("german.city_group_list"));
        File citiesList = new File("src/test/resources", "cities-group.lst");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(citiesList), "UTF-8"));
        while(iterator.hasNext())
        {
            //System.out.println(iterator.next().toString());
            String line = reader.readLine();
            assertThat(iterator.next().toString()).isEqualTo(line);
        }
        reader.close();
    }
    
    @Test
    public void test_german_currency_format() throws ParseException
    {
		InputStream stream = new ByteArrayInputStream(GERMAN_CURRENCY_XPL.getBytes());
		QueryParser queryParser = new QueryParser(stream);
		QueryProgram queryProgram = new QueryProgram();
		ParserContext context = new ParserContext(queryProgram);
		queryParser.input(context);
		queryProgram.executeQuery("german.item_query", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("format_total").toString());
				//System.out.println(solution.getAxiom("charge_plus_gst").toString());
				assertThat(solution.getAxiom("charge_plus_gst").toString()).isEqualTo("charge_plus_gst(total=13580.24)");
				assertThat(solution.getAxiom("format_total").toString()).isEqualTo("format_total(total_text=Gesamtkosten + gst: 13.580,24 EUR)");
				return true;
			}});
    }
    
    @Test
    public void test_german_scope() throws ParseException
    {
        InputStream stream = new ByteArrayInputStream(GERMAN_SCOPE_XPL.getBytes());
        QueryParser queryParser = new QueryParser(stream);
        QueryProgram queryProgram = new QueryProgram();
        ParserContext context = new ParserContext(queryProgram);
        queryParser.input(context);
        queryProgram.executeQuery("german.properties_query", new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                //System.out.println(solution.getAxiom("german.format_summary").toString());
                assertThat(solution.getAxiom("german.format_summary").toString()).isEqualTo("format_summary(summary=language = de, region = DE)");
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
				assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(i=5, n=4, factorial=24)");
				return true;
			}};
		queryProgram.executeQuery("factorial_example.factorial", solutionHandler);
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
				//System.out.println(solution.getAxiom("sort_example.insert_sort").toString());
				assertThat(solution.getAxiom("sort_example.insert_sort").toString()).isEqualTo("insert_sort(i=5)");
				return true;
			}};
		queryProgram.executeQuery("sort_example.insert_sort", solutionHandler);
		Axiom sortAxiom = sortScope.getParserAssembler().getAxiomSource(QualifiedName.parseGlobalName("unsorted")).iterator().next();
		//System.out.println(sortAxiom.toString());
		assertThat(sortAxiom.toString()).isEqualTo("unsorted(1, 3, 5, 8, 12)");
	}
	
	@Test
	public void test_city_elevation() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.setResourceBase(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
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
		assertThat(keyName.getAxiomKey()).isEqualTo(new QualifiedName("city"));
		assertThat(keyName.getTemplateName().getTemplate()).isEqualTo("high_city");
		assertThat(globalScope.getParserAssembler().getTemplateAssembler().getTemplate("high_city")).isNotNull();
		// TODO - fix with QualifiedNames
		//assertThat(parserAssembler.getAxiomSource("city")).isNull();
		//assertThat(queryProgram.getGlobalScope().getParserAssembler().getAxiomSource("city")).isNotNull();
		SolutionHandler solutionHandler = new SolutionHandler(){
            int index = 0;
			@Override
			public boolean onSolution(Solution solution) {
				assertThat(solution.getAxiom("high_city").toString()).isEqualTo(HIGH_CITIES[index++]);
				return true;
			}};
		queryProgram.executeQuery("cities.high_cities", solutionHandler);
	}

	@Test
	public void test_agricultural_land() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.setResourceBase(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
		openScript(AGRICULTURAL_LAND, queryProgram);
		SolutionHandler solutionHandler = new SolutionHandler(){
	 	    File surfaceAreaList = new File("src/test/resources", "surface-area.lst");
	  	    LineNumberReader reader = new LineNumberReader(new FileReader(surfaceAreaList));
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("surface_area_increase").toString());
 	 	    	String line = "";
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
 	    		assertThat(solution.getAxiom("surface_area_increase").toString()).isEqualTo(line);
				return true;
			}};
		queryProgram.executeQuery("countries.more_agriculture", solutionHandler);
		solutionHandler = new SolutionHandler(){
	 	    File surfaceAreaList = new File("src/test/resources", "surface_area_mi2.lst");
	  	    LineNumberReader reader = new LineNumberReader(new FileReader(surfaceAreaList));
			@Override
			public boolean onSolution(Solution solution) 
			{
				//System.out.println(solution.getString("surface_area_increase", "country") + " " + solution.getString("km2_to_mi2", "mi2") + " mi2");
 	 	    	String line = "";
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
 	    		assertThat(solution.getString("surface_area_increase", "country") + " " + solution.getString("km2_to_mi2", "mi2") + " mi2").isEqualTo(line);
				return true;
			}};
		queryProgram.executeQuery("countries.more_agriculture_mi2", solutionHandler);
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
			    //System.out.println(solution.getAxiom("account").toString());
			    //System.out.println(solution.getAxiom("delivery").toString());
				assertThat(solution.getAxiom("account").toString()).isEqualTo(FEE_AND_FREIGHT[index++]);
				assertThat(solution.getAxiom("delivery").toString()).isEqualTo(FEE_AND_FREIGHT[index++]);
				return true;
			}};
		queryProgram.executeQuery("greek_construction.greek_business", solutionHandler);
	}

	@Test 
	public void test_birds() throws ParseException, IOException
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.setResourceBase(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
		openScript(BIRDS, queryProgram);
		Scope scope =queryProgram.getScope("birds");
		Map<String, QuerySpec> querySpecMap = scope.getQuerySpecMap();
		Pattern pattern = Pattern.compile("([^_]+)_([^_]+)");
		Set<String> keywordSet = new TreeSet<String>();
		Set<String> orderSet = new TreeSet<String>();
		Set<String> familySet = new TreeSet<String>();
		Set<String> birdSet = new TreeSet<String>();
		final Map<String, List<Axiom>> promptMap = new TreeMap<String, List<Axiom>>();
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
		Set<Axiom> axiomSet = new TreeSet<Axiom>(new Comparator<Axiom> (){

            @Override
            public int compare(Axiom axiom1, Axiom axiom2)
            {
                return axiom1.getName().compareTo(axiom2.getName());
            }}
		);
		axiomSet.addAll(axiomList);
		for (Axiom axiom: axiomSet)
		{
			final String attribute = axiom.getTermByName(keyword).getValue().toString();
			if (("bird_" + keyword).equals(axiom.getName()))
				birdsResultsChecker.checkNextResult(axiom.getTermByName("bird").getValue().toString() + " " + keyword + " " + attribute);
			else 
			{
				QueryProgram queryProgram = new QueryProgram();
				queryProgram.setResourceBase(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
				openScript(BIRDS, queryProgram);

				Scope scope = queryProgram.getScope("birds");
				ParserAssembler parserAssembler = scope.getParserAssembler();
				SolutionHandler solutionHandler = new SolutionHandler(){

					@Override
					public boolean onSolution(Solution solution) {
						if (solution.keySet().contains("birds.family_by_order"))
					    	birdsResultsChecker.checkNextResult("family " + solution.getAxiom("birds.family_by_order").getTermByName("family").getValue().toString() + " " + keyword + " " + attribute);
					    else if (solution.keySet().contains("birds.bird_by_family"))
							birdsResultsChecker.checkNextResult(solution.getAxiom("birds.bird_by_family").getTermByName("bird").getValue().toString() + " " + keyword + " " + attribute);
					    return true;
					}};
				if (("order_" + keyword).equals(axiom.getName()))
				{
					Operand orderOperand = parserAssembler.findOperandByName("order");
					orderOperand.assign(new Parameter(Term.ANONYMOUS, axiom.getTermByName("order").getValue().toString()));
					queryProgram.executeQuery(scope.getName(), "find_family_by_order", solutionHandler);
				}
				else
				{
					Operand familyOperand = parserAssembler.findOperandByName("family");
					familyOperand.assign(new Parameter(Term.ANONYMOUS, axiom.getTermByName("family").getValue().toString()));
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
		//Parameter term1 = (Parameter) queryParams.getTemplateList().get(1).getTermByName("name");
		//Parameter term2 = null;
		QueryExecuterAdapter adapter = new QueryExecuterAdapter(queryParams.getAxiomCollection(), queryParams.getTemplateList());
		TestQueryExecuter headQuery = new TestQueryExecuter(new QueryParams(adapter.getScope(), adapter.getQuerySpec()));
		if (querySpec.getQueryChainList() != null)
			for (QuerySpec chainQuerySpec: querySpec.getQueryChainList())
			{
				 queryParams = new QueryParams(scope, chainQuerySpec);
				 //if (term2 == null)
				 //	 term2 = (Parameter) queryParams.getTemplateList().get(0).getTermByName("name");
				 headQuery.chain(queryParams.getAxiomCollection(), queryParams.getTemplateList());
			}
		while (headQuery.execute())
		{
			//System.out.println("Name1 id = " + term1.getId() + ", Name2 id = " + term2.getId());
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
		ParserContext context = new ParserContext(queryProgram);
		queryParser.input(context);
	}
}
