package com.hazelcast.patch;

public class Preconditions {

    public static <T> T checkNotNull (T argument, String errorMessage) {
        if(argument == null) {
            throw new NullPointerException(errorMessage);
        }
        return argument;
    }

    public static <T> T checkNotNull (T argument) {
        if(argument == null) {
            throw new NullPointerException();
        }
        return argument;
    }
}
