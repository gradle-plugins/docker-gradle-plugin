package com.devbliss.docker

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class DockerPluginExtension {

    @Input
    String dockerHost

    @Input
    @Optional
    Map authConfigPlain

    @Input
    @Optional
    def authConfigEncoded

    @Input
    String imageName

    @Input
    @Optional
    String versionTag

    @Input
    @Optional
    String registry

    @Input
    String repositoryName

    @Input
    File buildContextDirectory

    @Input
    @Optional
    String dependingContainers
}
