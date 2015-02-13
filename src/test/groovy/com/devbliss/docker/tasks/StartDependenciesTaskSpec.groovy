package com.devbliss.docker.tasks

import com.devbliss.docker.task.StartDependenciesTask
import com.devbliss.docker.Configuration
import de.gesellix.docker.client.DockerClient
import de.gesellix.gradle.docker.tasks.DockerPsTask
import groovy.util.logging.Log
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

@Log
class StartDependenciesTaskSpec extends Specification {

    Project project
    StartDependenciesTask task
    DockerClient dockerClient
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
        dockerClient.ps() >>> [
            [["Names":["_service2"], "Status":"Up"], ["Names":["_service3"], "Status":"Up"]],
            [["Names":["_service2"], "Status":"Up"], ["Names":["_service3"], "Status":"Up"], ["Names":["_service1"], "Status":"Up"]]
        ]
        dockerClient.exec(_, ["./gradlew", "serviceDependencies"]) >> ["plain": ""]

        when:
        task.run()

        then:
        1 * dockerClient.run('example.registry:5000/example-repository/service1',
            ['HostConfig': ['PortBindings': ['8080/tcp': [['HostPort': '8080']]]]
                , 'Cmd':'-Pdocker.alreadyHandled=service3,service1,service2']
            , 'latest', 'service1')
        1 * dockerClient.exec('service2',
            ['./gradlew', 'startDependencies', '-Pdocker.alreadyHandled=service3,service1,service2'],
            ['AttachStdin':false, 'Detach':true, 'Tty':false])
        0 * dockerClient.run(_, _, _, 'service2')
        0 * dockerClient.run(_, _, _, 'service3')
        0 * dockerClient.exec('service3', _)
    }

    def "prepareHostConfig"() {
        given:
        String port1 = "8080-9090"
        String port2 = "1010"

        when:
        Map hostConf1 = task.prepareHostConfig(port1)
        Map hostConf2 = task.prepareHostConfig(port2)

        then:
        hostConf1 == ['PortBindings': ['9090/tcp': [['HostPort': '8080']]]]
        hostConf2 == ['PortBindings': ['1010/tcp': [['HostPort': '1010']]]]
    }

    def "startContainer"() {
        given:
        def commandArg = "testArgument"

        when:
        task.startContainer(name, "", "", commandArg, [name])

        then:
        1 * dockerClient.exec(name,
            ["./gradlew", Configuration.TASK_NAME_START_DEPENDENCIES, commandArg],
            ["AttachStdin" : false,
            "Detach"      : true,
            "Tty"         : false])
    }

    def "prepareNewdockerAlreadyHandledList"() {
        given:
        task.dockerAlreadyHandledList = ["test1", "test4"]
        List additional = ["test2", "test3"]
        List additional2 = ["test1", "test4"]

        when:
        Set result = task.prepareNewContainerAlreadyHandledList(additional)
        Set result2 = task.prepareNewContainerAlreadyHandledList(additional2)

        then:
        result.size() == 4
        result == ["test1", "test4", "test2", "test3"] as Set
        result2.size() == 2
        result2 == ["test1", "test4"] as Set
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

    def "getCommandArgs"() {
        given:
        task.dockerAlreadyHandledList = [name]
        List<String> dependingContainersList = [name, name2]

        when:
        String commandArgs= task.getCommandArgs(dependingContainersList)

        then:
        commandArgs.equals "-P${Configuration.DOCKER_ALREADY_HANDLED_PROPERTY}=${name},${name2}".toString()
    }

    def "getRunningContainers"() {
        given:
        def service1 = "service1"
        def service2 = "service2"
        dockerClient.ps() >> [["Names":["_$service1"], "Status":"Up"], ["Names":["_$service2"], "Status":"Exited sinde 10 seconds"]]

        when:
        List<String> runningContainers = task.getRunningContainers()

        then:
        runningContainers == [service1]
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
