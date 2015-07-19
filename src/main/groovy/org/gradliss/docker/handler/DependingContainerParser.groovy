package org.gradliss.docker.handler

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
                    parsedDependencies = parseDependencyString(line.substring(from, to).replaceAll("\\s", ""))
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
                if ((from > (0)).and(to > from)) {
                    parsedDependencies = parseDependencyString(line.substring(from, to).replaceAll("\\s", ""))
                }
            }
        }
        return parsedDependencies
    }

    List<String> parseDependencyString(String dependencyString) {
        List<String> parsedDependencies = dependencyString.tokenize(",")
        return parsedDependencies.collect { it.tokenize("#")[0] }
    }
}
