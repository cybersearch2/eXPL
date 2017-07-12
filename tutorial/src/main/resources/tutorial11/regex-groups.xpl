// Use an external axiom source (class LexiconSource)
axiom lexicon (word, definition) : "lexicon";

string wordRegex = "^in[^ ]+";
string defRegex = "^(.)\. (.*+)";

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
  regex word == wordRegex, 
  regex definition == defRegex { . part, . def },
  string in_word = word + ", " + expand[part] + "- " + def
);

query<axiom> in_words(lexicon : in_words);

