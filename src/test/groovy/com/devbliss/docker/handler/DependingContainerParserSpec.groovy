package com.devbliss.docker.handler

import spock.lang.Specification

class DependingContainerParserSpec extends Specification {

    final String dependenciesString = '''
:serviceDependencies
Running Container   ------>[]
Depending Container ------>[eureka-server, course-service, dementity]

BUILD SUCCESSFUL

Total time: 22.87 secs
'''

    final String dependenciesString2 = '''
serviceDependencies
-Depending Container: [vandam, eureka-server]

(BUILD SUCCESSFUL

Total time: 8.07 secs
'''

    DependingContainerParser parser

    def setup() {
    }

    def "getParsedDependencies"() {
        given:
        parser = new DependingContainerParser(dependenciesString)

        when:
        List<String> parsedDeps = parser.getParsedDependencies()

        then:
        parsedDeps == ["eureka-server", "course-service", "dementity"]
    }

    def "getParsedDependencies2"() {
        given:
        parser = new DependingContainerParser(dependenciesString2)

        when:
        List<String> parsedDeps = parser.getParsedDependencies()

        then:
        parsedDeps == ["vandam", "eureka-server"]
    }
}
