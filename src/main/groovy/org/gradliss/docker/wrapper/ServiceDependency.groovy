package org.gradliss.docker.wrapper

class ServiceDependency {

    public static List<ServiceDependency> parseServiceDependencies(String dependencies) {
        List<String> stringDependenciesList = dependencies.replaceAll("\\s", "").split(",")
        return stringDependenciesList.collect { dependency ->
            new ServiceDependency(dependency)
        }
    }

    String name
    String[] port

    public ServiceDependency(String dependency) {
        String portString
        (name, portString) = getDependencyNameAndPort(dependency)
        setPortConfiguration(portString)
    }

    public String getName() {
        return name
    }

    public String getPort() {
        return port[0]
    }

    String[] getPortConfiguration() {
        return port
    }

    public String getImageName() {
        return name.split("_")[0]
    }

    void setPortConfiguration(String portConfig) {
        if (portConfig.contains("-")) {
            port = portConfig.split("-").toList()
        } else {
            port = [portConfig, portConfig]
        }
    }

    List getDependencyNameAndPort(String dependency) {
        return dependency.split("#").toList()
    }
}

