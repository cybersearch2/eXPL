list<term> german_list(german.colors : resource);
list<term> french_list(french.colors : resource);

scope french (language="fr", region="FR"){}
scope german (language="de", region="DE"){}

axiom french.colors (aqua, black, blue, white)
  {"bleu vert", "noir", "bleu", "blanc"};
axiom german.colors (aqua, black, blue, white)
  {"Wasser", "schwarz", "blau", "weiß"};
  
query color_query (german.colors:german.colors) -> (french.colors:french.colors);
