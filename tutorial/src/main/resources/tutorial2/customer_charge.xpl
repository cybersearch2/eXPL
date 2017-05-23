axiom charge() : "greek_construction";
axiom customer() : "greek_construction"; 
template freight(city,  charge);
template customer_freight(name, city ? city == freight.city, charge = freight.charge);
		
query customer_charge(charge:freight, customer:customer_freight);
