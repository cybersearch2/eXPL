// Use an external axiom source (class DictionarySource)
axiom dictionary (entry) : "dictionary";

// Convert single letter part of speech to word
axiom expand =
{ 
   n = "noun",
   v = "verb",
   a = "adv.",
   j = "adj." 
};

template in_words
( 
. regex entry == "(^in[^ ]+) - (.)\. (.*+)" { word, . pos, definition },
  part = expand[pos]
);

query<axiom> in_words(dictionary : in_words);
