/* Generated By:JavaCC: Do not edit this line. QueryParserConstants.java */
package au.com.cybersearch2.classy_logic.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface QueryParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int INTEGER = 6;
  /** RegularExpression Id. */
  int DOUBLE = 7;
  /** RegularExpression Id. */
  int DECIMAL = 8;
  /** RegularExpression Id. */
  int BOOLEAN = 9;
  /** RegularExpression Id. */
  int STRING = 10;
  /** RegularExpression Id. */
  int TEMPLATE = 11;
  /** RegularExpression Id. */
  int AXIOM = 12;
  /** RegularExpression Id. */
  int REGEX = 13;
  /** RegularExpression Id. */
  int INCLUDE = 14;
  /** RegularExpression Id. */
  int SCOPE = 15;
  /** RegularExpression Id. */
  int QUERY = 16;
  /** RegularExpression Id. */
  int CALC = 17;
  /** RegularExpression Id. */
  int LIST = 18;
  /** RegularExpression Id. */
  int FACT = 19;
  /** RegularExpression Id. */
  int LENGTH = 20;
  /** RegularExpression Id. */
  int TERM = 21;
  /** RegularExpression Id. */
  int RESOURCE = 22;
  /** RegularExpression Id. */
  int CURRENCY = 23;
  /** RegularExpression Id. */
  int FORMAT = 24;
  /** RegularExpression Id. */
  int LOCAL = 25;
  /** RegularExpression Id. */
  int CHOICE = 26;
  /** RegularExpression Id. */
  int PARAMETER = 27;
  /** RegularExpression Id. */
  int CALL = 28;
  /** RegularExpression Id. */
  int INTEGER_LITERAL = 29;
  /** RegularExpression Id. */
  int DECIMAL_LITERAL = 30;
  /** RegularExpression Id. */
  int HEX_LITERAL = 31;
  /** RegularExpression Id. */
  int FLOATING_POINT_LITERAL = 32;
  /** RegularExpression Id. */
  int NUMBER_LITERAL = 33;
  /** RegularExpression Id. */
  int EXPONENT = 34;
  /** RegularExpression Id. */
  int STRING_LITERAL = 35;
  /** RegularExpression Id. */
  int TRUE = 36;
  /** RegularExpression Id. */
  int FALSE = 37;
  /** RegularExpression Id. */
  int UNKNOWN = 38;
  /** RegularExpression Id. */
  int NAN = 39;
  /** RegularExpression Id. */
  int IDENTIFIER = 40;
  /** RegularExpression Id. */
  int LPAREN = 41;
  /** RegularExpression Id. */
  int RPAREN = 42;
  /** RegularExpression Id. */
  int LBRACE = 43;
  /** RegularExpression Id. */
  int RBRACE = 44;
  /** RegularExpression Id. */
  int LBRACKET = 45;
  /** RegularExpression Id. */
  int RBRACKET = 46;
  /** RegularExpression Id. */
  int SEMICOLON = 47;
  /** RegularExpression Id. */
  int COMMA = 48;
  /** RegularExpression Id. */
  int DOT = 49;
  /** RegularExpression Id. */
  int ASSIGN = 50;
  /** RegularExpression Id. */
  int GT = 51;
  /** RegularExpression Id. */
  int LT = 52;
  /** RegularExpression Id. */
  int BANG = 53;
  /** RegularExpression Id. */
  int COLON = 54;
  /** RegularExpression Id. */
  int EQ = 55;
  /** RegularExpression Id. */
  int LE = 56;
  /** RegularExpression Id. */
  int GE = 57;
  /** RegularExpression Id. */
  int NE = 58;
  /** RegularExpression Id. */
  int SC_OR = 59;
  /** RegularExpression Id. */
  int SC_AND = 60;
  /** RegularExpression Id. */
  int INCR = 61;
  /** RegularExpression Id. */
  int DECR = 62;
  /** RegularExpression Id. */
  int PLUS = 63;
  /** RegularExpression Id. */
  int MINUS = 64;
  /** RegularExpression Id. */
  int STAR = 65;
  /** RegularExpression Id. */
  int SLASH = 66;
  /** RegularExpression Id. */
  int BIT_AND = 67;
  /** RegularExpression Id. */
  int BIT_OR = 68;
  /** RegularExpression Id. */
  int XOR = 69;
  /** RegularExpression Id. */
  int REM = 70;
  /** RegularExpression Id. */
  int PLUSASSIGN = 71;
  /** RegularExpression Id. */
  int MINUSASSIGN = 72;
  /** RegularExpression Id. */
  int STARASSIGN = 73;
  /** RegularExpression Id. */
  int SLASHASSIGN = 74;
  /** RegularExpression Id. */
  int ANDASSIGN = 75;
  /** RegularExpression Id. */
  int ORASSIGN = 76;
  /** RegularExpression Id. */
  int XORASSIGN = 77;
  /** RegularExpression Id. */
  int REMASSIGN = 78;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<token of kind 5>",
    "\"integer\"",
    "\"double\"",
    "\"decimal\"",
    "\"boolean\"",
    "\"string\"",
    "\"template\"",
    "\"axiom\"",
    "\"regex\"",
    "\"include\"",
    "\"scope\"",
    "\"query\"",
    "\"calc\"",
    "\"list\"",
    "\"fact\"",
    "\"length\"",
    "\"term\"",
    "\"resource\"",
    "\"currency\"",
    "\"format\"",
    "\"local\"",
    "\"choice\"",
    "\"parameter\"",
    "\"call\"",
    "<INTEGER_LITERAL>",
    "<DECIMAL_LITERAL>",
    "<HEX_LITERAL>",
    "<FLOATING_POINT_LITERAL>",
    "<NUMBER_LITERAL>",
    "<EXPONENT>",
    "<STRING_LITERAL>",
    "\"true\"",
    "\"false\"",
    "\"unknown\"",
    "\"NaN\"",
    "<IDENTIFIER>",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\">\"",
    "\"<\"",
    "\"!\"",
    "\":\"",
    "\"==\"",
    "\"<=\"",
    "\">=\"",
    "\"!=\"",
    "\"||\"",
    "\"&&\"",
    "\"++\"",
    "\"--\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"&\"",
    "\"|\"",
    "\"^\"",
    "\"%\"",
    "\"+=\"",
    "\"-=\"",
    "\"*=\"",
    "\"/=\"",
    "\"&=\"",
    "\"|=\"",
    "\"^=\"",
    "\"%=\"",
    "\">>\"",
    "\"?\"",
    "\"<<\"",
    "\"~\"",
  };

}
