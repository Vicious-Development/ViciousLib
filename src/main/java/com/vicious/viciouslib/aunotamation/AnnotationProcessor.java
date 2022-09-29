package com.vicious.viciouslib.aunotamation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

public abstract class AnnotationProcessor<ANNOTATION extends Annotation, ON> {
    private final Class<ANNOTATION> annotationClass;
    private final Class<ON> acceptsClass;

    public AnnotationProcessor(Class<ANNOTATION> annotationClass, Class<ON> applyON) {
        this.acceptsClass = applyON;
        this.annotationClass = annotationClass;
    }
    public final void processObject(Object object, AnnotatedElement element){
        process(acceptsClass.cast(object),element);
    }
    public abstract void process(ON object, AnnotatedElement element);
    public Class<ANNOTATION> getAnnotationClass(){
        return annotationClass;
    }
    public Class<ON> getObjectClass(){
        return acceptsClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationProcessor<?, ?> that = (AnnotationProcessor<?, ?>) o;
        return Objects.equals(annotationClass, that.annotationClass);
    }

    public void err(AnnotatedElement element, String cause){
        throw new InvalidAnnotationException(Aunotamation.getElementName(element) + " in " + Aunotamation.getElementLocation(element).getCanonicalName() + " annotated with " + annotationClass.getName() + " " + cause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotationClass);
    }
}