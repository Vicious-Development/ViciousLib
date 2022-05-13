package com.vicious.viciouslib.util.reflect.deep;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class SearchContext<T> {
    public SearchContext<T> after;
    public SearchContext<T> before;
    public String name;
    public List<Class<? extends Annotation>> annotations;
    public List<Predicate<Integer>> modifierPredicators;

    public SearchContext(){
    }
    public SearchContext<T> after(SearchContext<T> ctx){
        after=ctx;
        return this;
    }
    public SearchContext<T> before(SearchContext<T> ctx){
        before=ctx;
        return this;
    }
    public SearchContext<T> name(String name){
        this.name=name;
        return this;
    }
    public SearchContext<T> annotated(List<Class<? extends Annotation>> annotations){
        this.annotations=annotations;
        return this;
    }

    /**
     * Takes a list of modifier predicators to decide if the method matches the modifiers.
     * @see java.lang.reflect.Modifier for information on what to use.
     */
    public SearchContext<T> withAccess(List<Predicate<Integer>> modifierPredicators){
        this.modifierPredicators=modifierPredicators;
        return this;
    }
    public abstract boolean matches(T in);
    public List<T> getAllMatchingWithin(T[] objs){
        boolean matchedAfter = after == null;
        List<T> options = new ArrayList<>();
        for (int i = 0; i < objs.length; i++) {
            T t = objs[i];
            if(!matchedAfter){
                matchedAfter = after.matches(t);
            }
            else{
                if(matches(t)){
                    options.add(t);
                }
                else if(before != null && before.matches(t)){
                    return options;
                }
            }
        }
        return options;
    }
}
