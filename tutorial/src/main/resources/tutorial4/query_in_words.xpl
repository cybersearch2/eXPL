resource lexicon 
  axiom(word, definition),
  export in_words;

template in_words (regex word == "^in[^ ]+", definition);

query<axiom> query_in_words(lexicon : in_words);