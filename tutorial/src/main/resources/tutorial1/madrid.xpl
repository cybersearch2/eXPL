resource city_altitude axiom();

template madrid(city {"madrid"}, altitude);

query<axiom> madrid (city_altitude : madrid);