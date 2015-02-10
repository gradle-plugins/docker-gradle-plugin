package com.devbliss.docker.util

/**
 *
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 */
class DependencyStringUtils {
	
    static public List<String> splitServiceDependenciesString(String dependingContainers) {
        return dependingContainers.replaceAll("\\s", "").split(",")
    }

    static public List getDependencyNameAndPort(String dependency) {
        return dependency.split("#").toList()
    }

    static public String getServiceNameFromContainer(Map container) {
        return container.Names[0].substring(1, container.Names[0].length());
    }
}

