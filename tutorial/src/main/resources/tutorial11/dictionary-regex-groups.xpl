// Use an external axiom source (class DictionarySource)
resource dictionary axiom(entry);

// Convert single letter part of speech to word
list<axiom> expand 
{ 
   n = "noun",
   v = "verb",
   a = "adv.",
   j = "adj." 
};

template in_words
( 
. regex entry == "(^in[^ ]+) - (.)\\. (.*+)" { word, pos, definition },
  word,
  part = expand[pos],
  definition
);

query<axiom> in_words(dictionary : in_words);
