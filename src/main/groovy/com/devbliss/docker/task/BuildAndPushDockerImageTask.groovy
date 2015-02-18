package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask

class BuildAndPushDockerImageTask extends AbstractDockerTask {

    BuildAndPushDockerImageTask() {
        description = "Build and push your images for this project"
        group = "Devbliss"
    }
}
