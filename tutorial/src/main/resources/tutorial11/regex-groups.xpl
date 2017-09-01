// Use an external axiom source (class LexiconSource)
resource lexicon axiom(word, definition);

string wordRegex = "^in[^ ]+";
string defRegex = "^(.)\\. (.*+)";

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
  regex word ? wordRegex, 
. regex definition ? defRegex { part, def },
  expand[part],
  def
);

query<axiom> in_words(lexicon : in_words);

