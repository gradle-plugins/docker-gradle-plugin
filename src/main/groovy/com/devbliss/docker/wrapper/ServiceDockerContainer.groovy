package com.devbliss.docker.wrapper

import de.gesellix.docker.client.DockerClient

class ServiceDockerContainer {

    static public List<ServiceDockerContainer> getServiceContainers(DockerClient dockerClient) {
        List<ServiceDockerContainer> runningServiceContainers = new ArrayList()
        List dockerHostStatus = dockerClient.ps()
        println dockerHostStatus
        dockerHostStatus.each() { container ->
            runningServiceContainers << new ServiceDockerContainer(container)
        }
        return runningServiceContainers
    }

	String name
	Boolean isRunning
    String image

    ServiceDockerContainer(Map container) {
        name = container.Names[0].substring(1, container.Names[0].length())
        image = container.Image
        if (container.Status.contains('Up')) {
            isRunning = true
        } else {
            isRunning = false
        }
    }

    String getName() {
        return name
    }

    String getImage() {
        return image
    }

    Boolean isRunning() {
        return isRunning
    }

    Boolean imageIsUpToDate() {
        return image.contains(":")
    }
}

