package com.vicious.viciouslib.persistence.vson.value;

import com.vicious.viciouslib.persistence.vson.writer.NamePair;

import java.util.List;

public interface IHasChildren {
    boolean hasChildren();
    List<NamePair> getChildren();
}
