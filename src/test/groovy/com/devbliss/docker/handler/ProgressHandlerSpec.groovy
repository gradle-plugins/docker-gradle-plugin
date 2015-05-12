package com.devbliss.docker.handler

import com.devbliss.docker.wrapper.ServiceDependency
import de.gesellix.docker.client.DockerClient
import spock.lang.Specification

class ProgressHandlerSpec extends Specification {

    ProgressHandler handler
    DockerClient dockerClient
    List<ServiceDependency> dependingContainersList

    def setup() {
        dependingContainersList = [
            new ServiceDependency("service1#8080"),
            new ServiceDependency("service2#8082")
        ]
        dockerClient = Mock(DockerClient)
        handler = new ProgressHandler(dockerClient, dependingContainersList)
    }

    def "setRunningStateForContainer"() {
        given:
        Map<String, Map<String, Boolean>> containerList = [
                "service1": ["checked_deps": false, "running": false]
        ]
        Map container = ["Names": ["/service1"], "Status": "Up"]

        when:
        handler.setRunningStateForContainer(containerList, container)

        then:
        containerList == [
                "service1": ["checked_deps": false, "running": true]
        ]
    }

    def "prepareStartMap"() {
        when:
        Map<String, Map<String, Boolean>> startMap = handler.prepareStartMap()

        then:
        startMap == [
                "service1": ["checked_deps": false, "running": false],
                "service2": ["checked_deps": false, "running": false]
        ]
    }

    def "createNewContainerItem"() {
        when:
        Map<String, Boolean> item = handler.createNewContainerItem()

        then:
        item == ["checked_deps": false, "running": false]
    }

    def "getServiceDependencies"() {
        given:
        dockerClient.exec(_, _) >> ["plain": "dependingEcosystemServices=eureka-server#8080,course-service#1002,dementity#8081"]

        when:
        List<String> deps = handler.getServiceDependencies(dependingContainersList[0].getName())

        then:
        deps == ["eureka-server", "course-service", "dementity"]
    }

    def "waitUnilDependenciesRun"() {
        given:
        dockerClient.exec(_, _) >> ["plain": "Depending Container ------>[eureka-server]"]
        dockerClient.ps() >> [
                ["Names": ["_service1"], "Status": "Up"], ["Names": ["_service2"], "Status": "Up"],
                ["Names": ["_eureka-server"], "Status": "Up"]
        ]

        when:
        handler.waitUntilDependenciesRun()

        then:
        true //waitUntil run through
    }

    def "updateDependenciesMap"() {
        given:
        Map stateMap = new HashMap()
        stateMap[ProgressHandler.RUNNING] = true
        stateMap[ProgressHandler.RECEIVED_DEPENDENCIES] = false
        Map containerMap = ["service1": stateMap]
        dockerClient.exec(_, _) >> ["plain": "dependingEcosystemServices=eureka-server#8080,course-service#1002,dementity#8081"]

        when:
        handler.updateDependenciesMap(containerMap)

        then:
        containerMap.size() == 4
        containerMap.containsKey("eureka-server")
        containerMap.containsKey("course-service")
        containerMap.containsKey("dementity")
    }

}
