axiom mega_city (Rank,Megacity,Country,Continent,Population) : resource;
template euro_megacities (Megacity, Country, Continent { "Europe" } );
query euro_megacities (mega_city : euro_megacities);