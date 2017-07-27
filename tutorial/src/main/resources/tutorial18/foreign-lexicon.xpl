resource german.colors export;
resource french.colors export;

scope french (language="fr", region="FR"){}
scope german (language="de", region="DE"){}

axiom french.colors (aqua, black, blue, white)
  {"bleu vert", "noir", "bleu", "blanc"};
axiom german.colors (aqua, black, blue, white)
  {"Wasser", "schwarz", "blau", "weiß"};
  
query color_query (german.colors:german.colors) -> (french.colors:french.colors);
