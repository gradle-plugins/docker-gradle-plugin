package com.devbliss.docker.handler

import spock.lang.Specification

class DependingContainerParserSpec extends Specification {

    final String dependenciesPropertiesString = '''
#Project docker settings
dockerDaemonHost=http://172.17.42.1:2375
dockerRegistry=d-v229-xen:5000
dockerRepository=ecosystem
dockerImage=dementity
dependingEcosystemServices=,eureka-server#8080,course-service#1002,dementity#8081
dockerTag=latest
'''

    final String dependenciesPropertiesString2 = '''
#Project docker settings
dockerDaemonHost=http://172.17.42.1:2375
dockerRegistry=d-v229-xen:5000
dockerRepository=ecosystem
dockerImage=dementity
dependingEcosystemServices=vandam#8083,eureka-server#8080
dockerTag=latest
'''

    final String dependenciesTaskString = '''
:serviceDependencies
Running Container   ------>[]
Depending Container ------>[eureka-server, course-service, dementity]
BUILD SUCCESSFUL
Total time: 22.87 secs
'''

    final String dependenciesTaskString2 = '''
serviceDependencies
-Depending Container: [vandam, eureka-server]
(BUILD SUCCESSFUL
Total time: 8.07 secs
'''

    DependingContainerParser parser

    def setup() {
    }

    def "getParsedPropertiesDependencies"() {
        given:
        parser = new DependingContainerParser(dependenciesPropertiesString, true)

        when:
        List<String> parsedDeps = parser.getParsedDependencies()

        then:
        parsedDeps == ["eureka-server", "course-service", "dementity"]
    }

    def "getParsedPropertiesDependencies2"() {
        given:
        parser = new DependingContainerParser(dependenciesPropertiesString2, true)

        when:
        List<String> parsedDeps = parser.getParsedDependencies()

        then:
        parsedDeps == ["vandam", "eureka-server"]
    }

    def "getParsedTaskDependencies"() {
        given:
        parser = new DependingContainerParser(dependenciesTaskString, false)

        when:
        List<String> parsedDeps = parser.getParsedDependencies()

        then:
        parsedDeps == ["eureka-server", "course-service", "dementity"]
    }

    def "getParsedTaskDependencies2"() {
        given:
        parser = new DependingContainerParser(dependenciesTaskString2, false)

        when:
        List<String> parsedDeps = parser.getParsedDependencies()

        then:
        parsedDeps == ["vandam", "eureka-server"]
    }
}
