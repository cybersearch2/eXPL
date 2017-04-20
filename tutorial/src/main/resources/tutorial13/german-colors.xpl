// Define lexicon term names
axiom lexicon (aqua, black, blue, white);

axiom german.lexicon 
  (    aqua,    black,    blue,  white)
  {"Wasser", "schwarz", "blau", "weiß"};
 
choice swatch 
  (   name,      red, green, blue)
  {colors[aqua],    0, 255, 255}
  {colors[black],   0,   0,   0}
  {colors[blue],    0  , 0, 255}
  {colors[white], 255, 255, 255};
  
axiom shade (name) : parameter;

local colors(lexicon);

scope german (language="de", region="DE")
{
  query<term> color_query (shade : swatch);
}
