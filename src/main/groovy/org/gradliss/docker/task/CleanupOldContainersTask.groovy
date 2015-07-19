package org.gradliss.docker.task

import org.gradliss.docker.wrapper.ServiceDependency
import org.gradliss.docker.wrapper.ServiceDockerContainer
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

@Log
class CleanupOldContainersTask extends AbstractDockerClusterTask {

    @Input
    @Optional
    String dependingContainers

    public CleanupOldContainersTask() {
        super()
        description = "Pull images and start depending containers for this Project"
    }

    @TaskAction
    public void run() {
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
            !isImageUpToDateAndRunning(container, runningContainers)
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

    boolean isImageUpToDateAndRunning(ServiceDockerContainer container, List<String> runningContainers) {
        return container.imageIsUpToDate() && runningContainers.contains(container.getName())
    }

    void stopAndRemoveContainer(ServiceDockerContainer container) {
        dockerClient.stop(container.getName())
        dockerClient.rm(container.getName())
    }
}

