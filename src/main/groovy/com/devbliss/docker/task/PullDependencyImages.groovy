package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

@Log
// TODO: PullDependingImages wäre besseres Englisch, außerdem ist das der einzige Task der nicht Task im Namen hat
// -> PullDependingImagesTask
class PullDependencyImages extends AbstractDockerTask {

    @Input
    @Optional
    def dependingContainers
    @Input
    def dockerRepository
    @Input
    def dockerRegistry
    @Input
    def versionTag

    List<String> dockerAlreadyHandledList

    PullDependencyImages() {
        // TODO: siehe Anmerkungen in StartDependenciesTask()
        description = "Pull all depending images for this Project"
        group = "Devbliss"

        if (getProject().hasProperty(Configuration.dockerAlreadyHandledProperty)) {
            String dockerAlreadyHandled = getProject().getProperty(Configuration.dockerAlreadyHandledProperty)
            dockerAlreadyHandledList = dockerAlreadyHandled.replaceAll("\\s", "").split(',')
        } else {
            dockerAlreadyHandledList = new ArrayList<>()
        }
    }

    @TaskAction
    public void run() {
        if (dependingContainers == null) {
            return
        }
        // TODO: auch hier wieder die List<DockerContainer> einsetzen
        List<String> dependingContainersList = DependencyStringUtils.splitServiceDependenciesString(dependingContainers)
        log.info("Pull images for services: " + dependingContainersList)
        dependingContainersList.each() { dep ->
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dep)
            if (!dockerAlreadyHandledList.contains(name)) {
                // TODO: Umschreiben zu -> pullImageForContainerFromRegistry(dependingContainer)
                pullImageFromRegistry(name.split("_")[0])
            }
        }
    }

    void pullImageFromRegistry(String name) {
        // TODO: sollten auch Attribute der DockerContainer-Klasse werden
        def imageName = "${dockerRepository}/${name}"
        def tag = versionTag
        def registry = dockerRegistry

        log.info "docker pull" + name
        dockerClient.pull(imageName, tag, registry)
    }
}
