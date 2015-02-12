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
class CleanupOldContainersTask extends AbstractDockerTask {

    @Input
    @Optional
    def dependingContainers

    List<String> dockerAlreadyHandledList

    public CleanupOldContainersTask() {
        // TODO: siehe Anmerkungen in StartDependenciesTask()
        description = "Pull images and start depending containers for this Project"
        group = "Devbliss"

        if (getProject().hasProperty(Configuration.dockerAlreadyHandledProperty)) {
            String dockerAlreadyHandled = getProject().getProperty(Configuration.dockerAlreadyHandledProperty)
            dockerAlreadyHandledList = DependencyStringUtils.splitServiceDependenciesString(dockerAlreadyHandled)
        } else {
            dockerAlreadyHandledList = new ArrayList<>()
        }
    }

    @TaskAction
    public void run() {
        List dockerHostStatus = dockerClient.ps()
        List existingContainers = []
        List runningContainers = []

        // TODO: auch hier wäre es wieder schön die in StartDependenciesTask angesprochene DockerContainer-Klasse zu
        // verwenden. Dafür müsste man dann wohl die Rückgabe von dockerClient.ps() entsprechend umwandeln.
        // So wird man aber wahrscheinlich die Utils-Klasse komplett los
        dockerHostStatus.each() { container ->
            def name = DependencyStringUtils.getServiceNameFromContainer(container)
            existingContainers.add(["name":name, "image": container.Image])
            if (container.Status.contains('Up')) {
                runningContainers.add(name)
            }
        }

        // TODO: wäre dann z.B. DockerContainer.getDependingContainers(), was wiederum eine Liste von DockerContainer-Objekten wäre
        List<String> dependingContainersList = DependencyStringUtils.splitServiceDependenciesString(dependingContainers)
        List existingDependencies = getExistingDependencies(existingContainers, dependingContainersList)
        List containerToClean = getOutdatedContainer(existingDependencies, runningContainers)

        cleanupDependencies(containerToClean)
    }

    List getOutdatedContainer(List containerList, List runningContainers) {
        containerList.findAll{ container ->
            !isImageUptodateAndRunning(container, runningContainers)
        }
    }

    List getExistingDependencies(List existingContainers, List dependingContainersList) {
        existingContainers.findAll { container ->
            def dependency = dependingContainersList.find { dep ->
                def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dep)
                return !dockerAlreadyHandledList.contains(name) && container.name.equals(name.split("_")[0])
            }
            return dependency != null
        }
    }

    void cleanupDependencies(List containerToClean) {
        containerToClean.each() { container ->
            log.info "clean $container"
            stopAndRemoveContainer(container.name)
        }
    }

    // TODO: isImageUpToDateAndRunning, sonst gut umgesetzt
    boolean isImageUptodateAndRunning(Map container, List runningContainers) {
        return container.image.contains(container.name) && runningContainers.contains(container.name)
    }

    void stopAndRemoveContainer(String name) {
        dockerClient.stop(name)
        dockerClient.rm(name)
    }
}

