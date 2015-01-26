package com.devbliss.docker.tasks

import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.Configuration
import de.gesellix.docker.client.DockerClient
import de.gesellix.gradle.docker.tasks.DockerPsTask
import groovy.util.logging.Log
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by Christian Soth <christian.soth@devbliss.com> on 19.01.15.
 */
@Log
class StartDependenciesTaskSpec extends Specification {

  def project
  def task
  def dockerClient
  String name
  String registry
  String repository
  String tag
  String name2

  def setup() {
    project = ProjectBuilder.builder().build()
    task = project.task(Configuration.TASK_NAME_START_DEPENDENCIES, type: StartDependenciesTask)
    dockerClient = Mock(DockerClient)
    task.dockerClient = dockerClient
    name = "service1"
    registry = "dockerRegistry"
    repository = "dockerRepository"
    tag = "tag"
    name2 = "service2"
  }

  def "add task to startServiceDependencies project"() {
    given:
    task.dependingContainers = 'service1#8080,service3#8082,service2#8081'
    task.dockerRepository = 'example-repository'
    task.dockerRegistry = 'example.registry:5000'
    task.versionTag = 'latest'
    task.dockerAlreadyHandledList = ['service3']
    dockerClient.ps() >> [["Names":["_service3"], "Status":"Up", "Image":"435hi3u5h345"],
      ["Names":["_service2"], "Status":"Up", "Image":"example-repository/service2:latest"]]

    when:
    task.run()

    then:
    0 * dockerClient.pull('example-repository/service3', 'latest', 'example.registry:5000')
    1 * dockerClient.pull('example-repository/service2', 'latest', 'example.registry:5000')
    1 * dockerClient.pull('example-repository/service1', 'latest', 'example.registry:5000')
//    1 * dockerClient.ps()
    1 * dockerClient.run('example.registry:5000/example-repository/service1',
      ['HostConfig': ['PortBindings': ['8080/tcp': [['HostPort': '8080']]]]
        , 'Cmd':'-Pdocker.alreadyHandled=service3,service1,service2']
      , 'latest', 'service1')
    0 * dockerClient.stop('service2')
    0 * dockerClient.rm('service2')
    0 * dockerClient.run('example.registry:5000/example-repository/service2', _, 'latest', 'service2')
    1 * dockerClient.exec('service2', _)
    0 * dockerClient.run('example.registry:5000/example-repository/service3', _, 'latest', 'service3')
    0 * dockerClient.exec('service3', _)
  }

  def "cleanupOldDependencies"() {
    given:
    task.dockerHostStatus = [["Names":["_$name"], "Image":"435hi3u5h345"], ["Names":["_$name2"], "Image":"435hi3u5h345/$name2"]]
    List<String> dependingContainersList = ["${name}#0000"]
    task.existingContainers = [name]
    task.runningContainers = [name]

    when:
    task.cleanupOldDependencies(dependingContainersList)

    then:
    1 * dockerClient.stop(name)
    1 * dockerClient.rm(name)
    0 * dockerClient.stop(name2)
    0 * dockerClient.rm(name2)
    task.existingContainers.size() == 1
    task.runningContainers.size() == 1
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
    List additional = ["test2", "test3"]
    List additional2 = ["test1", "test4"]

    when:
    Set result = task.prepareNewdockerAlreadyHandledList(additional)
    Set result2 = task.prepareNewdockerAlreadyHandledList(additional2)

    then:
    result.size() == 4
    result == ["test1", "test4", "test2", "test3"] as Set
    result2.size() == 2
    result2 == ["test1", "test4"] as Set
  }

  def "stopAndRemoveContainer"() {
    given:

    when:
    task.stopAndRemoveContainer(name)

    then:
    1 * dockerClient.stop(name)
    1 * dockerClient.rm(name)
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
    List deps = ["${name}#0", "${name2}#1234"]
    task.dockerRepository = repository
    task.versionTag = tag
    task.dockerRegistry = registry

    when:
    task.splitDependingContainersStringAndPullImage(deps)

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
