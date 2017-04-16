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
   
// Collect words starting with 'in' along with other details
axiom word_definitions = {};

calc in_words 
(
  word regex(wordRegex), definition regex(defRegex { part, def }),
  axiom in_word = { word + ", " + expand[part] + "- " + def },
  word_definitions += in_word
);

query query_in_words(lexicon : in_words);

