package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask

/**
 *
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 */
abstract class AbstractDockerClusterTask extends AbstractDockerTask {
	AbstractDockerClusterTask() {
        group = "DockerCluster"
    }
}

