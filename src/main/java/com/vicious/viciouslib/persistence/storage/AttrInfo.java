package com.vicious.viciouslib.persistence.storage;

public interface AttrInfo {
    AttrInfo EMPTY = new AttrInfo() {
        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String parent() {
            return "";
        }
    };

    String name();
    String description();
    String parent();

    default boolean hasParent(){
        return parent() != null && !parent().isEmpty();
    }

    default boolean hasDescription(){
        return description() != null && !description().isEmpty();
    }

    class Named implements AttrInfo {
        private final String name;

        public Named(String key) {
            name=key;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public String parent() {
            return null;
        }
    }
}
