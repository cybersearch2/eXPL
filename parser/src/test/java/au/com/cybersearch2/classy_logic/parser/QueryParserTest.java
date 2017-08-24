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
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.JavaTestResourceEnvironment;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.QueryProgramParser;
import au.com.cybersearch2.classy_logic.Result;
import au.com.cybersearch2.classy_logic.axiom.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.LexiconSource;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.TemplateArchetype;
import au.com.cybersearch2.classy_logic.query.Calculator;
import au.com.cybersearch2.classy_logic.query.LogicQueryExecuter;
import au.com.cybersearch2.classy_logic.query.QueryExecuterAdapter;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.query.TestChainQueryExecuter;
import au.com.cybersearch2.classy_logic.query.TestQueryExecuter;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * QueryParserTest
 * @author Andrew Bowley
 * 6 Dec 2014
 */
public class QueryParserTest 
{
    static class SystemFunctionProvider implements FunctionProvider
    {
        @Override
        public String getName()
        {
            return "system";
        }

        @Override
        public CallEvaluator<Axiom> getCallEvaluator(String identifier)
        {
            if (identifier.equals("print"))
                return new CallEvaluator<Axiom>(){
    
                    @Override
                    public String getName()
                    {
                        return "print";
                    }
    
                    @Override
                    public Axiom evaluate(List<Term> argumentList)
                    {
                        for (Term term: argumentList)
                            System.out.print(term.getValue().toString());
                        System.out.println();
                        return null;
                    }

                    @Override
                    public void setExecutionContext(ExecutionContext context)
                    {
                    }
                    
            };
            throw new ExpressionException("Unknown function identifier: " + identifier);
        }
    }
    
	static final String SCRIPT1 =
	    "integer twoFlip = ~2;\n" +
        "integer mask = 2;\n" +
	    "integer maskFlip = ~mask;";

	static final String SCRIPT2 =
		"template city(string name, integer height);\n";
	
	static final String SCRIPT3 =
		"axiom city()" +
		"{\"bilene\", 1718}" +
		"{\"denver\", 5280}" +
		";";

	static final String SCRIPT4 =
	    "integer x = 1;\n" +
        "integer y = 2;\n" +
	    "integer x_y = x + y;\n" +
        "integer a = x * 2;\n" +
        "integer b = 7 * 2;\n" +
        "boolean c = (y * 2) < (x * 5);\n" +
        "decimal d = 1234;" +
	    "decimal e = d + b;";

	static final String SCRIPT5 =
	    "integer x = y;\n" +
	    "integer z = 4 + (y = 6);\n" +
	    "integer a = (y = (x + z));";

	static final String CITY_EVELATIONS =
	    "include \"cities.xpl\";" + 		
		"template high_city(string name, integer altitude, boolean is_high = altitude > 5000);";

	static final String GREEK_BUSINESS = "include \"greek_business.xpl\";";
    static final String GREEK_BUSINESS2 = "include \"greek_business2.xpl\";";
	static final String NAMED_GREEK_BUSINESS = "include \"named_greek_business.xpl\";";
	static final String LEXICAL_SEARCH = "template in_words (regex Word == \"^in[^ ]+\", string Definition);";
	static final String NOUN_LEXICAL_SEARCH = "template in_words (regex Word == \"^in[^ ]+\", regex Definition == \"^n\");";
	static final String REGEX_GROUPS = "template dictionary (word, part, text, regex definition == \"^(.)\\. (.*+)\" { part, text });";

	static final String AGRICULTURAL_LAND = 
		"include \"agriculture-land.xpl\";" +
		"include \"surface-land.xpl\";" +
	    "template agri_10y (double Y1990, double Y2010, country ? Y2010 - Y1990 > 1.0);" +
		"calc surface_area_increase " +
		"(" +
		"  country? country == agri_10y.country," +
		". double surface_area_Km2," +
		"  double surface_area = (agri_10y.Y2010 - agri_10y.Y1990)/100 * surface_area_Km2" +
		");";

	static final String[] GREEK_BUSINESS_LIST =
	{
	    "customer(name=Marathon Marble, city=Sparta), charge(city=Sparta, fee=13)",
	    "customer(name=Acropolis Construction, city=Athens), charge(city=Athens, fee=23)",
	    "customer(name=Agora Imports, city=Sparta), charge(city=Sparta, fee=13)",
	    "customer(name=Spiros Theodolites, city=Milos), charge(city=Milos, fee=17)" 	};
	
    static final String[] GREEK_BUSINESS_LIST2 =
    {
        "charge(city=Athens, fee=23), customer(name=Acropolis Construction, city=Athens)",
        "charge(city=Sparta, fee=13), customer(name=Marathon Marble, city=Sparta)",
        "charge(city=Sparta, fee=13), customer(name=Agora Imports, city=Sparta)",
        "charge(city=Milos, fee=17), customer(name=Spiros Theodolites, city=Milos)"
    };
    
	static final String[] CITY_NAME_HEIGHT =
	{
		"city(name=\"bilene\", altitude=1718)",
		"city(name=\"addis ababa\", altitude=8000)",
		"city(name=\"denver\", altitude=5280)",
		"city(name=\"flagstaff\", altitude=6970)",
		"city(name=\"jacksonville\", altitude=8)",
		"city(name=\"leadville\", altitude=10200)",
		"city(name=\"madrid\", altitude=1305)",
		"city(name=\"richmond\", altitude=19)",
		"city(name=\"spokane\", altitude=1909)",
		"city(name=\"wichita\", altitude=1305)"
	};

	static final String[] HIGH_CITY =
	{
		"high_city(name=\"bilene\", is_high=false)",
		"high_city(name=\"addis ababa\", is_high=true)",
		"high_city(name=\"denver\", is_high=true)",
		"high_city(name=\"flagstaff\", is_high=true)",
		"high_city(name=\"jacksonville\", is_high=false)",
		"high_city(name=\"leadville\", is_high=true)",
		"high_city(name=\"madrid\", is_high=false)",
		"high_city(name=\"richmond\", is_high=false)",
		"high_city(name=\"spokane\", is_high=false)",
		"high_city(name=\"wichita\", is_high=false)"	
	};

	static final String[] HIGH_CITY_v2 =
	{
		"high_city(name=bilene, altitude=1718, is_high=false)",
		"high_city(name=addis ababa, altitude=8000, is_high=true)",
		"high_city(name=denver, altitude=5280, is_high=true)",
		"high_city(name=flagstaff, altitude=6970, is_high=true)",
		"high_city(name=jacksonville, altitude=8, is_high=false)",
		"high_city(name=leadville, altitude=10200, is_high=true)",
		"high_city(name=madrid, altitude=1305, is_high=false)",
		"high_city(name=richmond, altitude=19, is_high=false)",
		"high_city(name=spokane, altitude=1909, is_high=false)",
		"high_city(name=wichita, altitude=1305, is_high=false)"
	};

	static final String MEGA_CITY1 = 
		"include \"mega_city.xpl\";\n" +
		"integer count = 0;\n" +
		"template asia_top_ten (Rank, Megacity ? Continent == \"Asia\" && count++ < 10, Country, Population);"; 

	static final String MEGA_CITY2 = 
			"include \"mega_city.xpl\";\n" +
			"template america_megacities (Rank, Megacity ? Continent == \"North America\" || Continent == \"South America\", Country, Population);";

