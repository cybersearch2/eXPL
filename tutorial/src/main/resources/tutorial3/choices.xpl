choice bank_by_prefix 
  ( prefix,  bank,                       bsb )
  { "456448", "Bank of Queensland",      "124-001" }
  { "456443", "Bendigo Bank LTD",        "633-000" }
  { "456445", "Commonwealth Bank Aust.", "527-146" };

axiom prefix_account 
  (prefix, account)
  { "456448", 2 }
  { "456445", 1 }
  { "456443", 3 };
  
template account_bank
(
  choice Account
  {
    "sav" ? account == 1,
    "cre" ? account == 2,
    "chq" ? account == 3
  },
. choice bank_by_prefix,
  Bank = bank,
  BSB = bsb
);

query<axiom> bank_details( prefix_account: account_bank ); 