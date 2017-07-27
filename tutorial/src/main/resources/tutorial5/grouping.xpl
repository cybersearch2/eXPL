resource mega_city axiom(Rank,Megacity,Country,Continent,Population);

axiom continents(continent)
  { "Asia" }
  { "Africa"}
  { "Europe" }
  { "South America" }
  { "North America" };

template continent 
(
  continent
); 

template continent_group 
(
  continent ? continent == Continent, 
  city = Megacity, 
  country = Country, 
  rank = Rank, 
  population = Population.format
); 

query<axiom> mega_cities_by_continent 
(
  continents : continent, 
  mega_city : continent_group
); 