	static final String BIRDS = 		"include \"birds.xpl\";\n" +
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
                                        "template bird_voice (bird, family, order, voice ? voice != \"\");\n" 
                                        ; 
    static final String[] BIRD_PROMPTS =
    {
        "bill: {flat,hooked,sharp hooked,short}",
        "color: {dark,white}",
        "eats: {birds,flying insects,insects,meat}",
        "family: ",
        "feed: {on water surface,scavange}",
        "feet: {curved talons,one long backward toe,webbed}",
        "flight: {agile,flap glide,flat,ponderous,powerful,v shaped}",
        "head: {large}",
        "live: {at sea}",
        "neck: {long}",
        "nostrils: {external tubular}",
        "order: ",
        "size: {large,medium,plump}",
        "tail: {forked,long rusty,narrow at tip,square}",
        "throat: {white}",
        "voice: {loud trumpeting,muffled musical whistle,short whistle}",
        "wings: {broad,long narrow,long pointed}"
    };
 
    static final String SIMPLE_CALCULATE =
    		"calc increment_n (" +
    		"integer n, " +
    		"integer limit, " +
    		"{\n" +
    		"  ? ++n < limit\n" +
    		"}\n" +
    		") ( n = 1, limit = 3);"
    		;

    static final String FACTORIAL_CALCULATE =
    	 	"calc factorial (" +
    	 	"integer i, " +
    		"integer n, " +
    		"decimal factorial," +
    		"{\n" +
    		"  factorial *= i," +
     		"  ? i++ < n" +
    		"})\n" +
    		"(factorial = 1, n = 4, i = 1);"
    		;

    static final String ONE_SHOT_CALCULATE =
    		"calc km2_to_mi2 (\n" +
    		"decimal km2,\n" +
    		"decimal mi2,\n" +
    		"mi2 = km2 * 0.3861)\n" +
    		"(km2 = 1323.98);"
    		;

    static final String SIMPLE_LIST_CALCULATE =
            "list<integer> number_list;\n" +
    		"calc increment_n (\n" +
    		"integer n, \n" +
    		"integer limit, \n" +
    		"{\n" +
    		"  number_list[0] = n++,\n" +
    		"  number_list[1] = n++,\n" +
    		"  number_list[2] = n++,\n" +
    		"  ? number_list[2] < limit" +
    		"}\n" +
    		") (n = 1, limit = 3);"
    		;

    static final String SIMPLE_VARIABLE_INDEX_LIST_CALCULATE =
            "list<integer> number_list;\n" +
    		"calc increment_n (\n" +
    		"integer n, \n" +
    		"integer i, \n" +
    		"integer limit, \n" +
    		"{\n" +
    		"  number_list[i++] = n++,\n" +
    		"  ? i < limit" +
    		"})\n" +
    		"(n = 1, i = 0, limit = 3);"
    		;

    static final String SIMPLE_LIST_LENGTH_CALCULATE =
            "list<integer> number_list;\n" +
    		"calc increment_n (\n" +
    		"integer n, \n" +
    		"integer i, \n" +
    		"integer limit, \n" +
    		"{\n" +
    		"  number_list[i++] = n++,\n" +
    		"  ? number_list.length < limit\n" +
    		"}\n" +
    		")\n" +
    		"(n = 1, i = 0, limit = 3);"
    		;

    static final String INSERT_SORT_XPL =
    		"axiom unsorted() {12, 3, 1, 5, 8};\n" +
            "list<term> sort_list(unsorted);\n" +
    		"calc insert_sort (\n" +
    		"integer i, \n" +
    		"{\n" +
    		"  integer j = i - 1, \n" +
    		"  integer temp = sort_list[i], \n" +
    		"  {\n" +
    		"    ? temp < sort_list[j],\n" +
       		"    sort_list[j + 1] = sort_list[j],\n" +
    		"    ? --j >= 0\n" +
    		"  },\n" +
     		"  sort_list[j + 1] = temp,\n" +
    		"  ? ++i < sort_list.length\n" +
    		"}\n" +
            ")(i = 1);"
    		;

    static final String AXIOM_WRAPPER_XPL =
    		"axiom colors (red, green, blue) {0.75, 0.50, 0.25};\n" +
            "list<term> colors_list(colors);\n" +
    		"template color_convert(red, green, blue, double r = colors_list->red, double g = colors_list->green, double b = colors_list->blue);"
    		;
    
	static final String CITY_EVELATIONS_SORTED =
		    "axiom city (name, altitude)\n"  +
			"{\"bilene\", 1718}\n"  +
			"{\"addis ababa\", 8000}\n"  +
			"{\"denver\", 5280}\n"  +
			"{\"flagstaff\", 6970}\n"  +
			"{\"jacksonville\", 8}\n"  +
			"{\"leadville\", 10200}\n"  +
			"{\"madrid\", 1305}\n"  +
			"{\"richmond\",19}\n"  +
			"{\"spokane\", 1909}\n"  +
			"{\"wichita\", 1305};\n" +
			"template high_city(string name, altitude ? altitude > 5000);\n" +
            "list city_list(high_city);\n" +
    		"calc insert_sort (\n" +
    		"  integer i = city_list.length- 1, \n" +
    		"  : i < 1, \n" +
    		"  integer j = i - 1, \n" +
    		"  integer altitude = city_list[i].altitude, \n" +
    		". temp = city_list[i],\n" +
    		"  {\n" +
    		"    ? altitude < city_list[j].altitude,\n" +
       		"    city_list[j + 1] = city_list[j],\n" +
    		"    ? --j >= 0\n" +
    		"  },\n" +
     		"  .  city_list[j + 1] = temp,\n" +
    		"  . ++i\n" +
     		");"
			;

	static final String HIGH_CITIES_JPA_XPL =
			"axiom city (name, altitude): resource \"cities\";\n" +
			"template high_city(string name, altitude ? altitude > 5000);\n"
			;

	static final String CURRENCY_XPL =
			"axiom item() {\"$1234.56\"};\n" +
			"template charge(currency $ \"AU\" amount);\n" +
	        "calc charge_plus_gst(currency $ \"AU\" total = charge.amount * 1.1);\n" +
	        "calc format_total(string total_text = \"Total + gst: \" + charge_plus_gst.total.format);\n" +
			"query item_query(item : charge) -> (charge_plus_gst) -> (format_total);";
	
	static final String WORLD_CURRENCY_XPL =
			"include \"world_currency.xpl\";\n" +
			"template charge(currency $ country amount);\n" +
	        "calc charge_plus_gst(currency $ charge.country total = charge.amount * 1.1);\n" +
	        "calc format_total(string total_text = charge.country + \" Total + gst: \" + charge_plus_gst.total.format);\n" +
	        "list world_list(format_total);\n" +
			"query price_query(price : charge) -> (charge_plus_gst) -> (format_total);";
  			
	static final String STAMP_DUTY_XPL =
			"choice bracket "
			+ "(amount,       threshold,  base,    percent)\n" +
			"  {amount <  12000,      0,     0.00, 1.00}\n" +
			"  {amount <  30000,  12000,   120.00, 2.00}\n" +
			"  {amount <  50000,  30000,   480.00, 3.00}\n" +
			"  {amount < 100000,  50000,  1080.00, 3.50}\n" +
			"  {amount < 200000, 100000,  2830.00, 4.00}\n" +
			"  {amount < 250000, 200000,  6830.00, 4.25}\n" +
			"  {amount < 300000, 250000,  8955.00, 4.75}\n" +
			"  {amount < 500000, 300000, 11330.00, 5.00}\n" +
			"  {amount > 500000, 500000, 21330.00, 5.50};\n" +
			"\n" +
			"axiom transacton_amount (amount) { 123458 };\n" +
			"calc payable(duty = bracket.base + (amount - bracket.threshold) * (bracket.percent / 100));\n" +
			"query stamp_duty_query (transacton_amount : bracket) -> (payable);\n";

