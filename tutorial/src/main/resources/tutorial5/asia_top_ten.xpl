axiom mega_city (Rank,Megacity,Country,Continent,Population): resource;
integer count = 0;
template asia_top_ten (Megacity ? Continent == "Asia" && count++ < 10, Country, Population); 
query asia_top_ten (mega_city : asia_top_ten); 
