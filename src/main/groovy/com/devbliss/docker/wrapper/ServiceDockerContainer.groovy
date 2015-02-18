package com.devbliss.docker.wrapper

import de.gesellix.docker.client.DockerClient

class ServiceDockerContainer {

    static public List<ServiceDockerContainer> getServiceContainers(DockerClient dockerClient) {
        List<ServiceDockerContainer> runningServiceContainers = []
        List dockerHostStatus = dockerClient.ps()
        dockerHostStatus.each() { container ->
            runningServiceContainers << new ServiceDockerContainer(container)
        }
    }

	String name
	Boolean isRunning

    ServiceDockerContainer(Map container) {
        name = container.Names[0].substring(1, container.Names[0].length())
        if (container.Status.contains('Up')) {
            isRunning = true
        } else {
            isRunning = false
        }
    }

    String getName() {
        return name
    }

    Boolean isRunning() {
        return isRunning
    }
}

