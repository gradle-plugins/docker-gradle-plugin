package com.devbliss.docker.task

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
            List<String> dependingContainersList = splitServiceDependenciesString(dependingContainers)

            dependingContainersList.each { dependingContainer ->
                def (name, port) = getDependencyNameAndPort(dependingContainer)
                notRunningServiceDependencies.add(name)
            }

            println("Depending Container: " + notRunningServiceDependencies)
        } else {
            log.info("No depending container for service.")
        }
    }

    static public List<String> splitServiceDependenciesString(String dependingContainers) {
        return dependingContainers.replaceAll("\\s", "").split(",")
    }

    static public List getDependencyNameAndPort(String dependency) {
        return dependency.split("#").toList()
    }
}


