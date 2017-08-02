resource mega_city axiom(Rank,Megacity,Country,Continent,Population);

template city_info 
+ export list<axiom> asia {};
+ export list<axiom> africa {};
+ export list<axiom> europe {};
+ export list<axiom> south_america {};
+ export list<axiom> north_america {};
(
  continent = Continent, 
  city = Megacity, 
  country = Country 
); 

template group
+ list<axiom> asia = city_info.asia;
+ list<axiom> africa = city_info.africa;
+ list<axiom> europe = city_info.europe;
+ list<axiom> south_america = city_info.south_america;
+ list<axiom> north_america = city_info.north_america;
+ list<axiom> mega_city {};
(
  mega_city = axiom { continent, city, country },
  choice group 
  {
    asia += mega_city ? continent == "Asia",
    africa += mega_city ? continent == "Africa",
    europe += mega_city ? continent == "Europe",
    south_america += mega_city ? continent == "South America",
    north_america += mega_city ? continent == "North America"
  }
);

query mega_cities_by_continent 
(
  mega_city:city_info, group 
); 
