axiom city() : resource;
template high_city(name ? altitude > 5000, altitude);
query<axiom> high_cities (city : high_city);