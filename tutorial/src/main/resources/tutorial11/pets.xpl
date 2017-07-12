list<string> pets_info = 
{
  "<pet><species>dog</species><name>Lassie</name><color>blonde</color></pet>",
  "<pet><species>cat</species><name>Cuddles</name><color>tortoise</color></pet>",
  "<pet><species>Dog</species><name>Bruiser</name><color>brindle</color></pet>",
  "<pet><species>Dog</species><name>Rex</name><color>black and tan</color></pet>",
  "<pet><species>Cat</species><name>Pixie</name><color>black</color></pet>",
  "<pet><species>dog</species><name>Axel</name><color>white</color></pet>",
  "<pet><species>Cat</species><name>Amiele</name><color>ginger</color></pet>",
  "<pet><species>dog</species><name>Fido</name><color>brown</color></pet>"
};

string speciesRegex = "<species>dog";
string nameRegex = "<name>([a-zA-z']*)[^a-zA-z']";
string colorRegex = "<color>([a-zA-z' ]*)[^a-zA-z' ]";

calc dogs
(
  integer i = 0,
  string petRegex = 
    "^.*" + speciesRegex + 
    ".*" + nameRegex + 
    ".*" + colorRegex +".*", 
  {
    ? i < pets_info.length,
    string pet = pets_info[i++],
    regex(case_insensitive) pet == petRegex { name, color }, 
    //system.print(pet)
    system.print(name, " is a ", color, " dog.")
  }
);

query pet_query (dogs);