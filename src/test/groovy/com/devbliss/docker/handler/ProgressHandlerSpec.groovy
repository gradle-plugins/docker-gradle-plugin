package com.devbliss.docker.handler

import com.devbliss.docker.Configuration
import com.devbliss.docker.handler.ProgressHandler
import de.gesellix.docker.client.DockerClient
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ProgressHandlerSpec extends Specification {

    ProgressHandler handler
    DockerClient dockerClient
    List<String> dependingContainersList

    def setup() {
        dependingContainersList = ["service1", "service2"]
        dockerClient = Mock(DockerClient)
        handler = new ProgressHandler(dockerClient, dependingContainersList)
    }

    def "setRunningStateForContainer"() {
        given:
        Map<String, Map<String,Boolean>> containerList = [
            "service1": ["checked_deps": false, "running" : false]
        ]
        Map container = ["Names":["/service1"], "Status":"Up"]

        when:
        handler.setRunningStateForContainer(containerList, container)

        then:
        containerList == [
            "service1": ["checked_deps": false, "running" : true]
        ]
    }

    def "prepareStartMap"() {
        when:
        Map<String, Map<String,Boolean>> startMap = handler.prepareStartMap()

        then:
        startMap == [
            "service1": ["checked_deps": false, "running" : false],
            "service2": ["checked_deps": false, "running" : false]
        ]
    }

    def "createNewContainerItem"() {
        when:
        Map<String,Boolean> item = handler.createNewContainerItem()

        then:
        item == ["checked_deps": false, "running" : false]
    }

    def "getServiceDependencies"() {
        given:
        dockerClient.exec(_, _) >> ["plain": "Depending Container ------>[eureka-server, course-service, dementity]"]

        when:
        List<String> deps = handler.getServiceDependencies(dependingContainersList[0])

        then:
        deps == ["eureka-server", "course-service", "dementity"]
    }

    def "isContainerRunning"() {
        when:
        boolean up = handler.isContainerRunning(["Status":"Up"])
        boolean exited = handler.isContainerRunning(["Status":"Exited"])

        then:
        up == true
        exited == false
        
    }

}