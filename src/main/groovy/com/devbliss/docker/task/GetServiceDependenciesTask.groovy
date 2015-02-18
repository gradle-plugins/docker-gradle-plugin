package com.devbliss.docker.task

import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

@Log
class GetServiceDependenciesTask extends AbstractDockerTask {

    GetServiceDependenciesTask() {
        description = "Get dependencies for any individual Service."
        group = "Devbliss"
    }

    @Input
    @Optional
    def dependingContainers

    def notRunningServiceDependencies = []

    @TaskAction
    public void run() {
        if (dependingContainers != null) {
            List<String> dependingContainersList = DependencyStringUtils.splitServiceDependenciesString(dependingContainers)

            dependingContainersList.each { dependingContainer ->
                def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dependingContainer)
                notRunningServiceDependencies.add(name)
            }

            println("Depending Container: " + notRunningServiceDependencies)
        } else {
            log.info("No depending container for service.")
        }
    }
}


