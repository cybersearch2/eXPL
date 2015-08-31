axiom charge: 
  ( "Athens", 23),
  ( "Sparta", 13),
  ( "Milos", 17);
  
axiom customer:
  ("Marathon Marble", "Sparta"),  
  ("Acropolis Construction", "Athens"),
  ("Agora Imports", "Sparta"),
  ("Spiros Theodolites", "Milos");
  
template charge(city, fee);  
template customer(name, city ? city == charge.city);
  