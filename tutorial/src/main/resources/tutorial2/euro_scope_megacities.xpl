resource mega_city axiom(Rank,Megacity,Country,Continent,Population);

scope europe
{
  template megacities (Megacity, Country, Continent { "Europe" } );
}

scope asia
{
  template megacities (Megacity, Country, Continent { "Asia" } );
}

query<axiom> euro_megacities (mega_city : europe.megacities);
query<axiom> asia_megacities (mega_city : asia.megacities);