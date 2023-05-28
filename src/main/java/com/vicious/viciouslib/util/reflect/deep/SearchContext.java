package com.vicious.viciouslib.util.reflect.deep;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class SearchContext<T extends AccessibleObject & Member> {
    public SearchContext<T> after;
    public SearchContext<T> before;
    public String name;
    public Class<?> type;
    public Class<?> superType;
    public Class<? extends Annotation>[] annotations;
    public List<Predicate<Integer>> modifierPredicators;
    protected boolean setAccessible;
    private boolean required = true;

    public SearchContext<T> forceAccess() {
        this.setAccessible = true;
        return this;
    }

    public SearchContext<T> superType(Class<?> type){
        this.superType=type;
        return this;
    }

    public boolean shouldForceAccessible() {
        return setAccessible;
    }

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
    public SearchContext<T> annotated(Class<? extends Annotation>... annotations){
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
    public boolean matches(T in){
        if(name != null) if(!in.getName().equals(name)) return false;
        if(annotations != null){
            for (Class<? extends Annotation> annotation : annotations) {
                if(!in.isAnnotationPresent(annotation)) return false;
            }
        }
        if(modifierPredicators != null){
            int inMod = in.getModifiers();
            for (Predicate<Integer> modifierPredicator : modifierPredicators) {
                if(!modifierPredicator.test(inMod)) return false;
            }
        }
        if(shouldForceAccessible()){
            in.setAccessible(true);
        }
        return true;
    }
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

    public SearchContext<T> required(boolean b) {
        this.required = b;
        return this;
    }

    public boolean isRequired() {
        return required;
    }
}