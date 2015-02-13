package com.devbliss.docker.task

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class BuildAndPushDockerImageTask extends AbstractDockerTask {

    BuildAndPushDockerImageTask() {
        description = "Build and push your images and start depending containers for this project"
        group = "Devbliss"
    }

    @Input
    @Optional
    def buildContextDirectory

    @TaskAction
    public void run() {

    }
}
