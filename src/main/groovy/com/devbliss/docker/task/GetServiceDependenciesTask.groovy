package com.devbliss.docker.task

import com.devbliss.docker.wrapper.ServiceDependency
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class GetServiceDependenciesTask extends AbstractDockerClusterTask {

    GetServiceDependenciesTask() {
        super()
        description = "Get dependencies for any individual Service."
    }

    @Input
    @Optional
    String dependingContainers

    def notRunningServiceDependencies = []

    @TaskAction
    public void run() {
        if (dependingContainers != null) {
            List<ServiceDependency> dependingContainersList = ServiceDependency.parseServiceDependencies(dependingContainers)

            dependingContainersList.each { ServiceDependency dep ->
                notRunningServiceDependencies.add(dep.getName())
            }
            println("Depending Container: " + notRunningServiceDependencies)
        } else {
            println("No depending container for service.")
        }
    }
}

