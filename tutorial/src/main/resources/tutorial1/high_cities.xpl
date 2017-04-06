axiom city() : resource;
template high_city(name ? altitude > 5000, altitude);
query high_cities (city : high_city);