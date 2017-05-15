/* Generated By:JavaCC: Do not edit this line. QueryParserTokenManager.java */
package au.com.cybersearch2.classy_logic.parser;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.compile.AxiomAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserResources;
import au.com.cybersearch2.classy_logic.compile.Group;
import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.ParserTask;
import au.com.cybersearch2.classy_logic.compile.SourceMarker;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.compile.VariableType;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.DoubleOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.NullOperand;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.RegExOperand;
import au.com.cybersearch2.classy_logic.expression.MatchOperand;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.LoopEvaluator;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.expression.FormatterOperand;
import au.com.cybersearch2.classy_logic.expression.FactOperand;
import au.com.cybersearch2.classy_logic.expression.ChoiceOperand;
import au.com.cybersearch2.classy_logic.expression.LiteralListOperand;
import au.com.cybersearch2.classy_logic.expression.Orientation;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.list.AxiomContainerOperand;
import au.com.cybersearch2.classy_logic.list.ItemListOperand;
import au.com.cybersearch2.classy_logic.list.ListLength;
import au.com.cybersearch2.classy_logic.terms.StringTerm;
import au.com.cybersearch2.classy_logic.terms.IntegerTerm;
import au.com.cybersearch2.classy_logic.terms.DoubleTerm;
import au.com.cybersearch2.classy_logic.terms.BooleanTerm;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.terms.LiteralParameter;
import au.com.cybersearch2.classy_logic.terms.LiteralType;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AssignType;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.helper.OperandParam;

