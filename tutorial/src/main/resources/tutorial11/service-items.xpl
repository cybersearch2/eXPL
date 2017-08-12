list<string> account_info = 
{
 "Invoice #00035", 
 "Service #83057 $60.00",
 "Service #93001       ",
 "Service #10800 $30.00",
 "Service #10661 $45.00",
 "Service #00200       ",
 "Service #78587 $15.00",
 "Service #99585 $10.00",
 "Service #99900  $5.00"
};

string serviceRegex = "#([0-9]+)";
string amountRegex = "(\\$[0-9]+\\.[0-9]+)";

calc scan_item
+ export list<axiom> charges {};
+ cursor item(account_info);
(
. string itemRegex = 
    "^Service "  + serviceRegex + 
    "\\s+" + amountRegex + "?$", 
. currency $ "US" amount,
  currency $ "US" total = 0.0,
  {
    ? item.fact,
    amount = 0.0,
    regex (item++) == itemRegex { service, amount }
    {
      total += amount,
      charges += axiom {
        Service = service,
        Amount = amount.format} 
    } 
  }
);

query<term> scan_service_items(scan_item);