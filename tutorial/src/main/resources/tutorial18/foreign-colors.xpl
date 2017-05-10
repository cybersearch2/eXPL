axiom colors        (aqua, black, blue, white);
axiom german.colors (aqua, black, blue, white) : resource;
axiom french.colors (aqua, black, blue, white) : resource;

local select(colors);

choice swatch (name, red, green, blue)
  {select^aqua, 0, 255, 255}
  {select^black, 0, 0, 0}
  {select^blue, 0, 0, 255}
  {select^white, 255, 255, 255};
  
axiom shade (name) : parameter;

scope french (language="fr", region="FR")
{
  query<term> color_query (shade : swatch);
}

scope german (language="de", region="DE")
{
  query<term> color_query (shade : swatch);
}
