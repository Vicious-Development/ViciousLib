package com.vicious.viciouslib.persistence.serialization.vson;

import com.vicious.viciouslib.persistence.serialization.generic.Parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface VSONParser extends Parser {
    HashSet<Character> defaultDeliminators = new HashSet<>(Arrays.asList(',','\n','}',']'));

    default Collection<Character> deliminators(){
        return defaultDeliminators;
    }

    default boolean isDeliminator(char c){
        return deliminators().contains(c);
    }
}
