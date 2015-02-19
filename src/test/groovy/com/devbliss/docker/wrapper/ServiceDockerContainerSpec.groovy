package com.devbliss.docker.wrapper

import de.gesellix.docker.client.DockerClient
import spock.lang.Specification

class ServiceDockerContainerSpec extends Specification {

    String serviceName, serviceName2
    List servicesDocker

    def setup() {
        serviceName = "service"
        serviceName2 = "service2"
        servicesDocker = [
                ["Names": ["_$serviceName"], "Image": "435hi3u5h345", "Status": "Up"],
                ["Names": ["_$serviceName2"], "Image": "${serviceName2}:latest", "Status": "Exited"]
        ]
    }

    def "getServiceContainers"() {
        setup:
        DockerClient dockerClient = Mock(DockerClient)
        dockerClient.ps() >> servicesDocker

        when:
        List<ServiceDockerContainer> services = ServiceDockerContainer.getServiceContainers(dockerClient)

        then:
        services[0].getName() == serviceName
        services[0].isRunning() == true
        services[0].getImage() == "435hi3u5h345"
        services[0].imageIsUpToDate() == false

        services[1].getName() == serviceName2
        services[1].isRunning() == false
        services[1].getImage() == "${serviceName2}:latest"
        services[1].imageIsUpToDate() == true
    }
}
