resource german.colors axiom(aqua, black, blue, white);
resource french.colors axiom(aqua, black, blue, white);

choice swatch 
+ list<term> colors@scope;
(name, red, green, blue)
  {colors->aqua, 0, 255, 255}
  {colors->black, 0, 0, 0}
  {colors->blue, 0, 0, 255}
  {colors->white, 255, 255, 255};
  
axiom shade (name) : parameter;

scope french (language="fr", region="FR")
{
  query<term> color_query (shade : swatch);
}

scope german (language="de", region="DE")
{
  query<term> color_query (shade : swatch);
}
