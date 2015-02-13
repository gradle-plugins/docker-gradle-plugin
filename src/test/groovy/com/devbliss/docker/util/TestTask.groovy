package com.devbliss.docker.util

import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class TestTask extends AbstractDockerTask {

    @Input
    def imageName = project.devblissDocker.imageName

    @Input
    @Optional
    def versionTag = project.devblissDocker.versionTag

    @Input
    @Optional
    def registryName = project.devblissDocker.registryName

    @Input
    def repositoryName = project.devblissDocker.repositoryName

    @Input
    def buildContextDirectory = project.devblissDocker.buildContextDirectory

    @Input
    def dependingContainers = 'service1#8080,service2#8081,service3#8082'

    @TaskAction
    def run() {
    }
}
