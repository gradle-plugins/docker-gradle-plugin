package com.devbliss.docker.wrapper

class ServiceDockerContainer {
	String name

    ServiceDockerContainer(Map container) {
        name = container.Names[0].substring(1, container.Names[0].length())
    }

    String getName() {
        return name
    }
}

