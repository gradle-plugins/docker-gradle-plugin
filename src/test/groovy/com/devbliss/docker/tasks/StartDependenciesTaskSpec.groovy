package com.devbliss.docker.tasks

import com.devbliss.docker.task.StartDependenciesTask
import de.gesellix.docker.client.DockerClient
import de.gesellix.gradle.docker.tasks.DockerPsTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 19.01.15.
 */
class StartDependenciesTaskSpec extends Specification {

  def project
  def task
  def dockerClient = Mock(DockerClient)
  def name

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task('startServiceDependencies', type: StartDependenciesTask)

  }

  def "add task to startServiceDependencies project"() {
    given:
    task.dependingContainers = 'service1#8080,service2#8081,service3#8082'
    task.dockerRepository = 'example-repository'
    task.dockerRegistry = 'example.registry:5000'
    task.versionTag = 'latest'
    task.dockerClient = dockerClient
    task.dockerHostStatus = dockerClient.ps()

    when:
    task.execute()

    then:
    1 * dockerClient.pull('example-repository/service3', 'latest', 'example.registry:5000')
    1 * dockerClient.pull('example-repository/service2', 'latest', 'example.registry:5000')
    1 * dockerClient.pull('example-repository/service1', 'latest', 'example.registry:5000')
    2 * dockerClient.ps()
    1 * dockerClient.run('example.registry:5000/example-repository/service1',
            ['HostConfig': ['PortBindings': ['8080/tcp': [['HostPort': '8080']]]]], 'latest', 'service1')
    1 * dockerClient.run('example.registry:5000/example-repository/service2',
            ['HostConfig': ['PortBindings': ['8081/tcp': [['HostPort': '8081']]]]], 'latest', 'service2')
    1 * dockerClient.run('example.registry:5000/example-repository/service3',
            ['HostConfig': ['PortBindings': ['8082/tcp': [['HostPort': '8082']]]]], 'latest', 'service3')

  }

  def "try to removeContainer"() {
    given:
    task.dockerClient = dockerClient
    name = "service1"

    when:
    task.stopAndRemoveContainer(name)

    then:
    println "stop and removing container works"
  }
}
