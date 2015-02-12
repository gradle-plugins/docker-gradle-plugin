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
        task.dockerClient = Mock(DockerClient)
        task.dockerClient.exec("eureka-server", ["cat", "gradle.properties"]) >> ""
        task.dockerClient.exec("course-service", ["cat", "gradle.properties"]) >> "dependingEcosystemServices=vandam#8083,eureka-server#8080"
        task.dockerClient.exec("vandam", ["cat", "gradle.properties"]) >> "dependingEcosystemServices=eureka-server#8080,infinispan#11222"
        task.dockerClient.exec("infinispan", ["cat", "gradle.properties"]) >> ""
        task.dockerClient.exec("dementity", ["cat", "gradle.properties"]) >> "dependingEcosystemServices=eureka-server#8080,infinispan#11222,vandam#8083"

        when:
        task.execute()

        then:
        task.notRunningServiceDependencies == ["vandam", "eureka-server", "infinispan"]
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
}
