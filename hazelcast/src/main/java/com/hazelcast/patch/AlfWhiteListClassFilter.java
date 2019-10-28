package com.hazelcast.patch;

public class AlfWhiteListClassFilter extends ClassFilter {

    public AlfWhiteListClassFilter () {
        addPrefixes(
                "org.alfresco."
        );
    }
}
