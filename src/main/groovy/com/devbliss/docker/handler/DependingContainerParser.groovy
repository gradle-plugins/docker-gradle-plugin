package com.devbliss.docker.handler

class DependingContainerParser {

    private final List<String> parsedDependencies

    DependingContainerParser(String dependencies) {
        parsedDependencies = parseDepdendencies(dependencies)
    }

    public List<String> getParsedDependencies() {
        return parsedDependencies
    }

    List<String> parseDepdendencies(String dependencies) {
        List<String> parsedDependencies = []
        dependencies.eachLine { String line ->
            if (line.contains("Depending Container")) {
                int from = line.indexOf("[") + 1
                int to = line.indexOf("]")
                if ((from >= (0)).or(to >= (0))) {
                    String dependencyNames = line.substring(from, to).replaceAll("\\s", "")
                    parsedDependencies = dependencyNames.tokenize(",")
                }
            }
        }
        return parsedDependencies
    }
}
