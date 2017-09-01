include "mega_city.xpl";

scope europe
{
  template megacities (Megacity, Country, Continent { "Europe" } );
}

query<axiom> euro_megacities (mega_city : europe.megacities);

scope asia
{
  template megacities (Megacity, Country, Continent { "Asia" } );
}

query<axiom> asia_megacities (mega_city : asia.megacities);