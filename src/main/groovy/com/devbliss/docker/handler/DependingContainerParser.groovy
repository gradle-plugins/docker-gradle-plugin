package com.devbliss.docker.handler

class DependingContainerParser {

    private final List<String> parsedDependencies

    DependingContainerParser(String dependencies, Boolean fromProperties) {
        if (fromProperties) {
            parsedDependencies = parsePropertyDependencies(dependencies)
        } else {
            parsedDependencies = parseTaskDependencies(dependencies)
        }
    }

    public List<String> getParsedDependencies() {
        return parsedDependencies
    }

    List<String> parsePropertyDependencies(String dependencies) {
        List<String> parsedDependencies = []
        dependencies.eachLine { String line ->
            if ((line.contains("dependingEcosystemServices")).or(line.contains("dependingServices"))) {
                int from = line.indexOf("=") + 1
                int to = line.size()-1
                if (from > (0)) {
                    String dependencyNames = line.substring(from, to).replaceAll("\\s", "")
                    parsedDependencies = dependencyNames.tokenize(",")
                    parsedDependencies = parsedDependencies.collect { it.tokenize("#")[0] }
                }
            }
        }
        return parsedDependencies
    }

    List<String> parseTaskDependencies(String dependencies) {
        List<String> parsedDependencies = []
        dependencies.eachLine { String line ->
            if (line.contains("Depending Container")) {
                int from = line.indexOf("[") + 1
                int to = line.indexOf("]")
                if ((from > (0)).and(to >= (0))) {
                    String dependencyNames = line.substring(from, to).replaceAll("\\s", "")
                    parsedDependencies = dependencyNames.tokenize(",")
                    parsedDependencies = parsedDependencies.collect { it.tokenize("#")[0] }
                }
            }
        }
        return parsedDependencies
    }
}
