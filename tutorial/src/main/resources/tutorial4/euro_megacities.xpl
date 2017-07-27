resource mega_city axiom(Rank,Megacity,Country,Continent,Population);

template euro_megacities 
(
  Megacity, 
  Country, 
  Continent !{ "Asia", "Africa", "South America", "North America" } 
);

query<axiom> euro_megacities (mega_city : euro_megacities);