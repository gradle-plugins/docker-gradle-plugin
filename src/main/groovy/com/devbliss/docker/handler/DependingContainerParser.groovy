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
        int from = dependencies.indexOf("[") + 1
        int to = dependencies.indexOf("]")
        if ((from == (-1)).or(to == (-1))) {
            return [];
        }
        String dependencyNames = dependencies.substring(from, to)
        return dependencyNames.tokenize(",")
    }
}
