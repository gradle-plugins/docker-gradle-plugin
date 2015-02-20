package com.devbliss.docker.task

import com.devbliss.docker.Constant
import de.gesellix.gradle.docker.tasks.AbstractDockerTask

abstract class AbstractDockerClusterTask extends AbstractDockerTask {

    List<String> dockerAlreadyHandledList

    AbstractDockerClusterTask() {
        group = "DockerCluster"

        if (getProject().hasProperty(Constant.DOCKER__ALREADY_HANDLED_PROPERTY)) {
            String dockerAlreadyHandled = getProject().getProperty(Constant.DOCKER__ALREADY_HANDLED_PROPERTY)
            dockerAlreadyHandledList = dockerAlreadyHandled.replaceAll("\\s", "").split(',')
        } else {
            dockerAlreadyHandledList = new ArrayList<>()
        }
    }
}

