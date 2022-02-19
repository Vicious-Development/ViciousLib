package com.vicious.viciouslib.util.reflect.deep;

import java.util.Objects;

public class ClassLocation {
    public String className;
    public String packageToLocate;
    public ClassLocation(String className, String packageToLocate){
        this.className = className;
        this.packageToLocate = packageToLocate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassLocation that = (ClassLocation) o;
        return Objects.equals(className, that.className) && Objects.equals(packageToLocate, that.packageToLocate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, packageToLocate);
    }

    @Override
    public String toString() {
        return "ClassLocation{" +
                "className='" + className + '\'' +
                ", packageToLocate='" + packageToLocate + '\'' +
                '}';
    }
}
