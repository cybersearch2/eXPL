axiom euro_amounts() 
{
  "14.567,89",
  "14 197,52",
  "590,00"
};

template all_amounts
+ export list<currency> amount_list;
(
  currency $ "DE" amount1,
  currency $ "DE" amount2,
  currency $ "DE" amount3,
  amount_list[0] = amount1,
  amount_list[1] = amount2,
  amount_list[2] = amount3
);

query parse_amounts(euro_amounts:all_amounts);