
package com.hazelcast.patch;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import static java.util.Collections.unmodifiableSet;

/**
 * Holds blacklist and whitelist configuration in java deserialization configuration.
 */
public class ClassFilter {

    private static final String PROPERTY_CLASSNAME_LIMIT = "hazelcast.serialization.filter.classname.limit";
    private static final int CLASSNAME_LIMIT = Integer.getInteger(PROPERTY_CLASSNAME_LIMIT, 10000);
    private static final ILogger LOGGER = Logger.getLogger(ClassFilter.class.getName());

    private final Set<String> classes = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> packages = Collections.synchronizedSet(new HashSet<String>());
    private final Set<String> prefixes = Collections.synchronizedSet(new HashSet<String>());

    private AtomicBoolean warningLogged = new AtomicBoolean();

    public ClassFilter () {
    }

    /**
     * Used by spring bean definition builder
     * @param classes
     * @param packages
     * @param prefixes
     */
    public ClassFilter (List<String> classes, List<String> packages, List<String> prefixes) {
        if(classes != null){
            addClasses(classes.toArray(new String[0]));
        }
        if(packages != null){
            addPackages(packages.toArray(new String[0]));
        }
        if(prefixes != null){
            addPrefixes(prefixes.toArray(new String[0]));
        }
    }

    /**
     * Returns unmodifiable set of class names.
     */
    public Set<String> getClasses () {
        return unmodifiableSet(classes);
    }

    /**
     * Returns unmodifiable set of package names.
     */
    public Set<String> getPackages () {
        return unmodifiableSet(packages);
    }

    /**
     * Returns unmodifiable set of class name prefixes.
     */
    public Set<String> getPrefixes () {
        return unmodifiableSet(prefixes);
    }

    public ClassFilter addClasses (String... names) {
        Preconditions.checkNotNull(names);
        for (String name : names) {
            classes.add(name);
        }
        return this;
    }

    public ClassFilter setClasses (Collection<String> names) {
        Preconditions.checkNotNull(names);
        classes.clear();
        classes.addAll(names);
        return this;
    }

    public ClassFilter addPackages (String... names) {
        Preconditions.checkNotNull(names);
        for (String name : names) {
            packages.add(name);
        }
        return this;
    }

    public ClassFilter setPackages (Collection<String> names) {
        Preconditions.checkNotNull(names);
        packages.clear();
        packages.addAll(names);
        return this;
    }

    public ClassFilter addPrefixes (String... names) {
        Preconditions.checkNotNull(names);
        for (String name : names) {
            prefixes.add(name);
        }
        return this;
    }

    public ClassFilter setPrefixes (Collection<String> names) {
        Preconditions.checkNotNull(names);
        prefixes.clear();
        prefixes.addAll(names);
        return this;
    }

    public boolean isEmpty () {
        return classes.isEmpty() && packages.isEmpty() && prefixes.isEmpty();
    }

    public boolean isListed (String className) {
        if(classes.contains(className)) {
            return true;
        }
        if(!packages.isEmpty()) {
            int dotPosition = className.lastIndexOf(".");
            if(dotPosition > 0 && checkPackage(className, className.substring(0, dotPosition))) {
                return true;
            }
        }
        return checkPrefixes(className);
    }

    /**
     * Checks if given class name is listed by package. If it's listed, then performance optimization is used and classname is
     * added directly to {@code classes} collection.
     *
     * @param className   Class name to be checked.
     * @param packageName Package name of the checked class.
     * @return {@code true} iff class is listed by-package
     */
    private boolean checkPackage (String className, String packageName) {
        if(packages.contains(packageName)) {
            cacheClassname(className);
            return true;
        }
        return false;
    }

    private void cacheClassname (String className) {
        if(classes.size() < CLASSNAME_LIMIT) {
            // performance optimization
            classes.add(className);
        } else if(warningLogged.compareAndSet(false, true)) {
            LOGGER.log(Level.WARNING, String.format(
                    "The class names collection size reached its limit. Optimizations for package names checks "
                            + "will not optimize next usages. You can control the class names collection size limit by "
                            + "setting system property '%s'. Actual value is %d.",
                    PROPERTY_CLASSNAME_LIMIT, CLASSNAME_LIMIT));
        }
    }

    private boolean checkPrefixes (String className) {
        for (String prefix : prefixes) {
            if(className.startsWith(prefix)) {
                cacheClassname(className);
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + classes.hashCode();
        result = prime * result + packages.hashCode();
        result = prime * result + prefixes.hashCode();
        result = prime * result + (warningLogged.get() ? 0 : 1);
        return result;
    }

    @Override
    public boolean equals (Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ClassFilter other = (ClassFilter) obj;
        boolean result = classes.equals(other.classes)
                && packages.equals(other.packages)
                && prefixes.equals(other.prefixes)
                && warningLogged.get() == other.warningLogged.get();
        return result;
    }

    @Override
    public String toString () {
        return "ClassFilter{classes=" + classes + ", packages=" + packages + ", prefixes=" + prefixes + "}";
    }

}
