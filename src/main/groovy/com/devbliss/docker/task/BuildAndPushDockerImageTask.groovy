package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask

class BuildAndPushDockerImageTask extends AbstractDockerClusterTask {

    BuildAndPushDockerImageTask() {
        super()
        description = "Build and push your images for this project"
    }
}
