package com.devbliss.docker

import org.gradle.api.Task
import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.task.BuildAndPushDockerImageTask
import com.devbliss.docker.Configuration
import de.gesellix.docker.client.DockerClient
import de.gesellix.gradle.docker.tasks.DockerPsTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 21.01.15.
 */
class ConfigurationSpec extends Specification {
  def project
  def name
  def DockerPluginExtension extension

  def setup() {
    project = ProjectBuilder.builder().build()
    project.apply plugin: 'com.devbliss.docker'
    name = "service1"

    extension = project.devblissDocker
    project.devblissDocker.dependingContainers = 'service1#8080,service2#8081,service3#8082'
    project.devblissDocker.repositoryName = 'example-repository'
    project.devblissDocker.registryName = 'example.registry:5000'
    project.devblissDocker.versionTag = 'latest'
    project.devblissDocker.imageName = 'imageName'
    project.devblissDocker.dockerHost = "dockerHostStatus"
    project.devblissDocker.buildContextDirectory = new File("./")
    project.devblissDocker.authConfigPlain = "authConfigPlain"
    project.devblissDocker.authConfigEncoded = "authConfigPlain"
    
  }

  def "configureStartServiceDependenciesTasks"() {
    when:
    def task = project.getTasks().getByName('startDependencies')

    then:
    extension.dependingContainers == startDependenciesTask.dependingContainers
    extension.repositoryName == startDependenciesTask.dockerRepository
    extension.registryName == startDependenciesTask.dockerRegistry
    extension.versionTag == startDependenciesTask.versionTag
    extension.dockerHost == startDependenciesTask.dockerHost
    extension.buildContextDirectory == startDependenciesTask.buildContextDirectory
    extension.authConfigPlain == startDependenciesTask.authConfigPlain
    extension.authConfigEncoded == startDependenciesTask.authConfigEncoded
  }

  def "configurePullTasks"() {
    when:
    def task = project.getTasks().getByName('pullDockerImage')

    then:
    task.imageName == extension.repositoryName + '/' + extension.imageName
    extension.dockerHost == task.dockerHost
    extension.repositoryName == task.dockerRepository
    extension.registry == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }

  def "configurePushTasks"() {
    when:
    def task = project.getTasks().getByName('pushDockerImage')

    then:
    extension.dockerHost == task.dockerHost
    task.repositoryName == extension.repositoryName + '/' + extension.imageName
    extension.registryName == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }

  def "configureBuildTasks"() {
    when:
    def task = project.getTasks().getByName('buildDockerImage')

    then:
    task.buildContextDirectory == extension.buildContextDirectory
    extension.dockerHost == task.dockerHost
    extension.repositoryName == task.dockerRepository
    extension.registryName == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }
  def "configureStopTasks"() {
    when:
    def task = project.getTasks().getByName('stopDockerContainer')

    then:
    extension.dockerHost == task.dockerHost
    extension.repositoryName == task.dockerRepository
    extension.registryName == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }
  def "configureStartTasks"() {
    when:
    def task = project.getTasks().getByName('startDockerContainer')

    then:
    extension.dockerHost == task.dockerHost
    extension.repositoryName == task.dockerRepository
    extension.registryName == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }
  def "configureRmTasks"() {
    when:
    def task = project.getTasks().getByName('removeDockerContainer')

    then:
    extension.dockerHost == task.dockerHost
    extension.repositoryName == task.dockerRepository
    extension.registryName == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }
  def "configureRunTasks"() {
    when:
    def task = project.getTasks().getByName('runDockerContainer')

    then:
    extension.dockerHost == task.dockerHost
    extension.repositoryName == task.dockerRepository
    extension.registryName == task.dockerRegistry
    extension.versionTag == task.versionTag
    extension.authConfigPlain == task.authConfigPlain
    extension.authConfigEncoded == task.authConfigEncoded
  }

}
