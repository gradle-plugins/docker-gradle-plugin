package org.gradliss.docker.task

import org.gradliss.docker.wrapper.ServiceDockerContainer
import groovy.util.logging.Log
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class stops all running containers in your configured host vm.
 */
@Log
class StopAllRunningContainersTask extends AbstractDockerClusterTask {

    StopAllRunningContainersTask() {
        super()
        description = "Stops all running docker containers in your host vm"
    }

    @TaskAction
    public void run() {
        List<ServiceDockerContainer> serviceContainers = ServiceDockerContainer.getServiceContainers(dockerClient)
        serviceContainers.each() { ServiceDockerContainer container ->
            if (container.isRunning()) {
                log.info("Container " + container.getName() + " is running")
                dockerClient.stop(container.getName())
                log.info("Container " + container.getName() + " was stopped")
            }
        }
    }
}
