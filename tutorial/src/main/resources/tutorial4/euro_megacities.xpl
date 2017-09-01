include "mega_city.xpl";

template euro_megacities 
(
  Megacity, 
  Country, 
  Continent !{ "Asia", "Africa", "South America", "North America" } 
);

query<axiom> euro_megacities (mega_city : euro_megacities);