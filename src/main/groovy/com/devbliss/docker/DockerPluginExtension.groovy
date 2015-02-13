package com.devbliss.docker

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * @todo Ist offensichtlich -> Kommentar entfernen
 * Configuration extension for docker tasks.
 */
class DockerPluginExtension {

    @Input
    def dockerHost

    @Input
    @Optional
    def authConfigPlain

    @Input
    @Optional
    def authConfigEncoded

    @Input
    def imageName

    @Input
    @Optional
    def versionTag

    @Input
    @Optional
    def registryName

    @Input
    def repositoryName

    @Input
    def buildContextDirectory

    @Input
    @Optional
    def dependingContainers
}
