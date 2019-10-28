package com.hazelcast.patch;

public class AlfBlackListClassFilter extends ClassFilter {

    public AlfBlackListClassFilter () {
        // default blacklist - some well-known vulnerable classes/packages
        addClasses(
                "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",
                "bsh.XThis",
                "org.apache.commons.beanutils.BeanComparator",
                "org.codehaus.groovy.runtime.ConvertedClosure",
                "org.codehaus.groovy.runtime.MethodClosure",
                "org.springframework.beans.factory.ObjectFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl"
        )
                .addPackages(
                        "org.apache.commons.collections.functors",
                        "org.apache.commons.collections4.functors"
                );
    }
}
