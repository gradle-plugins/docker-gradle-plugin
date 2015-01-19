package com.devbliss.docker

import com.devbliss.docker.tasks.TestTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 07.01.15.
 */

class DockerPluginTest extends Specification {

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
    project.devblissDocker.buildContextDirectory = './'
    project.devblissDocker.dependingContainers = 'service1#8080,service2#8081,service2#8082'

    when:
    def task = project.tasks.create("testTask", TestTask)

    then:
    task.dockerHost == "http://example.org:2375"
    task.authConfigPlain == ["plain auth"]
    task.authConfigEncoded == ["encoded auth"]
    task.imageName == 'test-service'
    task.versionTag == 'latest'
    task.registryName == 'example.registry:5000'
    task.repositoryName == 'example-repository'
    task.buildContextDirectory == './'
    task.dependingContainers == 'service1#8080,service2#8081,service2#8082'
  }

}
