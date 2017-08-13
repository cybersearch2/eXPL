axiom german.lexicon 
  (    aqua,    black,    blue,  white)
  {"Wasser", "schwarz", "blau", "weiß"};
 
choice swatch 
+ list<term> lexicon@scope;
  (   name,      red, green, blue)
  {lexicon->aqua,    0, 255, 255}
  {lexicon->black,   0,   0,   0}
  {lexicon->blue,    0  , 0, 255}
  {lexicon->white, 255, 255, 255};
  
axiom shade (name) : parameter;

scope german (language="de", region="DE")
{
  query<term> color_query (shade : swatch);
}
