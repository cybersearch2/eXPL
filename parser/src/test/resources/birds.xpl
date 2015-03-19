
axiom order (order, nostrils, live, bill, feet, eats) :
  ("tubenose", "external tubular", "at sea", "hooked", "", ""),
  ("waterfowl", "", "", "flat", "webbed", ""),
  ("falconiforms", "", "", "sharp hooked", "curved talons", "meat"),
  ("passerformes", "", "", "", "one long backward toe", "");

axiom family (family, order, size, wings, neck, color, flight, feed, head, tail, bill, eats):
  ("albatross", "tubenose", "large", "long narrow", "", "", "", "", "", "", "", ""),
  ("swan", "waterfowl", "", "", "long", "white", "ponderous", "", "", "", "", ""),
  ("goose", "waterfowl", "plump", "", "", "", "powerful", "", "", "", "", ""),
  ("duck", "waterfowl", "", "", "", "", "agile", "on water surface", "", "", "", ""), 
  ("vulture", "falconiforms", "", "broad", "", "", "", "scavange", "", "", "", ""),
  ("falcon", "falconiforms", "", "long pointed", "", "", "", "", "large", "narrow at tip", "", ""),
  ("flycatcher", "passerformes", "", "", "", "", "",  "", "", "", "flat", "flying insects"),
  ("swallow", "passerformes", "", "long pointed", "", "", "", "", "", "forked", "short", "");

axiom bird (bird, order, family, color, size, flight, throat, voice, eats, tail):
  ("laysan_albatross", "", "albatross", "white", "", "", "", "", "", ""),
  ("black footed albatross", "", "albatross", "dark", "", "", "", "", "", ""),
  ("fulmar", "tubenose", "", "", "medium", "flap glide", "", "", "", ""),
  ("whistling swan", "", "swan", "", "", "", "", "muffled musical whistle", "", ""),
  ("trumpeter swan", "", "swan", "", "", "", "", "loud trumpeting", "", ""),
  ("snow goose", "", "goose", "white", "", "", "", "", "", ""),
  ("pintail", "", "duck", "", "", "", "", "short whistle", "", ""),
  ("turkey vulture", "", "vulture", "", "", "v shaped", "", "", "", ""),
  ("california condor", "", "vulture", "", "", "flat", "", "", "", ""),
  ("sparrow hawk", "", "falcon", "", "", "", "", "", "insects", ""),
  ("peregrine falcon", "", "falcon", "", "", "", "", "", "birds", ""),
  ("great crested flycatcher", "", "flycatcher", "", "", "", "", "", "", "long rusty"),
  ("ash throated flycatcher", "", "flycatcher", "", "", "", "white", "", "", ""),
  ("barn swallow", "", "swallow", "", "", "", "", "", "", "forked"),
  ("cliff swallow", "", "swallow", "", "", "", "", "", "", "square"),
  ("purple martin", "", "swallow", "dark", "", "", "", "", "", "");
