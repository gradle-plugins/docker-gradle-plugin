package com.devbliss.docker.handler

import spock.lang.Specification

class DependingContainerParserSpec extends Specification {

    final String dependenciesString = '''
#Project docker settings
dockerDaemonHost=http://172.17.42.1:2375
dockerRegistry=d-v229-xen:5000
dockerRepository=ecosystem
dockerImage=dementity
dependingEcosystemServices=,eureka-server#8080,course-service#1002,dementity#8081
dockerTag=latest
'''

    final String dependenciesString2 = '''
#Project docker settings
dockerDaemonHost=http://172.17.42.1:2375
dockerRegistry=d-v229-xen:5000
dockerRepository=ecosystem
dockerImage=dementity
dependingEcosystemServices=vandam#8083,eureka-server#8080
dockerTag=latest
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
