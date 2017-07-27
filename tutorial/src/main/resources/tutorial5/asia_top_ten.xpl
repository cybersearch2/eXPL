resource mega_city axiom(Rank,Megacity,Country,Continent,Population);

integer count = 0;

template asia_top_ten 
(
  rank = count ? Continent == "Asia" && count++ < 10, 
  city = Megacity, country = Country, population = Population.format
); 

query<axiom> asia_top_ten (mega_city : asia_top_ten); 
