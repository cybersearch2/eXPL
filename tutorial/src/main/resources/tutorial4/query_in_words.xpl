axiom lexicon (word, definition) : resource;

template in_words (regex word == "^in[^ ]+", definition);

query<axiom> query_in_words(lexicon : in_words);