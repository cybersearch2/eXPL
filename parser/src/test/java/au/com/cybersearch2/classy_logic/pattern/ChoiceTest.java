/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.pattern;

import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * ChoiceTest
 * @author Andrew Bowley
 * 6 Sep 2015
 */
public class ChoiceTest
{
    static final String CHOICE_COLORS =
        "axiom shades (name) {\"aqua\"} {\"blue\"} {\"orange\"};\n" +
        "calc shader(\n" +
        "  color = name,\n" +        
        "  choice swatch\n" +
        "  ( color,     red, green, blue)\n" +
        "  { \"aqua\",  0,   255,   255 }\n" +
        "  { \"black\", 0,   0,     0   }\n" +
        "  { \"blue\",  0,   0,     255 }\n" +
        "  { \"white\", 255, 255,   255 },\n" +
        "  axiom rgb = { red, green, blue }\n" +
        ");\n" +
        "query color_query (shades : shader);\n";

    static final String[] CHOICE_COLORS_LIST =
    {
        "rgb = rgb(red = 0, green = 255, blue = 255)",
        "rgb = rgb(red = 0, green = 0, blue = 255)",
        "rgb = rgb(red = null, green = null, blue = null)"
    };

    static final String STAMP_DUTY =
            "axiom transacton_amount (amount)\n" +
            "{123458.00}\n" +
            "{55876.33}\n" +
            "{1245890.00};\n" +
            "calc stamp_duty_payable(\n" +
            "  currency amount,\n" +
            "  choice bracket "
            +   "( amount,           threshold, base, percent)\n" +
            "    { amount <  12000,      0,     0.00, 1.00 }\n" +
            "    { amount <  30000,  12000,   120.00, 2.00 }\n" +
            "    { amount <  50000,  30000,   480.00, 3.00 }\n" +
            "    { amount < 100000,  50000,  1080.00, 3.50 }\n" +
            "    { amount < 200000, 100000,  2830.00, 4.00 }\n" +
            "    { amount < 250000, 200000,  6830.00, 4.25 }\n" +
            "    { amount < 300000, 250000,  8955.00, 4.75 }\n" +
            "    { amount < 500000, 300000, 11330.00, 5.00 }\n" +
            "    { amount > 500000, 500000, 21330.00, 5.50 },\n" +
            "\n" +
            "  currency duty = base + (amount - threshold) * (percent / 100),\n" +
            "  string display = format(duty)\n" +
            ");\n" +
            "query stamp_duty_query (transacton_amount : stamp_duty_payable);\n";

    static final String[] STAMP_DUTY_LIST =
    {
        "stamp_duty_payable(amount = 123458.0, bracket = true, duty = 3768.320, display = AUD3,768.32)",
        "stamp_duty_payable(amount = 55876.33, bracket = true, duty = 1285.67155, display = AUD1,285.67)",
        "stamp_duty_payable(amount = 1245890.0, bracket = true, duty = 62353.9500, display = AUD62,353.95)"
    };
    
    @Test
    public void test_stamp_duty()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(STAMP_DUTY);
        queryProgram.executeQuery("stamp_duty_query", new SolutionHandler(){
            int index = 0;
            @Override
            public boolean onSolution(Solution solution)
            {
                //System.out.println(solution.getAxiom("stamp_duty_payable").toString());
                assertThat(solution.getAxiom("stamp_duty_payable").toString()).isEqualTo(STAMP_DUTY_LIST[index++]);
                return true;
            }});
    }

    @Test
    public void test_choice_colors()
    {
        QueryProgram queryProgram = new QueryProgram();
        queryProgram.parseScript(CHOICE_COLORS);
        // Create QueryParams object for Global scope and query "stamp_duty_query"
        queryProgram.executeQuery("color_query", new SolutionHandler(){
            int index = 0;
            @Override
            public boolean onSolution(Solution solution) {
                //System.out.println(solution.getAxiom("shader").getTermByName("rgb").toString());
                assertThat(solution.getAxiom("shader").getTermByName("rgb").toString()).isEqualTo(CHOICE_COLORS_LIST[index++]);
                boolean success = index < 3;
                assertThat((Boolean)solution.getAxiom("shader").getTermByName("swatch").getValue()).isEqualTo(success);
               return true;
            }});
     }

}
