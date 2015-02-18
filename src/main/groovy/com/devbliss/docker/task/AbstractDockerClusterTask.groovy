package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import de.gesellix.gradle.docker.tasks.AbstractDockerTask

/**
 *
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 */
abstract class AbstractDockerClusterTask extends AbstractDockerTask {

    List<String> dockerAlreadyHandledList

	AbstractDockerClusterTask() {
        group = "DockerCluster"

        if (getProject().hasProperty(Configuration.DOCKER_ALREADY_HANDLED_PROPERTY)) {
            String dockerAlreadyHandled = getProject().getProperty(Configuration.DOCKER_ALREADY_HANDLED_PROPERTY)
            dockerAlreadyHandledList = dockerAlreadyHandled.replaceAll("\\s", "").split(',')
        } else {
            dockerAlreadyHandledList = new ArrayList<>()
        }
    }
}

