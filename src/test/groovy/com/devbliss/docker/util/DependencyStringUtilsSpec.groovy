package com.devbliss.docker.util

import com.devbliss.docker.Configuration
import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.docker.client.DockerClient
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DependencyStringUtilsSpec extends Specification {

    def setup() {
    }
    
    def "splitServiceDependenciesString"() {
        when:
        List<String> deps = DependencyStringUtils.splitServiceDependenciesString("service1#8080,service3#8082,service2#8081")

        then:
        deps == ["service1#8080","service3#8082","service2#8081"]
    }

    def "getDependencyNameAndPort"() {
        when:
        def (name, port) = DependencyStringUtils.getDependencyNameAndPort("service1#8080")

        then:
        name.equals "service1"
        port.equals "8080"
    }

    def "getServiceNameFromContainer"() {
        when:
        String name = DependencyStringUtils.getServiceNameFromContainer(["Names":["/service1"]])

        then:
        name.equals "service1"
    }
}
