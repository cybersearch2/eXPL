resource city_altitude axiom();

template high_city(city ? altitude > 5000, altitude);

query<axiom> high_cities (city_altitude : high_city);