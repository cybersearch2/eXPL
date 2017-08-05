list<string> euro_amounts = 
{
  "14.567,89",
  "14 197,52",
  "590,00"
};

calc all_amounts
+ export list<currency> amount_list;
+ cursor<currency $ "DE"> euro_amount(euro_amounts);
(
  currency total = 0,
  {
    ? euro_amount.fact,
    amount_list += euro_amount++,
    total += amount_list
  }
);

query<term> parse_amounts(all_amounts);