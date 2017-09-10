include "mega_city.xpl";

integer count = 0;

template asia_top_ten 
(
  order = ++count ? Continent == "Asia" && count < 10, 
  city = Megacity, country = Country, population = Population.format
); 

query<axiom> asia_top_ten (mega_city : asia_top_ten); 
