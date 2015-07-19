package org.gradliss.docker.tasks

import org.gradliss.docker.task.GetServiceDependenciesTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GetServiceDependenciesTaskSpec extends Specification {

    def project
    def task
    def dependingContainers

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('serviceDependencies', type: GetServiceDependenciesTask)
        dependingContainers = "eureka-server#8080,course-service#8088,dementity#8081"
    }

    def "execute getServiceDependencies task with depending containers"() {
        given:
        task.dependingContainers = dependingContainers

        when:
        task.execute()

        then:
        task.notRunningServiceDependencies == ["eureka-server", "course-service", "dementity"]
    }
}
