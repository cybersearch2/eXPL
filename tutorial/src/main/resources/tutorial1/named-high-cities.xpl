axiom city_altitude(altitude, city) 
    {1718, "bilene"}
    {8000, "addis ababa"}
    {5280, "denver"}
    {6970, "flagstaff"}
    {8, "jacksonville"}
    {10200, "leadville"}
    {1305, "madrid"}
    {19, "richmond"}
    {1909, "spokane"}
    {1305, "wichita"};
    
template high_city(city ? altitude > 5000, altitude);

query<axiom> high_cities (city_altitude : high_city);