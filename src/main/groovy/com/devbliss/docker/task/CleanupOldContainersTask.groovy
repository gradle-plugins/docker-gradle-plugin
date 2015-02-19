package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.wrapper.ServiceDependency
import com.devbliss.docker.wrapper.ServiceDockerContainer
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

@Log
class CleanupOldContainersTask extends AbstractDockerClusterTask {

    @Input
    @Optional
    def dependingContainers

    public CleanupOldContainersTask() {
        super()
        description = "Pull images and start depending containers for this Project"
    }

    @TaskAction
    public void run() {
        List dockerHostStatus = dockerClient.ps()
        List<ServiceDockerContainer> serviceDockerContainer = ServiceDockerContainer.getServiceContainers(dockerClient)
        List<String> runningContainers = []

        serviceDockerContainer.each() { ServiceDockerContainer container ->
            if (container.isRunning()) {
                runningContainers.add(container.getName())
            }
        }

        List<ServiceDependency> serviceDependencies = ServiceDependency.parseServiceDependencies(dependingContainers)
        List<ServiceDockerContainer> existingDependencies = getExistingDependencies(serviceDockerContainer, serviceDependencies)
        List<ServiceDockerContainer> containerToClean = getOutdatedContainer(existingDependencies, runningContainers)

        cleanupDependencies(containerToClean)
    }

    List<ServiceDockerContainer> getOutdatedContainer(List<ServiceDockerContainer> containerList, List<String> runningContainers) {
        return containerList.findAll { container ->
            !isImageUptodateAndRunning(container, runningContainers)
        }
    }

    List<ServiceDockerContainer> getExistingDependencies(List<ServiceDockerContainer> existingContainers, List<ServiceDependency> dependingContainersList) {
        return existingContainers.findAll { ServiceDockerContainer container ->
            def dependency = dependingContainersList.find { ServiceDependency dep ->
                return !dockerAlreadyHandledList.contains(dep.getName()) && container.getName().equals(dep.getName())
            }
            return dependency != null
        }
    }

    void cleanupDependencies(List<ServiceDockerContainer> containerToClean) {
        containerToClean.each() { ServiceDockerContainer container ->
            log.info "clean ${container.getName()}"
            stopAndRemoveContainer(container)
        }
    }

    boolean isImageUptodateAndRunning(ServiceDockerContainer container, List<String> runningContainers) {
        return container.imageIsUpToDate() && runningContainers.contains(container.getName())
    }

    void stopAndRemoveContainer(ServiceDockerContainer container) {
        dockerClient.stop(container.getName())
        dockerClient.rm(container.getName())
    }
}

