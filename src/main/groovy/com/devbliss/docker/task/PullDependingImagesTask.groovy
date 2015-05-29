package com.devbliss.docker.task

import com.devbliss.docker.wrapper.ServiceDependency
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

@Log
class PullDependingImagesTask extends AbstractDockerClusterTask {

    @Input
    @Optional
    String dependingContainers
    @Input
    String dockerRepository
    @Input
    String registry
    @Input
    String versionTag

    PullDependingImagesTask() {
        super()
        description = "Pull all depending images for this Project"
    }

    @TaskAction
    public void run() {
        if (dependingContainers == null) {
            return
        }
        List<ServiceDependency> serviceDependencyList = ServiceDependency.parseServiceDependencies(dependingContainers)
        log.info("Pull images for services: " + serviceDependencyList)
        serviceDependencyList.each() { ServiceDependency dep ->
            if (!dockerAlreadyHandledList.contains(dep.getName())) {
                pullImageFromRegistry(dep)
            }
        }
    }

    void pullImageFromRegistry(ServiceDependency serviceDependency) {
        String imageName = "${dockerRepository}/${serviceDependency.getImageName()}"

        log.info "docker pull image for " + serviceDependency.getName()
        dockerClient.pull(imageName, versionTag, ".", registry)
    }
}
