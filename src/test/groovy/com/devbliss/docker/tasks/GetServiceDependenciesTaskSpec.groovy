package com.devbliss.docker.tasks

import com.devbliss.docker.task.GetServiceDependenciesTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GetServiceDependenciesTaskSpec extends Specification {

    def project
    def task
    def dependingContainers
    def versionTag
    def registry
    def repository

    def setup() {
        project = ProjectBuilder.builder().build()
        task = project.task('serviceDependencies', type: GetServiceDependenciesTask)
        dependingContainers = "eureka-server#8080,course-service#8088,dementity#8081"
        versionTag = 'latest'
        registry = 'example.registry:5000'
        repository = 'example-repository'
    }

    def "execute getServiceDependencies task with depending containers"() {
        given:
        task.dockerRepository = repository
        task.dockerRegistry = registry
        task.versionTag = versionTag
        task.dependingContainers = dependingContainers

        when:
        task.execute()

        then:
        task.notRunningServiceDependencies == ["eureka-server", "course-service", "dementity"]
    }
}
