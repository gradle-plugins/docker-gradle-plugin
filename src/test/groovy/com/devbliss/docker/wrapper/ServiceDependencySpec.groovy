package com.devbliss.docker.wrapper

import spock.lang.Specification

class ServiceDependencySpec extends Specification {

    ServiceDependency serviceDependency;

    def setup() {
    }

    def "test values"() {
        setup:
        String dependencyString = "service_1#8080"
        serviceDependency = new ServiceDependency(dependencyString)

        expect:
        serviceDependency.getName() equals "service_1"
        serviceDependency.getPort() equals "8080"
        serviceDependency.getImageName() equals "service"
    }

    def "getPort and getPortConfiguration"() {
        given:
        String from = "20"
        String to = "40"
        String port = "${from}-${to}"
        String dependencyString = "service_1#${port}"
        serviceDependency = new ServiceDependency(dependencyString)

        when:
        def (resultFrom, resultTo) = serviceDependency.getPortConfiguration()

        then:
        resultFrom == from
        resultTo == to
        from == serviceDependency.getPort()
    }
}
