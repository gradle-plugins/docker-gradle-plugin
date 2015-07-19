package org.gradliss.docker.task

import org.gradliss.docker.Constant
import org.gradliss.docker.handler.ProgressHandler
import org.gradliss.docker.wrapper.ServiceDependency
import org.gradliss.docker.wrapper.ServiceDockerContainer
import de.gesellix.docker.client.DockerClientException
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * This task starts other containers recursively.
 */
@Log
class StartDependenciesTask extends AbstractDockerClusterTask {

    @Input
    @Optional
    String dependingContainers
    @Input
    String dockerRepository
    @Input
    String registry
    @Input
    String versionTag

    StartDependenciesTask() {
        super()
        description = "Start depending containers for this Project"
    }

    @TaskAction
    public void run() {
        log.info "Already handled " + dockerAlreadyHandledList
        if (dependingContainers == null) {
            return
        }
        List<ServiceDependency> dependingContainersList = ServiceDependency.parseServiceDependencies(dependingContainers)
        startAndUpdateDependencies(dependingContainersList)

        if (dockerAlreadyHandledList.size() == 0) {
            ProgressHandler progressHandler = new ProgressHandler(dockerClient, dependingContainersList)
            progressHandler.waitUntilDependenciesRun()
        }
    }

    String getCommandArgs(List<ServiceDependency> dependingContainersList) {
        Set newHandledSet = prepareNewContainerAlreadyHandledList(dependingContainersList)
        return "-P${Constant.DOCKER__ALREADY_HANDLED_PROPERTY}=" + newHandledSet.join(",")
    }

    void startAndUpdateDependencies(List<ServiceDependency> dependingContainersList) {
        String commandArgs = getCommandArgs(dependingContainersList)
        List<String> runningContainers = getRunningContainers()
        log.info "Running containers => " + runningContainers
        dependingContainersList.each() { ServiceDependency dep ->
            if (!dockerAlreadyHandledList.contains(dep.getName())) {
                startContainer(dep, commandArgs, runningContainers)
            }
        }
    }

    List<String> getRunningContainers() {
        List<ServiceDockerContainer> serviceDockerContainer = ServiceDockerContainer.getServiceContainers(dockerClient)
        List<String> runningContainers = []
        serviceDockerContainer.each() { ServiceDockerContainer container ->
            if (container.isRunning()) {
                runningContainers.add(container.getName())
            }
        }
        return runningContainers
    }

    Set<String> prepareNewContainerAlreadyHandledList(List<ServiceDependency> additionalDependencies) {
        Set newList = [] as Set
        newList.addAll(dockerAlreadyHandledList)
        newList.addAll additionalDependencies.collect { ServiceDependency dep ->
            return dep.getName()
        }
        return newList
    }

    void startContainer(ServiceDependency serviceDependency, String commandArgs, List<String> runningContainers) {
        String image = "${registry}/${dockerRepository}/${serviceDependency.getImageName()}"
        String name = serviceDependency.getName()
        if (runningContainers.contains(name)) {
            startDependenciesNonBlockingExec(name, commandArgs)
        } else {
            Map hostConf = prepareHostConfig(serviceDependency)
            log.info("Start Container: " + name + " => " + image + " => " + hostConf)
            dockerClient.run(image.toString(), ["HostConfig": hostConf, "Cmd":commandArgs], versionTag, name)
        }
    }

    void startDependenciesNonBlockingExec(String containerName, String commandArgs) {
        List command = ["./gradlew", Constant.TASK_NAME__START_DEPENDENCIES, commandArgs]
        Map execConfig = [
        "AttachStdin" : false,
        "Detach"      : true,
        "Tty"         : false]

        log.info "Update " + containerName + " CommandArgs: "+"./gradlew startDependencies '" + commandArgs + "'"
        try {
            dockerClient.exec(containerName, command, execConfig)
        } catch (DockerClientException dcx) {
            log.warning "Error on update dependencies of " + containerName + ". This happens to dependencies that have no gradle setup or docker plugin inside."
            //Is not a gradle project or doesn't use this docker plugin
        }
    }

    Map prepareHostConfig(ServiceDependency serviceDependency) {
        String[] port = serviceDependency.getPortConfiguration()
        String tcpPort = "${port[1]}/tcp".toString()
        Map hostConf = ["PortBindings": [:]]
        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])
        return hostConf
    }
}
