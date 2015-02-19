package com.devbliss.docker

import com.devbliss.docker.util.TestTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DockerPluginSpec extends Specification {

    private Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "DockerPluginExtension is added to project"() {
        when:
        project.apply plugin: 'com.devbliss.docker'

        then:
        project["devblissDocker"] instanceof DockerPluginExtension

    }

    def "Configuration is passed to tasks"() {
        given:
        project.apply plugin: 'com.devbliss.docker'
        project.docker.dockerHost = 'http://example.org:2375'
        project.docker.authConfigPlain = ["plain auth"]
        project.docker.authConfigEncoded = ["encoded auth"]
        project.devblissDocker.imageName = 'test-service'
        project.devblissDocker.versionTag = 'latest'
        project.devblissDocker.registryName = 'example.registry:5000'
        project.devblissDocker.repositoryName = 'example-repository'
        project.devblissDocker.buildContextDirectory = new File('./')
        project.devblissDocker.dependingContainers = 'service1#8080,service2#8081,service3#8082'

        when:
        def task = project.tasks.create("testTask", TestTask)

        then: "Verify that all properties are set for AbstractDockerTask"
        task.dockerHost == "http://example.org:2375"
        task.authConfigPlain == ["plain auth"]
        task.authConfigEncoded == ["encoded auth"]
        task.imageName == 'test-service'
        task.versionTag == 'latest'
        task.registryName == 'example.registry:5000'
        task.repositoryName == 'example-repository'
        task.buildContextDirectory == new File('./')
        task.dependingContainers == 'service1#8080,service2#8081,service3#8082'
    }

    def "Check optional propertys"() {
        given:
        project.apply plugin: 'com.devbliss.docker'
        project.docker.dockerHost = 'http://example.org:2375'
        project.devblissDocker.imageName = 'test-service'
        project.devblissDocker.repositoryName = 'example-repository'
        project.devblissDocker.buildContextDirectory = new File('./')
        project.devblissDocker.dependingContainers = 'service1#8080,service2#8081,service3#8082'

        when:
        def task = project.tasks.create("testTask", TestTask)

        then:
        task.authConfigPlain == null
        task.authConfigEncoded == null
        task.versionTag == null
        task.registryName == null
    }
}
