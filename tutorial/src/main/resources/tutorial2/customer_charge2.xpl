resource charge axiom() = "greek_construction";
resource customer axiom() = "greek_construction"; 

template freight(charge, city);
template customer_freight(name, city ? city == freight.city, charge = freight.charge);
		
query customer_charge(charge:freight, customer:customer_freight);
