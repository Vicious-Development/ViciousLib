package com.vicious.viciouslib.util.reflect.deep;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchContext<T> {
    public SearchContext<T> after;
    public SearchContext<T> before;
    public String name;
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
