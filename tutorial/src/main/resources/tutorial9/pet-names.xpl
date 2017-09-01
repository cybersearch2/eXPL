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

string nameRegex = "<name>([a-zA-z']*)[^a-zA-z']";

calc pets
+ export list<string> pet_names;
+ cursor pet(pets_info);
(
  string petRegex = 
    "^.*" + nameRegex +".*", 
  {
    ? pet.fact,
    regex xml = (pet++) ? petRegex { name }, 
    pet_names += name
  }
);

calc reverse_pets
+ export list<string> pet_names;
+ cursor pet(pets_info);
(
  string petRegex = 
    "^.*" + nameRegex +".*", 
  -pet,  
  {
    ? pet.fact,
     regex xml = (pet--) ? petRegex { name }, 
     pet_names += name
  }
);

query pet_names(pets);

query reverse_pet_names(reverse_pets);