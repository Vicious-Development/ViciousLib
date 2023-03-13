package com.vicious.viciouslib.persistence.storage;

import com.vicious.viciouslib.jarloader.event.VEvent;

public class AttributeModificationEvent extends VEvent {
    private final Object currentAttributeValue;

    public AttributeModificationEvent(boolean hasCompleted, Object currentAttributeValue) {
        super(hasCompleted);
        this.currentAttributeValue = currentAttributeValue;
    }

    public Object getCurrentAttributeValue() {
        return currentAttributeValue;
    }
}