    static final String CHOICE_COLORS =
            "choice swatch\n" +
            "(name, red, green, blue)\n" +
            "{\"aqua\",  0, 255, 255}\n" +
            "{\"black\", 0, 0, 0}\n" +
            "{\"blue\",  0, 0, 255}\n" +
            "{\"white\", 255, 255, 255};\n" +
            "axiom shade (name) : parameter;\n" +
            "query color_query (shade : swatch);\n";
            ;

    static final String CHOICE_COLORS2 =
            "choice swatch\n" +
            "(rgb, color, red, green, blue)\n" +
            "{0x00FFFF, \"aqua\", 0, 255, 255}\n" +
            "{0x000000, \"black\", 0, 0, 0}\n" +
            "{0x0000FF, \"blue\", 0, 0, 255}\n" +
            "{0xFFFFFF, \"white\", 255, 255, 255};\n" +
            "axiom shade (rgb) : parameter;\n" +
            "query color_query (shade : swatch);\n";
            ;

    static final String CHOICE_COLORS3 =
             "choice swatch\n" +
            "(rgb, color, red, green, blue)\n" +
            "{0x00FFFF, \"aqua\", 0, 255, 255}\n" +
            "{0x000000, \"black\", 0, 0, 0}\n" +
            "{0x0000FF, \"blue\", 0, 0, 255}\n" +
            "{0xFFFFFF, \"white\", 255, 255, 255}\n" +
            "{unknown,  \"unknown\", 0, 0, 0};\n" +
            "axiom shade (rgb) : parameter;\n" +
            "query color_query (shade : swatch);\n";
            ;

    static final String MEGA_CITY3 = 
            "include \"mega_city.xpl\";\n" +
            "choice population_group\n" +
            "(Population,              Group)\n" +
            "{Population >= \"30,000,000\", \"Mega\"}\n" +
            "{Population >= \"20,000,000\", \"Huge\"}\n" +
            "{Population <  \"20,000,000\", \"Large\"};\n" +
            "list city_group_list(population_group);\n" +
            "query group_query (mega_city:population_group);";

            /** Named query to find all cities */
    static public final String ALL_CITIES = "all_cities";

    static String DIVIDE_TEST =
        "calc divide ( integer i = 35, integer j = 7, integer result = i/j );\n " +
        "query<term> divide_query (divide);";
    
    static String LOOP_TEST =
            "axiom test ( x) { 1 } { 2 } { 3 } { 4 };\n" +
            "list test(test);\n" +
            "calc loop ( i = 0, integer k = 0, { j = 0, { system.print(test[i + j].x), : ++j == 2 }, : (i += 2) == 4, : (k += 2) == 4 } );\n " +
            "calc loop1 ( i = 0, integer k = 0, { system.print(test[i].x), : ++i == 3, : ++k == 3 } );\n " +
            "query<term> loop_query (loop);\n" +
            "query<term> loop1_query (loop1);";

    @Test
    public void test_loop()
    {
        FunctionManager functionManager = new FunctionManager();
        SystemFunctionProvider systemFunctionProvider = new SystemFunctionProvider();
        functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
        QueryProgram queryProgram = new QueryProgram(functionManager);
        //queryProgram.setExecutionContext(new ExecutionContext());
        queryProgram.parseScript(LOOP_TEST);
        Result result = queryProgram.executeQuery("loop1_query");
        System.out.println(result.getAxiom("loop1_query").toString());
        System.out.println("Dual inner loop test");
        result = queryProgram.executeQuery("loop_query");
        System.out.println(result.getAxiom("loop_query").toString());
    }
    
