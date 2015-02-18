package com.devbliss.docker.wrapper

class ServiceDependency {

    public static List<ServiceDependency> parseServiceDependencies(String dependencies) {
        List<String> stringDependenciesList = dependencies.replaceAll("\\s", "").split(",")
        return stringDependenciesList.collect { dependency ->
            new ServiceDependency(dependency)
        }
    }

    String name
    String port

    public ServiceDependency(String dependency) {
        (name, port) = getDependencyNameAndPort(dependency)
    }

    public String getName() {
        return name
    }

    public String getPort() {
        return port
    }

    public String getImageName() {
        return name.split("_")[0]
    }

    List getDependencyNameAndPort(String dependency) {
        return dependency.split("#").toList()
    }
}