/** Token Manager. */
public class QueryParserTokenManager implements QueryParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1)
{
   switch (pos)
   {
      case 0:
         if ((active1 & 0x202L) != 0L)
            return 0;
         if ((active0 & 0x800000000000L) != 0L)
            return 10;
         if ((active0 & 0x3c0fffffc0L) != 0L)
         {
            jjmatchedKind = 38;
            return 21;
         }
         return -1;
      case 1:
         if ((active0 & 0x3c0fffffc0L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 1;
            return 21;
         }
         return -1;
      case 2:
         if ((active0 & 0x1c0fffffc0L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 2;
            return 21;
         }
         if ((active0 & 0x2000000000L) != 0L)
            return 21;
         return -1;
      case 3:
         if ((active0 & 0x180fd1ffc0L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 3;
            return 21;
         }
         if ((active0 & 0x4002e0000L) != 0L)
            return 21;
         return -1;
      case 4:
         if ((active0 & 0x80201b000L) != 0L)
            return 21;
         if ((active0 & 0x100dd04fc0L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 4;
            return 21;
         }
         return -1;
      case 5:
         if ((active0 & 0x5100480L) != 0L)
            return 21;
         if ((active0 & 0x1008c04b40L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 5;
            return 21;
         }
         return -1;
      case 6:
         if ((active0 & 0x8c00800L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 6;
            return 21;
         }
         if ((active0 & 0x1000004340L) != 0L)
            return 21;
         return -1;
      case 7:
         if ((active0 & 0xc00800L) != 0L)
            return 21;
         if ((active0 & 0x8000000L) != 0L)
         {
            jjmatchedKind = 38;
            jjmatchedPos = 7;
            return 21;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0, long active1)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         jjmatchedKind = 51;
         return jjMoveStringLiteralDfa1_0(0x200000000000000L, 0x0L);
      case 36:
         return jjStopAtPos(0, 80);
      case 37:
         jjmatchedKind = 69;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x2000L);
      case 38:
         jjmatchedKind = 66;
         return jjMoveStringLiteralDfa1_0(0x800000000000000L, 0x400L);
      case 40:
         return jjStopAtPos(0, 39);
      case 41:
         return jjStopAtPos(0, 40);
      case 42:
         jjmatchedKind = 64;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x100L);
      case 43:
         jjmatchedKind = 62;
         return jjMoveStringLiteralDfa1_0(0x1000000000000000L, 0x40L);
      case 44:
         return jjStopAtPos(0, 46);
      case 45:
         jjmatchedKind = 63;
         return jjMoveStringLiteralDfa1_0(0x2000000000000000L, 0x80L);
      case 46:
         return jjStartNfaWithStates_0(0, 47, 10);
      case 47:
         jjmatchedKind = 65;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x200L);
      case 58:
         return jjStopAtPos(0, 52);
      case 59:
         return jjStopAtPos(0, 45);
      case 60:
         jjmatchedKind = 50;
         return jjMoveStringLiteralDfa1_0(0x80000000000000L, 0x8000L);
      case 61:
         jjmatchedKind = 48;
         return jjMoveStringLiteralDfa1_0(0x40000000000000L, 0x0L);
      case 62:
         jjmatchedKind = 49;
         return jjMoveStringLiteralDfa1_0(0x100000000000000L, 0x4000L);
      case 63:
         return jjStopAtPos(0, 53);
      case 78:
         return jjMoveStringLiteralDfa1_0(0x2000000000L, 0x0L);
      case 91:
         return jjStopAtPos(0, 43);
      case 93:
         return jjStopAtPos(0, 44);
      case 94:
         jjmatchedKind = 68;
         return jjMoveStringLiteralDfa1_0(0x0L, 0x1000L);
      case 97:
         return jjMoveStringLiteralDfa1_0(0x1000L, 0x0L);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x200L, 0x0L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x4820000L, 0x0L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x180L, 0x0L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x801080000L, 0x0L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x4040L, 0x0L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x2140000L, 0x0L);
      case 112:
         return jjMoveStringLiteralDfa1_0(0x8000000L, 0x0L);
      case 113:
         return jjMoveStringLiteralDfa1_0(0x10000L, 0x0L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x402000L, 0x0L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x8400L, 0x0L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x400200800L, 0x0L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x1000000000L, 0x0L);
      case 123:
         return jjStopAtPos(0, 41);
      case 124:
         jjmatchedKind = 67;
         return jjMoveStringLiteralDfa1_0(0x400000000000000L, 0x800L);
      case 125:
         return jjStopAtPos(0, 42);
      case 126:
         return jjStopAtPos(0, 81);
      default :
         return jjMoveNfa_0(5, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0, long active1)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0, active1);
      return 1;
   }
   switch(curChar)
   {
      case 38:
         if ((active0 & 0x800000000000000L) != 0L)
            return jjStopAtPos(1, 59);
         break;
      case 43:
         if ((active0 & 0x1000000000000000L) != 0L)
            return jjStopAtPos(1, 60);
         break;
      case 45:
         if ((active0 & 0x2000000000000000L) != 0L)
            return jjStopAtPos(1, 61);
         break;
      case 60:
         if ((active1 & 0x8000L) != 0L)
            return jjStopAtPos(1, 79);
         break;
      case 61:
         if ((active0 & 0x40000000000000L) != 0L)
            return jjStopAtPos(1, 54);
         else if ((active0 & 0x80000000000000L) != 0L)
            return jjStopAtPos(1, 55);
         else if ((active0 & 0x100000000000000L) != 0L)
            return jjStopAtPos(1, 56);
         else if ((active0 & 0x200000000000000L) != 0L)
            return jjStopAtPos(1, 57);
         else if ((active1 & 0x40L) != 0L)
            return jjStopAtPos(1, 70);
         else if ((active1 & 0x80L) != 0L)
            return jjStopAtPos(1, 71);
         else if ((active1 & 0x100L) != 0L)
            return jjStopAtPos(1, 72);
         else if ((active1 & 0x200L) != 0L)
            return jjStopAtPos(1, 73);
         else if ((active1 & 0x400L) != 0L)
            return jjStopAtPos(1, 74);
         else if ((active1 & 0x800L) != 0L)
            return jjStopAtPos(1, 75);
         else if ((active1 & 0x1000L) != 0L)
            return jjStopAtPos(1, 76);
         else if ((active1 & 0x2000L) != 0L)
            return jjStopAtPos(1, 77);
         break;
      case 62:
         if ((active1 & 0x4000L) != 0L)
            return jjStopAtPos(1, 78);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x28080a0000L, active1, 0L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x8000L, active1, 0L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x702900L, active1, 0L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000L, active1, 0L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000L, active1, 0L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000004040L, active1, 0L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x3000280L, active1, 0L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000000L, active1, 0L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x400L, active1, 0L);
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x810000L, active1, 0L);
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000L, active1, 0L);
      case 124:
         if ((active0 & 0x400000000000000L) != 0L)
            return jjStopAtPos(1, 58);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0, active1);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0, long old1, long active1)
{
   if (((active0 &= old0) | (active1 &= old1)) == 0L)
      return jjStartNfa_0(0, old0, old1);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0, 0L);
      return 2;
   }
   switch(curChar)
   {
      case 78:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStartNfaWithStates_0(2, 37, 21);
         break;
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x2084100L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000L);
      case 103:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000L);
      case 107:
         return jjMoveStringLiteralDfa3_0(active0, 0x1000000000L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x800020000L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x4008200L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x9a00400L);
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x440000L);
      case 116:
         return jjMoveStringLiteralDfa3_0(active0, 0x40L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000080L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0, 0L);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0, 0L);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0xa000000L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x80L);
      case 99:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(3, 17, 21);
         break;
      case 101:
         if ((active0 & 0x400000000L) != 0L)
            return jjStartNfaWithStates_0(3, 34, 21);
         return jjMoveStringLiteralDfa4_0(active0, 0x2040L);
      case 103:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000L);
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000500L);
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x4200L);
      case 109:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(3, 21, 21);
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000L);
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x1000000000L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x401000L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x8800L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x810000L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000000L);
      case 116:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(3, 18, 21);
         else if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(3, 19, 21);
         break;
      default :
         break;
   }
   return jjStartNfa_0(2, active0, 0L);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0, 0L);
      return 4;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000L);
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000000L);
      case 101:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(4, 15, 21);
         else if ((active0 & 0x800000000L) != 0L)
            return jjStartNfaWithStates_0(4, 35, 21);
         return jjMoveStringLiteralDfa5_0(active0, 0x800200L);
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x40L);
      case 108:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(4, 25, 21);
         return jjMoveStringLiteralDfa5_0(active0, 0x880L);
      case 109:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(4, 12, 21);
         return jjMoveStringLiteralDfa5_0(active0, 0x8000100L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x400L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000000000L);
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000L);
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x404000L);
      case 120:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(4, 13, 21);
         break;
      case 121:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(4, 16, 21);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0, 0L);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0, 0L);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0xb00L);
      case 100:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000L);
      case 101:
         if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(5, 7, 21);
         else if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(5, 26, 21);
         return jjMoveStringLiteralDfa6_0(active0, 0x8000040L);
      case 103:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(5, 10, 21);
         break;
      case 104:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(5, 20, 21);
         break;
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x800000L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x400000L);
      case 116:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(5, 24, 21);
         break;
      case 119:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000000000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0, 0L);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0, 0L);
      return 6;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0xc00000L);
      case 101:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(6, 14, 21);
         break;
      case 108:
         if ((active0 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(6, 8, 21);
         break;
      case 110:
         if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(6, 9, 21);
         else if ((active0 & 0x1000000000L) != 0L)
            return jjStartNfaWithStates_0(6, 36, 21);
         break;
      case 114:
         if ((active0 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(6, 6, 21);
         break;
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000800L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0, 0L);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0, 0L);
      return 7;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(7, 11, 21);
         else if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(7, 22, 21);
         return jjMoveStringLiteralDfa8_0(active0, 0x8000000L);
      case 121:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(7, 23, 21);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0, 0L);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0, 0L);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0, 0L);
      return 8;
   }
   switch(curChar)
   {
      case 114:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(8, 27, 21);
         break;
      default :
         break;
   }
   return jjStartNfa_0(7, active0, 0L);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 34;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 5:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 28)
                        kind = 28;
                     jjCheckNAddStates(0, 5);
                  }
                  else if (curChar == 34)
                     jjCheckNAddStates(6, 8);
                  else if (curChar == 46)
                     jjCheckNAdd(10);
                  else if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 0;
                  if (curChar == 48)
                     jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 0:
                  if (curChar == 47)
                     jjCheckNAddStates(9, 11);
                  break;
               case 1:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(9, 11);
                  break;
               case 2:
                  if ((0x2400L & l) != 0L && kind > 5)
                     kind = 5;
                  break;
               case 3:
                  if (curChar == 10 && kind > 5)
                     kind = 5;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 6:
                  if (curChar == 48)
                     jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 9:
                  if (curChar == 46)
                     jjCheckNAdd(10);
                  break;
               case 10:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAddStates(12, 14);
                  break;
               case 12:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(13);
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAddTwoStates(13, 14);
                  break;
               case 15:
                  if (curChar == 34)
                     jjCheckNAddStates(6, 8);
                  break;
               case 16:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 18:
                  if ((0x408400000000L & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 19:
                  if (curChar == 34 && kind > 33)
                     kind = 33;
                  break;
               case 21:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 38)
                     kind = 38;
                  jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 22:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjCheckNAddStates(0, 5);
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjCheckNAdd(23);
                  break;
               case 24:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(24, 25);
                  break;
               case 25:
                  if (curChar != 46)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAddStates(15, 17);
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAddStates(15, 17);
                  break;
               case 28:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(29);
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAddTwoStates(29, 14);
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(18, 20);
                  break;
               case 32:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAdd(33);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 5:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 38)
                     kind = 38;
                  jjCheckNAdd(21);
                  break;
               case 1:
                  jjAddStates(9, 11);
                  break;
               case 7:
                  if ((0x100000001000000L & l) != 0L)
                     jjCheckNAdd(8);
                  break;
               case 8:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 28)
                     kind = 28;
                  jjCheckNAdd(8);
                  break;
               case 11:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(21, 22);
                  break;
               case 14:
                  if ((0x1000000010L & l) != 0L && kind > 31)
                     kind = 31;
                  break;
               case 16:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 17:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 18:
                  if ((0x14404410000000L & l) != 0L)
                     jjCheckNAddStates(6, 8);
                  break;
               case 21:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 38)
                     kind = 38;
                  jjCheckNAdd(21);
                  break;
               case 27:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(23, 24);
                  break;
               case 31:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(25, 26);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(9, 11);
                  break;
               case 16:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(6, 8);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 34 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   23, 24, 25, 30, 31, 14, 16, 17, 19, 1, 2, 4, 10, 11, 14, 26, 
   27, 14, 30, 31, 14, 12, 13, 28, 29, 32, 33, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, "\151\156\164\145\147\145\162", 
