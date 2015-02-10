package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 *
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 */
@Log
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
        List<String> dependingContainersList = DependencyStringUtils.splitServiceDependenciesString(dependingContainers)
        log.info("Pull images for services: " + dependingContainersList)
        dependingContainersList.each() { dep ->
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dep)
            if (!dockerAlreadyHandledList.contains(name)) {
                pullImageFromRegistry(name.split("_")[0])
            }
        }
    }

    void pullImageFromRegistry(String name) {
        def imageName = "${dockerRepository}/${name}"
        def tag = versionTag
        def registry = dockerRegistry

        log.info "docker pull" + name
        dockerClient.pull(imageName, tag, registry)
    }
}
