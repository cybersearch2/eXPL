// Use an external axiom source (class LexiconSource)
axiom lexicon (Word, Definition) : "lexicon";

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
   
calc in_words 
(
. word ? regex wordRegex, 
. definition ? regex defRegex { part, def },
  string in_word = word + ", " + expand[part] + "- " + def
);

query<axiom> in_words(lexicon : in_words);

