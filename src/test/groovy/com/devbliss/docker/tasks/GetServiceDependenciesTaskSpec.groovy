package com.devbliss.docker.tasks

import com.devbliss.docker.task.GetServiceDependenciesTask
import de.gesellix.docker.client.DockerClient
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
        task.runningServiceDependencies == ["eureka-server", "course-service", "dementity"]
        task.notRunningServiceDependencies == ["vandam", "infinispan"]

    }

    def "execute getServiceDependencies task with no depending containers"() {
        given:
        task.dockerRepository = repository
        task.dockerRegistry = registry
        task.versionTag = versionTag
        task.dependingContainers = null

        when:
        task.execute()

        then:
        task.dependingContainers == null
    }

    def "check non running container"() {

        given:
        task.dockerClient = Mock(DockerClient)
        task.dockerClient.inspectContainer("vandam") >> ["State": ["Running": false]]

        when:
        task.checkIfContainerIsRunning("vandam")

        then:
        task.notRunningServiceDependencies == ["vandam"]
    }
}
