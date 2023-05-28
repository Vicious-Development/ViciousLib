package com.vicious.viciouslib.util.reflect.deep;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public class FieldSearchContext extends SearchContext<Field>{

    @Override
    public boolean matches(Field in) {
        if(!super.matches(in)) return false;
        if(superType != null){
            if(!superType.isAssignableFrom(in.getType())){
                return false;
            }
        }
        return in.getType().equals(type);
    }
    public FieldSearchContext type(Class<?> type){
        this.type = type;
        return this;
    }

    @Override
    public FieldSearchContext name(String name) {
        super.name(name);
        return this;
    }
    @Override
    public FieldSearchContext annotated(Class<? extends Annotation>... annotations) {
        super.annotated(annotations);
        return this;
    }
    @Override
    public FieldSearchContext withAccess(List<Predicate<Integer>> modifierPredicators){
        this.modifierPredicators=modifierPredicators;
        return this;
    }
    @Override
    public FieldSearchContext after(SearchContext<Field> ctx) {
        super.after(ctx);
        return this;
    }
    @Override
    public FieldSearchContext before(SearchContext<Field> ctx) {
        super.before(ctx);
        return this;
    }

    public FieldSearchContext superType(Class<?> type){
        this.superType=type;
        return this;
    }


    @Override
    public FieldSearchContext forceAccess() {
        super.forceAccess();
        return this;
    }

    @Override
    public FieldSearchContext required(boolean b) {
        super.required(b);
        return this;
    }
}