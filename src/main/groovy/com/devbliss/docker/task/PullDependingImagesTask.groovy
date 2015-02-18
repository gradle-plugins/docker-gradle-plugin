package com.devbliss.docker.task

import com.devbliss.docker.Configuration
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
    String dockerRegistry
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
        List<ServiceDependency> serviceDependenyList = ServiceDependency.parseServiceDependencies(dependingContainers)
        log.info("Pull images for services: " + serviceDependenyList)
        serviceDependenyList.each() { ServiceDependency dep ->
            if (!dockerAlreadyHandledList.contains(dep.getName())) {
                pullImageFromRegistry(dep)
            }
        }
    }

    void pullImageFromRegistry(ServiceDependency serviceDependency) {
        String imageName = "${dockerRepository}/${serviceDependency.getImageName()}"

        log.info "docker pull image for " + serviceDependency.getName()
        dockerClient.pull(imageName, versionTag, dockerRegistry)
    }
}
