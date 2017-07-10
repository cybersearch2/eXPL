axiom city_altitude() : resource;
template high_city(city ? altitude > 5000, altitude);
query<axiom> high_cities (city_altitude : high_city);