"\144\157\165\142\154\145", "\144\145\143\151\155\141\154", "\142\157\157\154\145\141\156", 
"\163\164\162\151\156\147", "\164\145\155\160\154\141\164\145", "\141\170\151\157\155", 
"\162\145\147\145\170", "\151\156\143\154\165\144\145", "\163\143\157\160\145", 
"\161\165\145\162\171", "\143\141\154\143", "\154\151\163\164", "\146\141\143\164", 
"\154\145\156\147\164\150", "\164\145\162\155", "\162\145\163\157\165\162\143\145", 
"\143\165\162\162\145\156\143\171", "\146\157\162\155\141\164", "\154\157\143\141\154", 
"\143\150\157\151\143\145", "\160\141\162\141\155\145\164\145\162", null, null, null, null, null, null, 
"\164\162\165\145", "\146\141\154\163\145", "\165\156\153\156\157\167\156", "\116\141\116", null, 
"\50", "\51", "\173", "\175", "\133", "\135", "\73", "\54", "\56", "\75", "\76", 
"\74", "\41", "\72", "\77", "\75\75", "\74\75", "\76\75", "\41\75", "\174\174", 
"\46\46", "\53\53", "\55\55", "\53", "\55", "\52", "\57", "\46", "\174", "\136", "\45", 
"\53\75", "\55\75", "\52\75", "\57\75", "\46\75", "\174\75", "\136\75", "\45\75", 
"\76\76", "\74\74", "\44", "\176", };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0xfffffffe9fffffc1L, 0x3ffffL, 
};
static final long[] jjtoSkip = {
   0x3eL, 0x0L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[34];
private final int[] jjstateSet = new int[68];
protected char curChar;
/** Constructor. */
public QueryParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public QueryParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 34; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
