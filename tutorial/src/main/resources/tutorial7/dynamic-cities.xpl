list<axiom> city_list = 
    {"bilene", 1718}
    {"addis ababa", 8000}
    {"denver", 5280}
    {"flagstaff", 6970}
    {"jacksonville", 8}
    {"leadville", 10200}
    {"madrid", 1305}
    {"richmond",19}
    {"spokane", 1909}
    {"wichita", 1305};
    
// Template for name and altitude of a high city
template high_city(name ? altitude > 5000, altitude);

query<axiom> high_cities (city_list : high_city); 