   @Test
    public void test_divide()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(DIVIDE_TEST);
        Result result = queryProgram.executeQuery("divide_query");
        System.out.println(result.getAxiom("divide_query").toString());
    }

    @Ignore // Under development
    @Test
    public void test_sudoku() throws IOException
    {
        FunctionManager functionManager = new FunctionManager();
        SystemFunctionProvider systemFunctionProvider = new SystemFunctionProvider();
        functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
        File resourcePath = new File("src/test/resources");
        QueryProgramParser queryProgramParser = new QueryProgramParser(resourcePath, functionManager);
        QueryProgram queryProgram = queryProgramParser.loadScript("sudoku.xpl");
        //queryProgram.setExecutionContext(new ExecutionContext());
        Result result = queryProgram.executeQuery("sudoku");
        System.out.println(result.getAxiom("puzzle").toString());
    }
    
    @Test
    public void test_towers_of_hanoi() throws IOException
    {
        File resourcePath = new File("src/test/resources");
        FunctionManager functionManager = new FunctionManager();
        SystemFunctionProvider systemFunctionProvider = new SystemFunctionProvider();
        functionManager.putFunctionProvider(systemFunctionProvider.getName(), systemFunctionProvider);
        QueryProgramParser queryProgramParser = new QueryProgramParser(resourcePath, functionManager);
        QueryProgram queryProgram = queryProgramParser.loadScript("towers-of-hanoi.xpl");
        //queryProgram.setExecutionContext(new ExecutionContext());
        //queryProgram.executeQuery("towers_of_hanoi");
        Result result = queryProgram.executeQuery("towers_of_hanoi");
        Iterator<Axiom> iterator = result.axiomIterator("moves");
        File towersList = new File("src/test/resources", "towers-of-hanoi.lst");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(towersList), "UTF-8"));
        while (iterator.hasNext())
        {
            //System.out.println(iterator.next().toString());
            String line = reader.readLine();
            assertThat(iterator.next().toString()).isEqualTo(line);
        }
        reader.close();
    }
    
    @Test
    public void test_mega_cities3() throws IOException
    {
        QueryProgram queryProgram = new QueryProgram(provideProviderManager());
        //queryProgram.setExecutionContext(new ExecutionContext());
        queryProgram.parseScript(MEGA_CITY3);
        //queryProgram.executeQuery("group_query", new SolutionHandler(){
       //   @Override
       //   public boolean onSolution(Solution solution) {
       //       System.out.println(solution.getAxiom("population_group").toString());
       //       return true;
       //   }});
        Result result = queryProgram.executeQuery("group_query");
        Iterator<Axiom> iterator = result.axiomIterator(QualifiedName.parseGlobalName("city_group_list"));
        File worldCurrencyList = new File("src/test/resources", "cities-group.lst");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(worldCurrencyList), "UTF-8"));
        while(iterator.hasNext())
        {
            //System.out.println(iterator.next().toString());
            String line = reader.readLine();
            assertThat(iterator.next().toString()).isEqualTo(line);
        }
        reader.close();
    }

    @Test
    public void test_choice_string_colors()
    {
        QueryProgram queryProgram = new QueryProgram();
        //queryProgram.setExecutionContext(new ExecutionContext());
        queryProgram.parseScript(CHOICE_COLORS);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", "aqua")));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                //System.out.println(solution.getAxiom("swatch").toString());
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(name=aqua, red=0, green=255, blue=255, swatch=0)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", "blue")));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(name=blue, red=0, green=0, blue=255, swatch=2)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        // Test choice short circuit on no match
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("name", "orange")));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").getTermCount()).isEqualTo(0);
                return true;
            }});
        queryProgram.executeQuery(queryParams);
    }
    

    @Test
    public void test_choice_hex_colors()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(CHOICE_COLORS2);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.decode("0x00ffff"))));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                //System.out.println(solution.getAxiom("swatch").toString());
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(rgb=" + Long.decode("0x00ffff").toString() + ", color=aqua, red=0, green=255, blue=255, swatch=0)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.decode("0x0000ff"))));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(rgb=255, color=blue, red=0, green=0, blue=255, swatch=2)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        // Test choice short circuit on no match
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.decode("0x77ffff"))));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").getTermCount()).isEqualTo(0);
                return true;
            }});
        queryProgram.executeQuery(queryParams);
    }
    
    @Test
    public void test_choice_unknown_hex_color()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(CHOICE_COLORS3);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        QueryParams queryParams = queryProgram.getQueryParams(QueryProgram.GLOBAL_SCOPE, "color_query");
        // Add a shade Axiom with a single "aqua" term
        // This axiom goes into the Global scope and is removed at the start of the next query.
        Solution initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.decode("0x00ffff"))));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                //System.out.println(solution.getAxiom("swatch").toString());
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(rgb=" + Long.decode("0x00ffff").toString() + ", color=aqua, red=0, green=255, blue=255, swatch=0)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.decode("0x0000ff"))));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(rgb=255, color=blue, red=0, green=0, blue=255, swatch=2)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
        // Test default choice on no match
        initialSolution = queryParams.getInitialSolution();
        initialSolution.put("shade", new Axiom("shade", new Parameter("rgb", Long.decode("0x77ffff"))));
        queryParams.setSolutionHandler(new SolutionHandler(){
            @Override
            public boolean onSolution(Solution solution) {
                assertThat(solution.getAxiom("swatch").toString()).isEqualTo("swatch(rgb=" + Long.decode("0x77ffff").toString() + ", color=unknown, red=0, green=0, blue=0, swatch=4)");
                return true;
            }});
        queryProgram.executeQuery(queryParams);
    }
    
   @Test
    public void test_stamp_duty()
    {
		QueryProgram queryProgram = new QueryProgram();
        //queryProgram.setExecutionContext(new ExecutionContext());
		queryProgram.parseScript(STAMP_DUTY_XPL);
		queryProgram.executeQuery("stamp_duty_query", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("payable").toString());
				assertThat(solution.getAxiom("payable").toString()).isEqualTo("payable(duty=3768.32)");
				return true;
			}});
    }
 
    @Test
    public void test_world_currency_Format() throws IOException
    {
		QueryProgram queryProgram = new QueryProgram();
        queryProgram.setResourceBase(new File(JavaTestResourceEnvironment.DEFAULT_RESOURCE_LOCATION));
		queryProgram.parseScript(WORLD_CURRENCY_XPL);
		//queryProgram.executeQuery("price_query", new SolutionHandler(){
		//	@Override
		//	public boolean onSolution(Solution solution) {
		//		System.out.println(solution.getAxiom("format_total").toString());
		//		return true;
		//	}});
		Result result = queryProgram.executeQuery("price_query");
		Iterator<Axiom> iterator = result.axiomIterator(QualifiedName.parseGlobalName("world_list"));
    	File worldCurrencyList = new File("src/test/resources", "world_currency.lst");
     	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(worldCurrencyList), "UTF-8"));
        while(iterator.hasNext())
        {
            String text = iterator.next().toString();
		    //System.out.println(text);
 	    	String line = reader.readLine();
  	    	assertThat(text).isEqualTo(line);
        }
 	    reader.close();
    }
    
    @Test
    public void test_currency_Format()
    {
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.parseScript(CURRENCY_XPL);
		queryProgram.executeQuery("item_query", new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				//System.out.println(solution.getAxiom("format_total").toString());
				assertThat(solution.getAxiom("format_total").toString()).isEqualTo("format_total(total_text=Total + gst: AUD1,358.02)");
				return true;
			}});
    }
    
	@SuppressWarnings("unchecked")
    @Test
	public void test_high_cities_sorted() throws Exception
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.parseScript(CITY_EVELATIONS_SORTED);
		ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
		QuerySpec querySpec = new QuerySpec("Test", true);
		KeyName keyName1 = new KeyName("city", "high_city");
		querySpec.addKeyName(keyName1);
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("insert_sort");
        QueryParams queryParams = new QueryParams(queryProgram.getGlobalScope(), querySpec);
        queryParams.initialize();
        LogicQueryExecuter highCitiesQuery = new LogicQueryExecuter(queryParams);
        //highCitiesQuery.setExecutionContext(new ExecutionContext());
	    highCitiesQuery.chainCalculator(null, calcTemplate, null);
	    //System.out.println(highCitiesQuery.toString());
    	assertThat(highCitiesQuery.toString()).isEqualTo("high_city(name, altitude>5000?altitude)");
 	    while (highCitiesQuery.execute())
 	    {
 	        //Axiom axiom = calcTemplate.toAxiom();
            //System.out.println(axiom.toString());
 	    	//System.out.println(highCitiesQuery.toString());
 	    	//System.out.println(parserAssembler.getOperandMap().getItemList(QualifiedName.parseGlobalName("city_list")).toString());
 	    	//System.out.println();
 	    }
 	    ItemList<AxiomTermList> cityList = (ItemList<AxiomTermList>) parserAssembler.getListAssembler().getItemList(QualifiedName.parseGlobalName("city_list"));
 	    assertThat(cityList.getItem(0).getAxiom().toString()).isEqualTo("high_city(name=denver, altitude=5280)");
 	    assertThat(cityList.getItem(1).getAxiom().toString()).isEqualTo("high_city(name=flagstaff, altitude=6970)");
 	    assertThat(cityList.getItem(2).getAxiom().toString()).isEqualTo("high_city(name=addis ababa, altitude=8000)");
 	    assertThat(cityList.getItem(3).getAxiom().toString()).isEqualTo("high_city(name=leadville, altitude=10200)");
 	  	}

    @Test
    public void test_insert_sort() throws ParseException
    {
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.parseScript(INSERT_SORT_XPL);
		ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
		TestChainQueryExecuter queryExecuter = new TestChainQueryExecuter(new QueryParams(queryProgram.getGlobalScope(), new QuerySpec("test", true)));
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("insert_sort");
		queryExecuter.chainCalculator( null, calcTemplate, null);
		queryExecuter.setSolution(new Solution());
		queryExecuter.execute();
		Axiom unsortedAxiom = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("unsorted")).iterator().next();
		assertThat(queryExecuter.getSolution().getString("insert_sort", "i")).isEqualTo("5");
		assertThat(unsortedAxiom.toString()).isEqualTo("unsorted(1, 3, 5, 8, 12)");
    }
    
    @Test
    public void test_simple_calculate() throws ParseException
    {
		ParserAssembler parserAssembler = openScript(SIMPLE_CALCULATE);
		assertThat(parserAssembler.getTemplateAssembler().getTemplate("increment_n").toString()).isEqualTo("increment_n(n, limit, increment_n1(?++n<limit))");
		//System.out.println(parserAssembler.getTemplateAssembler().getTemplate("increment_n"));
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("increment_n");
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate, mock(ExecutionContext.class));
        assertThat(solution.getAxiom("increment_n").toString()).isEqualTo("increment_n(n=3, limit=3)");
    }
    
    @Test
    public void test_simple_list_calculate() throws ParseException
    {
		ParserAssembler parserAssembler = openScript(SIMPLE_LIST_CALCULATE);
		assertThat(parserAssembler.getTemplateAssembler().getTemplate("increment_n")
			.toString()).isEqualTo(
				"increment_n(n, limit, increment_n1(number_list.0=n++ ... ?number_list.2<limit))");
		//System.out.println(parserAssembler.getTemplateAssembler().getTemplate("increment_n"));
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("increment_n");
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate, mock(ExecutionContext.class));
        //System.out.println(solution.getAxiom("increment_n").toString());
        assertThat(solution.getAxiom("increment_n").toString()).isEqualTo("increment_n(n=4, limit=3)");
    }
    
    @Test
    public void test_simple_variable_index_list_calculate() throws ParseException
    {
		ParserAssembler parserAssembler = openScript(SIMPLE_VARIABLE_INDEX_LIST_CALCULATE);
		assertThat(parserAssembler.getTemplateAssembler().getTemplate("increment_n").toString()).isEqualTo("increment_n(n, i, limit, increment_n1(i=n++ ... ?i<limit))");
		//System.out.println(parserAssembler.getTemplateAssembler().getTemplate("increment_n"));
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("increment_n");
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate, mock(ExecutionContext.class));
        //System.out.println(solution.getAxiom("increment_n").toString());
        assertThat(solution.getAxiom("increment_n").toString()).isEqualTo("increment_n(n=4, i=3, limit=3)");
    }
        
    @Test
    public void test_simple_list_length_calculate() throws ParseException
    {
		ParserAssembler parserAssembler = openScript(SIMPLE_LIST_LENGTH_CALCULATE);
		assertThat(parserAssembler.getTemplateAssembler().getTemplate("increment_n")
				.toString()).isEqualTo(
						"increment_n(n, i, limit, increment_n1(i=n++ ... ?number_list_length<limit))");
		//System.out.println(parserAssembler.getTemplateAssembler().getTemplate("increment_n"));
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("increment_n");
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate, mock(ExecutionContext.class));
        //System.out.println(solution.getAxiom("increment_n").toString());
        assertThat(solution.getAxiom("increment_n").toString()).isEqualTo("increment_n(n=4, i=3, limit=3)");
    }
    
    @Test
    public void test_factorial_calculate() throws ParseException
    {
		ParserAssembler parserAssembler = openScript(FACTORIAL_CALCULATE);
		assertThat(parserAssembler.getTemplateAssembler().getTemplate("factorial").toString()).isEqualTo("factorial(i, n, factorial, factorial1(factorial*=i ... ?i++<n))");
		//System.out.println(parserAssembler.getTemplateAssembler().getTemplate("factorial").toString());
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("factorial");
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate, mock(ExecutionContext.class));
        //System.out.println(solution.getAxiom("factorial").toString());
        assertThat(solution.getAxiom("factorial").toString()).isEqualTo("factorial(i=5, n=4, factorial=24)");
    }
    
    @Test
    public void test_one_shot_calculate() throws ParseException
    {
		ParserAssembler parserAssembler = openScript(ONE_SHOT_CALCULATE);
		assertThat(parserAssembler.getTemplateAssembler().getTemplate("km2_to_mi2").toString()).isEqualTo("km2_to_mi2(km2, mi2, mi2=km2*0.3861)");
		//System.out.println(parserAssembler.getTemplateAssembler().getTemplate("km2_to_mi2"));
        Template calcTemplate = parserAssembler.getTemplateAssembler().getTemplate("km2_to_mi2");
        Solution solution = new Solution();
        Calculator calculator = new Calculator();
        calculator.iterate(solution, calcTemplate, mock(ExecutionContext.class));
        //System.out.println(solution.getAxiom("km2_to_mi2").toString());
        assertThat(solution.getAxiom("km2_to_mi2").toString()).isEqualTo("km2_to_mi2(km2=1323.98, mi2=511.188678, mi20=511.188678)");
    }
    

    @Test
	public void testGlobalVariables() throws Exception
	{
		ParserAssembler parserAssembler = openScript(SCRIPT1);
		OperandMap operandMap = parserAssembler.getOperandMap();
        IntegerOperand twoFlip = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("twoFlip"));
        twoFlip.evaluate(1);
	    assertThat(twoFlip.getValue()).isEqualTo(-3);
        IntegerOperand mask = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("mask"));
        mask.evaluate(1);
        IntegerOperand maskFlip = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("maskFlip"));
        maskFlip.evaluate(1);
	    assertThat(maskFlip.getValue()).isEqualTo(-3);
	}
	
	@Test
	public void testTemplates() throws Exception
	{
	    openScript(SCRIPT2);
	}
	
	@Test
	public void testAxioms() throws Exception
	{
	    openScript(SCRIPT3);
	}


	@Test 
	public void testBinaryOps() throws Exception
	{
	    ParserAssembler parserAssembler = openScript(SCRIPT4);
		OperandMap operandMap = parserAssembler.getOperandMap();
        IntegerOperand x = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("x"));
        x.evaluate(1);
	    assertThat(x.getValue()).isEqualTo(1);
        IntegerOperand y = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("y"));
        y.evaluate(1);
	    assertThat(y.getValue()).isEqualTo(2);
        IntegerOperand x_y = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("x_y"));
        x_y.evaluate(1);
	    assertThat(x_y.getValue()).isEqualTo(3);
        IntegerOperand a = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("a"));
        a.evaluate(1);
	    assertThat(a.getValue()).isEqualTo(2);
        IntegerOperand b = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("b"));
        b.evaluate(1);
	    assertThat(b.getValue()).isEqualTo(14);
        BooleanOperand c = (BooleanOperand) operandMap.get(QualifiedName.parseGlobalName("c"));
        c.evaluate(1);
	    assertThat(c.getValue()).isTrue();
        BigDecimalOperand d = (BigDecimalOperand) operandMap.get(QualifiedName.parseGlobalName("d"));
        d.evaluate(1);
	    assertThat(d.getValue()).isEqualTo(BigDecimal.valueOf((long)1234));
        BigDecimalOperand e = (BigDecimalOperand) operandMap.get(QualifiedName.parseGlobalName("e"));
        e.evaluate(1);
	    assertThat(e.getValue()).isEqualTo(BigDecimal.valueOf((long)1248));
	}

	/*
	    "integer x = y;" +
	    "integer z = 4 + (y = 6);" +
	    "integer a = (y = (x + z));";

	 */
	@Test
	public void test_assign() throws Exception
	{
	    ParserAssembler parserAssembler = openScript(SCRIPT5);
		OperandMap operandMap = parserAssembler.getOperandMap();
        IntegerOperand x = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("x"));
        x.evaluate(1);
	    assertThat(x.toString()).isEqualTo("x=y");
        IntegerOperand z = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("z"));
        z.evaluate(1);
	    assertThat(z.toString()).isEqualTo("z=10");
        Operand y = operandMap.get(QualifiedName.parseGlobalName("y"));
	    assertThat(y.toString()).isEqualTo("y=6");
        IntegerOperand a = (IntegerOperand) operandMap.get(QualifiedName.parseGlobalName("a"));
        a.evaluate(1);
	    assertThat(a.toString()).isEqualTo("a=16");
	    assertThat(y.toString()).isEqualTo("y=16");
	}
	
	@Test
	public void test_highCities() throws Exception
	{
		QueryProgram queryProgram = new QueryProgram(provideProviderManager());
		queryProgram.parseScript(CITY_EVELATIONS);
	    ParserAssembler parserAssembler = queryProgram.getGlobalScope().getParserAssembler();
	    Template highCities = parserAssembler.getTemplateAssembler().getTemplate("high_city");
	    highCities.setKey("city");
        QuerySpec querySpec = new QuerySpec("TEST", true);
		KeyName keyName = new KeyName("city", "high_city");
		querySpec.addKeyName(keyName);
        QueryParams queryParams = new QueryParams(queryProgram.getGlobalScope(), querySpec);
        queryParams.initialize();
        LogicQueryExecuter highCitiesQuery = new LogicQueryExecuter(queryParams);
    	assertThat(highCitiesQuery.toString()).isEqualTo("high_city(name, altitude, is_high=altitude>5000)");
    	int index = 0;
 	    while (highCitiesQuery.execute())
  	    	assertThat(highCitiesQuery.toString()).isEqualTo(HIGH_CITY_v2[index++]);
	}

	@Test
	public void test_colors() throws Exception
	{
		QueryProgram queryProgram = new QueryProgram();
		queryProgram.parseScript(AXIOM_WRAPPER_XPL);
		QuerySpec querySpec = new QuerySpec("test", true);
		KeyName keyName = new KeyName("colors", "color_convert");
		querySpec.addKeyName(keyName);
        QueryParams queryParams = new QueryParams(queryProgram.getGlobalScope(), querySpec);
        queryParams.initialize();
        TestQueryExecuter colorsQuery = new TestQueryExecuter(queryParams);
 	    assertThat(colorsQuery.execute()).isTrue();
  	    //System.out.println(colorsQuery.getSolution().getAxiom("color_convert").toString());
 	    assertThat(colorsQuery.getSolution().getAxiom("color_convert").toString()).isEqualTo("color_convert(red=0.75, green=0.5, blue=0.25, r=0.75, g=0.5, b=0.25)");
	}

	@Test
	public void test_GreekBusiness() throws Exception
	{
	    final ParserAssembler parserAssembler = openScript(GREEK_BUSINESS);
	    Template charge = parserAssembler.getTemplateAssembler().getTemplate("charge");
	    Template customer = parserAssembler.getTemplateAssembler().getTemplate("customer");
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(customer);
	    templateList.add(charge);
	    AxiomCollection ensemble = new AxiomCollection(){

			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if (name.equals("charge"))
					return parserAssembler.getAxiomSource(QualifiedName.parseGlobalName(name));
				else if (name.equals("customer"))
				    return parserAssembler.getAxiomSource(QualifiedName.parseGlobalName(name));

				return null;
			}

			@Override
			public boolean isEmpty() 
			{
				return false;
			}};
	    QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
	    LogicQueryExecuter greekChargeCustomerQuery = new LogicQueryExecuter(adapter.getQueryParams());
	    assertThat(greekChargeCustomerQuery.toString()).isEqualTo("customer(name, city), charge(city==city?city, fee)");
    	int index = 0;
 	    while (greekChargeCustomerQuery.execute())
 	        //System.out.println(greekChargeCustomerQuery.toString());
  	    	assertThat(greekChargeCustomerQuery.toString()).isEqualTo(GREEK_BUSINESS_LIST[index++]);
	}
	
    @Test
    public void test_GreekBusiness2() throws Exception
    {
        final ParserAssembler parserAssembler = openScript(GREEK_BUSINESS2);
        Template charge = parserAssembler.getTemplateAssembler().getTemplate("charge");
        Template customer = parserAssembler.getTemplateAssembler().getTemplate("customer");
        List<Template> templateList = new ArrayList<Template>();
        templateList.add(charge);
        templateList.add(customer);
        AxiomCollection ensemble = new AxiomCollection(){

            @Override
            public AxiomSource getAxiomSource(String name) 
            {
                if (name.equals("charge"))
                    return parserAssembler.getAxiomSource(QualifiedName.parseGlobalName(name));
                else if (name.equals("customer"))
                    return parserAssembler.getAxiomSource(QualifiedName.parseGlobalName(name));

                return null;
            }

            @Override
            public boolean isEmpty() 
            {
                return false;
            }};
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
        LogicQueryExecuter greekChargeCustomerQuery = new LogicQueryExecuter(adapter.getQueryParams());
        assertThat(greekChargeCustomerQuery.toString()).isEqualTo("charge(city, fee), customer(name, city==city?city)");
        int index = 0;
        while (greekChargeCustomerQuery.execute())
            //System.out.println(greekChargeCustomerQuery.toString());
            assertThat(greekChargeCustomerQuery.toString()).isEqualTo(GREEK_BUSINESS_LIST2[index++]);
    }
    
	@Test
	public void test_NamedGreekBusiness() throws Exception
	{
	    final ParserAssembler parserAssembler = openScript(NAMED_GREEK_BUSINESS);
	    Template charge = parserAssembler.getTemplateAssembler().getTemplate("charge");
	    Template customer = parserAssembler.getTemplateAssembler().getTemplate("customer");
	    List<Template> templateList = new ArrayList<Template>();
	    templateList.add(customer);
	    templateList.add(charge);
	    AxiomCollection ensemble = new AxiomCollection(){

			@Override
			public AxiomSource getAxiomSource(String name) 
			{
				if (name.equals("charge"))
					return parserAssembler.getAxiomSource(QualifiedName.parseGlobalName(name));
				else if (name.equals("customer"))
				    return parserAssembler.getAxiomSource(QualifiedName.parseGlobalName(name));

				return null;
			}

			@Override
			public boolean isEmpty() 
			{
				return false;
			}};
	    QueryExecuterAdapter adapter = new QueryExecuterAdapter(ensemble, templateList);
	    LogicQueryExecuter greekChargeCustomerQuery = new LogicQueryExecuter(adapter.getQueryParams());
	    assertThat(greekChargeCustomerQuery.toString()).isEqualTo("customer(name, city), charge(city==city?city, fee)");
    	int index = 0;
 	    while (greekChargeCustomerQuery.execute())
 	        //System.out.println(greekChargeCustomerQuery.toString());
  	    	assertThat(greekChargeCustomerQuery.toString()).isEqualTo(GREEK_BUSINESS_LIST[index++]);
	}
 
	@Test
	public void testAgriculturalLand() throws Exception
	{
		ParserAssembler parserAssembler = openScript(AGRICULTURAL_LAND);
	    Template more_agriculture_y1990_y2010 = parserAssembler.getTemplateAssembler().getTemplate("agri_10y");
	    more_agriculture_y1990_y2010.setKey("Data");
	    AxiomSource agriSource = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("Data"));
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(agriSource, Collections.singletonList(more_agriculture_y1990_y2010));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        TestQueryExecuter agriculturalQuery = new TestQueryExecuter(queryParams);
    	assertThat(agriculturalQuery.toString()).isEqualTo("agri_10y(Y1990, Y2010, Y2010-Y1990>1.0?country)");
    	File agriList = new File("src/test/resources", "agriculture-land.lst");
     	LineNumberReader reader = new LineNumberReader(new FileReader(agriList));
        while (agriculturalQuery.execute())
 	    {
 	    	String line = reader.readLine();
            //System.out.println(agriculturalQuery.toString());
  	    	assertThat(agriculturalQuery.toString()).isEqualTo(line);
 	    }
 	    reader.close();
 	    
        QueryExecuterAdapter adapter2 = new QueryExecuterAdapter(agriSource, Collections.singletonList(more_agriculture_y1990_y2010));
        QueryParams queryParams2 = new QueryParams(adapter2.getScope(), adapter2.getQuerySpec());
        queryParams2.initialize();
 	    File surfaceAreaList = new File("src/test/resources", "surface-area.lst");
  	    final LineNumberReader reader2 = new LineNumberReader(new FileReader(surfaceAreaList));
  	    SolutionHandler solutionHandler = new SolutionHandler(){

			@Override
			public boolean onSolution(Solution solution) {
	 	    	String line = null;
				try {
					line = reader2.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 	    	//System.out.println(solution.getAxiom("surface_area_increase").toString());
	  	    	assertThat(solution.getAxiom("surface_area_increase").toString()).isEqualTo(line);
				return true;
			}};

 	    agriculturalQuery = new TestQueryExecuter(queryParams2);
	    more_agriculture_y1990_y2010.backup(false);
	    Template surface_area = parserAssembler.getTemplateAssembler().getTemplate("surface_area_increase");
	    surface_area.setKey("surface_area");
	    agriculturalQuery.chain(QueryExecuterAdapter.ensembleFromSource(parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("surface_area"))), surface_area, null);
		while (agriculturalQuery.execute())
		{
			if (!solutionHandler.onSolution(agriculturalQuery.getSolution()))
				break;
		}
	    reader2.close();
 	    Parameter[] dataParms = new Parameter[] 
 	    { 
 	    	new Parameter("country", "Kosovo"), 
 	    	new Parameter("Y1990", Double.valueOf(43.50d)), 
 	    	new Parameter("Y2009", Double.valueOf(48.63d)) 
 	    };
 	    more_agriculture_y1990_y2010.backup(false);
        QueryExecuterAdapter adapter3 = new QueryExecuterAdapter(new SingleAxiomSource(new Axiom("Data", dataParms)), Collections.singletonList(more_agriculture_y1990_y2010));
 	    QueryParams queryParams3 = new QueryParams(adapter3.getScope(), adapter3.getQuerySpec());
 	    queryParams3.initialize();
        agriculturalQuery = new TestQueryExecuter(queryParams3);
 	    try
 	    {
 	    	agriculturalQuery.execute();
 	    	//System.out.println(agriculturalQuery);
 	    	failBecauseExceptionWasNotThrown(QueryExecutionException.class);
 	    }
 	    catch(QueryExecutionException e)
 	    {
 	    	assertThat(e.getMessage()).isEqualTo("Error evaluating: agri_10y(Y1990=43.5, Y2010, Y2010-43.5>1.0?Kosovo)");
 	    	assertThat(e.getCause().getMessage()).isEqualTo("Left term is empty");
 	    }
	}
	
	@Test
	public void test_regex() throws ParseException, IOException
	{
		ParserAssembler parserAssembler = openScript(LEXICAL_SEARCH);
		Template inWordsTemplate = parserAssembler.getTemplateAssembler().getTemplate("in_words");
		inWordsTemplate.setKey("Lexicon");
		assertThat(inWordsTemplate.toString()).isEqualTo("in_words(Word \\^in[^ ]+\\?Word, Definition)");
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(new LexiconSource(), Collections.singletonList(inWordsTemplate));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        LogicQueryExecuter inWordsQuery = new LogicQueryExecuter(queryParams);
    	File inWordList = new File("src/test/resources", "in_words.lst");
     	LineNumberReader reader = new LineNumberReader(new FileReader(inWordList));
		while (inWordsQuery.execute())
		{
 	 	    String line = reader.readLine();
			assertThat(inWordsQuery.toString()).isEqualTo(line);
		}
		reader.close();
	}

	@Test
	public void test_regex_groups() throws ParseException, IOException
	{
		ParserAssembler parserAssembler = openScript(REGEX_GROUPS);
		//
		Template dictionaryTemplate = parserAssembler.getTemplateAssembler().getTemplate("dictionary");
		dictionaryTemplate.setKey("Lexicon");
		LexiconSource lexiconSource = new LexiconSource();
	    QueryExecuterAdapter adapter = new QueryExecuterAdapter(lexiconSource, Collections.singletonList(dictionaryTemplate));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        LogicQueryExecuter dictionaryQuery = new LogicQueryExecuter(queryParams);
		int count = 0;
		if (dictionaryQuery.execute())
		{
			++count;
			assertThat(dictionaryQuery.toString()).isEqualTo("dictionary(word=abbey, part=n, text=a monastery ruled by an abbot, definition=n. a monastery ruled by an abbot)");
			//System.out.println(dictionaryQuery.toString());
			if (dictionaryQuery.execute())
			{
				++count;
				assertThat(dictionaryQuery.toString()).isEqualTo("dictionary(word=abide, part=v, text=dwell; inhabit or live in, definition=v. dwell; inhabit or live in)");
				//System.out.println(dictionaryQuery.toString());
				while(dictionaryQuery.execute())
				{
					
				}
			}
		}
		assertThat(count).isEqualTo(2);
	}
	
	@Test
	public void test_mega_city() throws ParseException, IOException
	{
		ParserAssembler parserAssembler = openScript(MEGA_CITY1);
		Template asia_top_ten = parserAssembler.getTemplateAssembler().getTemplate("asia_top_ten");
		asia_top_ten.setKey("mega_city");
	    AxiomSource megacitySource = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("mega_city"));
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(megacitySource, Collections.singletonList(asia_top_ten));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        LogicQueryExecuter asiaTopTenQuery = new LogicQueryExecuter(queryParams);
    	File megaCityList = new File("src/test/resources", "mega_city1.lst");
     	LineNumberReader reader = new LineNumberReader(new FileReader(megaCityList));
		while (asiaTopTenQuery.execute())
		{
 	 	    String line = reader.readLine();
			assertThat(asiaTopTenQuery.toString()).isEqualTo(line);
		}
		reader.close();
		
		parserAssembler = openScript(MEGA_CITY2);
		Template american_megacities = parserAssembler.getTemplateAssembler().getTemplate("america_megacities");
		american_megacities.setKey("mega_city");
		megacitySource = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("mega_city"));
        QueryExecuterAdapter adapter2 = new QueryExecuterAdapter(megacitySource, Collections.singletonList(american_megacities));
        QueryParams queryParams2 = new QueryParams(adapter2.getScope(), adapter2.getQuerySpec());
        queryParams2.initialize();
		LogicQueryExecuter americanMegacitiesQuery = new LogicQueryExecuter(queryParams2);
    	File americanCityList = new File("src/test/resources", "mega_city2.lst");
     	reader = new LineNumberReader(new FileReader(americanCityList));
		while (americanMegacitiesQuery.execute())
		{
 	 	    String line = reader.readLine();
			assertThat(americanMegacitiesQuery.toString()).isEqualTo(line);
		}
		reader.close();
	}
	
	
	@Test
	public void test_regex2() throws ParseException, IOException
	{
		ParserAssembler parserAssembler = openScript(NOUN_LEXICAL_SEARCH);
		Template inWordsTemplate = parserAssembler.getTemplateAssembler().getTemplate("in_words");
		inWordsTemplate.setKey("Lexicon");
		assertThat(inWordsTemplate.toString()).isEqualTo("in_words(Word \\^in[^ ]+\\?Word, Definition \\^n\\?Definition)");
        QueryExecuterAdapter adapter = new QueryExecuterAdapter(new LexiconSource(), Collections.singletonList(inWordsTemplate));
        QueryParams queryParams = new QueryParams(adapter.getScope(), adapter.getQuerySpec());
        queryParams.initialize();
        LogicQueryExecuter inWordsQuery = new LogicQueryExecuter(queryParams);
		while (inWordsQuery.execute())
		{
			assertThat(inWordsTemplate.getTermByName("Word").getValue().toString().startsWith("in")).isTrue();
			assertThat(inWordsTemplate.getTermByName("Definition").getValue().toString().startsWith("n.")).isTrue();
		}
	}

	@Test 
	public void test_birds() throws ParseException, IOException
	{
		Set<String> keywordSet = new TreeSet<String>();
		Set<String> orderSet = new TreeSet<String>();
		Set<String> familySet = new TreeSet<String>();
		Set<String> birdSet = new TreeSet<String>();
		Map<String, List<Axiom>> promptMap = new TreeMap<String, List<Axiom>>();
		ParserAssembler parserAssembler = openScript(BIRDS);
		AxiomSource orderSource = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("order"));
		Axiom orderAxiom1 = orderSource.iterator().next();
		for (int i = 0; i < orderAxiom1.getTermCount(); i++)
		{
			String keyword = orderAxiom1.getTermByIndex(i).getName();
			if (!"order".equals(keyword))
				orderSet.add(keyword);
			keywordSet.add(keyword);
		}
		AxiomSource familySource = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("family"));
		Axiom familyAxiom1 = familySource.iterator().next();
		for (int i = 0; i < familyAxiom1.getTermCount(); i++)
		{
			String keyword = familyAxiom1.getTermByIndex(i).getName();
			if (!("family".equals(keyword) || "order".equals(keyword)))
				familySet.add(keyword);
			keywordSet.add(keyword);
		}
		AxiomSource birdSource = parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("bird"));
		Axiom birdAxiom1 = birdSource.iterator().next();
		for (int i = 0; i < birdAxiom1.getTermCount(); i++)
		{
			String keyword = birdAxiom1.getTermByIndex(i).getName();
			if (!("bird".equals(keyword) || "family".equals(keyword) || "order".equals(keyword)))
				birdSet.add(keyword);
			keywordSet.add(keyword);
		}
		for (String keyword: keywordSet)
		{
			if (!"bird".equals(keyword))
			{
				promptMap.put(keyword, new ArrayList<Axiom>());
				if (orderSet.contains(keyword))
				{
					String templateName = "order_" + keyword;
					Template template = parserAssembler.getTemplateAssembler().getTemplate(templateName);
					template.setKey("order");
				    QueryExecuterAdapter adapter = new QueryExecuterAdapter(parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("order")), Collections.singletonList(template));
				    TestQueryExecuter orderQuery = new TestQueryExecuter(adapter.getQueryParams());
					while (orderQuery.execute())
						promptMap.get(keyword).add(orderQuery.getSolution().getAxiom(templateName));
				}
				if (familySet.contains(keyword))
				{
					String templateName = "family_" + keyword;
					Template template = parserAssembler.getTemplateAssembler().getTemplate(templateName);
					template.setKey("family");
				    QueryExecuterAdapter adapter = new QueryExecuterAdapter(parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("family")), Collections.singletonList(template));
				    TestQueryExecuter orderQuery = new TestQueryExecuter(adapter.getQueryParams());
					while (orderQuery.execute())
						promptMap.get(keyword).add(orderQuery.getSolution().getAxiom(templateName));
				}
				if (birdSet.contains(keyword))
				{
					String templateName = "bird_" + keyword;
					Template template = parserAssembler.getTemplateAssembler().getTemplate(templateName);
					template.setKey("bird");
				    QueryExecuterAdapter adapter = new QueryExecuterAdapter(parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("bird")), Collections.singletonList(template));
				    TestQueryExecuter orderQuery = new TestQueryExecuter(adapter.getQueryParams());
					while (orderQuery.execute())
						promptMap.get(keyword).add(orderQuery.getSolution().getAxiom(templateName));
				}
			}
		}
		int index = 0;
		for (Entry<String, List<Axiom>> entry: promptMap.entrySet())
		{
			List<Axiom> axiomList = entry.getValue();
			Set<String> promptSet = new TreeSet<String>();
			for (Axiom axiom: axiomList)
				promptSet.add(axiom.getTermByName(entry.getKey()).getValue().toString());
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
			//System.out.println();
			//System.out.println(builder.toString());
			assertThat(BIRD_PROMPTS[index++]).isEqualTo(builder.toString());
		}
		//System.out.println();
		BirdsResultsChecker birdsResultsChecker = new BirdsResultsChecker("bird_results.lst");
		for (String keyword: keywordSet)
		{
			listBirds(parserAssembler, promptMap, keyword, birdsResultsChecker);
		}
	}

	private void listBirds(ParserAssembler parserAssembler, Map<String, List<Axiom>> promptMap, String keyword, BirdsResultsChecker birdsResultsChecker)
	{
		List<Axiom> axiomList = promptMap.get(keyword);
		if (axiomList == null)
			return;
		for (Axiom axiom: axiomList)
		{
			String attribute = axiom.getTermByName(keyword).getValue().toString();
			if (("bird_" + keyword).equals(axiom.getName()))
				birdsResultsChecker.checkNextResult(axiom.getTermByName("bird").getValue().toString() + " " + keyword + " " + attribute);
			else if (("family_" + keyword).equals(axiom.getName()))
			{
		        TemplateArchetype birdArchetype = new TemplateArchetype(new QualifiedTemplateName(QualifiedName.EMPTY, "bird"));
				Template template = new Template(birdArchetype);
				template.addTerm(new StringOperand(QualifiedName.parseGlobalName("bird")));
				template.addTerm(new StringOperand(QualifiedName.parseGlobalName("family"), axiom.getTermByName("family").getValue().toString()));
			    QueryExecuterAdapter adapter = new QueryExecuterAdapter(parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("bird")), Collections.singletonList(template));
			    TestQueryExecuter query = new TestQueryExecuter(adapter.getQueryParams());
				while (query.execute())
					birdsResultsChecker.checkNextResult(query.getSolution().getString("bird", "bird") + " " + keyword + " " + attribute);
			}
			else
			{
                TemplateArchetype familyArchetype = new TemplateArchetype(new QualifiedTemplateName(QualifiedName.EMPTY, "family"));
				Template template = new Template(familyArchetype);
				template.addTerm(new StringOperand(QualifiedName.parseGlobalName("family")));
				template.addTerm(new StringOperand(QualifiedName.parseGlobalName("order"), axiom.getTermByName("order").getValue().toString()));
			    QueryExecuterAdapter adapter = new QueryExecuterAdapter(parserAssembler.getAxiomSource(QualifiedName.parseGlobalName("family")), Collections.singletonList(template));
			    TestQueryExecuter query = new TestQueryExecuter(adapter.getQueryParams());
				while (query.execute())
					birdsResultsChecker.checkNextResult("family " + query.getSolution().getString("family", "family") + " " + keyword + " " + attribute);
			}
		}
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
	
    ProviderManager provideProviderManager()
    {
        return new TestAxiomProvider();
    }
}
