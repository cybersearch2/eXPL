axiom lexicon (word, definition) : resource;

template in_words (word ? regex "^in[^ ]+", string definition);

query<axiom> query_in_words(lexicon : in_words);