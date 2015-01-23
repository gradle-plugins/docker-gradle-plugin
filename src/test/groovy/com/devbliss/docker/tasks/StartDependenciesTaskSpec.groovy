package com.devbliss.docker.tasks

import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.Configuration
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
    task = project.task(Configuration.TASK_NAME_START_DEPENDENCIES, type: StartDependenciesTask)
    task.dockerClient = dockerClient
    name = "service1"
  }

  def "add task to startServiceDependencies project"() {
    given:
    task.dependingContainers = 'service1#8080,service2#8081,service3#8082'
    task.dockerRepository = 'example-repository'
    task.dockerRegistry = 'example.registry:5000'
    task.versionTag = 'latest'
    task.dockerHostStatus = dockerClient.ps()

    when:
    task.execute()

    then:
    1 * dockerClient.pull('example-repository/service3', 'latest', 'example.registry:5000')
    1 * dockerClient.pull('example-repository/service2', 'latest', 'example.registry:5000')
    1 * dockerClient.pull('example-repository/service1', 'latest', 'example.registry:5000')
    1 * dockerClient.ps()
    1 * dockerClient.run('example.registry:5000/example-repository/service1',
      ['HostConfig': ['PortBindings': ['8080/tcp': [['HostPort': '8080']]]]
        , 'Cmd':'-Pdocker.alreadyHandled=service1,service2,service3']
      , 'latest', 'service1')
    1 * dockerClient.run('example.registry:5000/example-repository/service2',
      ['HostConfig': ['PortBindings': ['8081/tcp': [['HostPort': '8081']]]]
        , 'Cmd':'-Pdocker.alreadyHandled=service1,service2,service3']
      , 'latest', 'service2')
    1 * dockerClient.run('example.registry:5000/example-repository/service3',
      ['HostConfig': ['PortBindings': ['8082/tcp': [['HostPort': '8082']]]]
        , 'Cmd':'-Pdocker.alreadyHandled=service1,service2,service3']
      , 'latest', 'service3')

  }

  def "cleanupOldDependencies"() {
    given:
    task.dockerHostStatus = [["Names":["_service1"], "Image":"435hi3u5h345"]]
    name = "service1"
    def dependingContainersList = ["${name}#0000"]
    task.existingContainers = [name]
    task.runningContainers = [name]

    when:
    task.cleanupOldDependencies(dependingContainersList)

    then:
    1 * dockerClient.stop(name)
    1 * dockerClient.rm(name)
    task.existingContainers.size() == 0
    task.runningContainers.size() == 0
  }

  def "updateContainerDependencies"() {
    given:
    def commandArg = "testArgument"

    when:
    task.updateContainerDependencies(name, commandArg)

    then:
    1 * dockerClient.exec(name, ["./gradlew", Configuration.TASK_NAME_START_DEPENDENCIES, commandArg])
  }

  def "prepareNewdockerAlreadyHandledList"() {
    given:
    task.dockerAlreadyHandledList = ["test1", "test4"]
    def additional = ["test2", "test3"]
    def additional2 = ["test1", "test4"]

    when:
    def result = task.prepareNewdockerAlreadyHandledList(additional)
    def result2 = task.prepareNewdockerAlreadyHandledList(additional2)

    then:
    result.size() == 4
    result == ["test1", "test4", "test2", "test3"] as Set
    result2.size() == 2
  }

  def "getPort"() {
    given:
    def from = "20"
    def to = "40"
    def port = "${from}-${to}"

    when:
    def (resultFrom, resultTo) = task.getPort(port)

    then:
    resultFrom == from
    resultTo == to
  }

  def "setContainerExts"() {
    given:
    def service1 = "service1"
    def service2 = "service2"
    dockerClient.ps() >> [["Names":["_$service1"], "Status":"Up"], ["Names":["_$service2"], "Status":"Exited sinde 10 seconds"]]

    when:
    task.setContainerExts()

    then:
    task.existingContainers == [service1, service2]
    task.runningContainers == [service1]
  }

  def "pullImageFromRegistry"() {
    given:
    def registry = "dockerRegistry"
    def repository = "dockerRepository"
    def tag = "tag"
    task.dockerRepository = repository
    task.versionTag = tag
    task.dockerRegistry = registry

    when:
    task.pullImageFromRegistry(name)

    then:
    1 * dockerClient.pull("${repository}/${name}", tag, registry)
  }

  def "splitDependingContainersStringAndPullImage"() {
    given:
    def registry = "dockerRegistry"
    def repository = "dockerRepository"
    def tag = "tag"
    def name2 = "service2"
    task.dockerRepository = repository
    task.versionTag = tag
    task.dockerRegistry = registry

    when:
    task.splitDependingContainersStringAndPullImage(["${name}#0", "${name2}#1234"])

    then:
    1 * dockerClient.pull("${repository}/${name}", tag, registry)
    1 * dockerClient.pull("${repository}/${name2}", tag, registry)
  }

  def "taskAktion without dependencies"() {
    given:
    task.dependingContainers = null

    when:
    task.run()

    then:
    0 * dockerClient._
  }
}
