axiom charge() 
  {"Sparta", 13 }
  {"Athens", 23 }
  {"Milos", 17};
		
axiom customer()
  {"Marathon Marble", "Sparta"}
  {"Acropolis Construction", "Athens"}
  {"Agora Imports", "Sparta"}
  {"Spiros Theodolites", "Milos"};
  
template freight(charge, city);
template customer_freight(name, city ? city == freight.city, charge = freight.charge);
		
query customer_charge(charge:freight, customer:customer_freight);
