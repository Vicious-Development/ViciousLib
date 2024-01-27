package com.vicious.viciouslib.persistence.serialization.generic;

import com.vicious.viciouslib.util.quick.ObjectList;

import java.io.IOException;
import java.io.InputStream;

public interface CollectionParser extends Parser{
    ObjectList getCollection();
}
