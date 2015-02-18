package com.devbliss.docker.wrapper

import com.devbliss.docker.Configuration
import com.devbliss.docker.wrapper.ServiceDependency
import de.gesellix.docker.client.DockerClient
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ServiceDependencySpec extends Specification {

    String dependencyString = "service_1#8080"
    ServiceDependency serviceDependency;

    def setup() {
        serviceDependency = new ServiceDependency(dependencyString)
    }

    def "test values"() {
        expect:
        serviceDependency.getName() equals "service_1"
        serviceDependency.getPort() equals "8080"
        serviceDependency.getImageName() equals "service"
    }
}
