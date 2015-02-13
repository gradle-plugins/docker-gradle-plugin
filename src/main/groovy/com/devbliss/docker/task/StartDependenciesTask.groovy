package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.handler.ProgressHandler
import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * This class pulls and run depending containers in your host vm.
 * The depending containers are configured in you gradle build file.
 */
@Log
class StartDependenciesTask extends AbstractDockerTask {

    // TODO: wäre es möglich die Typen der Variablen zu annotieren bzw. per java/groovydoc zu "hinten"?
    @Input
    @Optional
    def dependingContainers
    @Input
    def dockerRepository
    @Input
    def dockerRegistry
    @Input
    def versionTag

    List<String> dockerAlreadyHandledList

    StartDependenciesTask() {
        description = "Start depending containers for this Project"
        // TODO: move group into abstract base class and rename to "Docker"
        group = "Devbliss"

        // TODO: auslagern in sprechende methode, ggf. in Basisklasse, da von mehreren Klassen so benutzt
        if (getProject().hasProperty(Configuration.DOCKER_ALREADY_HANDLED_PROPERTY)) {
            String dockerAlreadyHandled = getProject().getProperty(Configuration.DOCKER_ALREADY_HANDLED_PROPERTY)
            dockerAlreadyHandledList = DependencyStringUtils.splitServiceDependenciesString(dockerAlreadyHandled)
        } else {
            dockerAlreadyHandledList = new ArrayList<>()
        }
    }

    @TaskAction
    public void run() {
        log.info "Already handled " + dockerAlreadyHandledList
        if (dependingContainers == null) {
            return
        }
        List<String> dependingContainersList = DependencyStringUtils.splitServiceDependenciesString(dependingContainers)
        String commandArgs = getCommandArgs(dependingContainersList)

        // TODO: zusammen mit dem Logging auslagern in logListOfRunningContainers
        List<String> runningContainers = getRunningContainers()
        log.info "Running containers => " + runningContainers
        dependingContainersList.each() { dep ->
            // TODO: Klasse (z.B. DockerContainer) bauen mit getName und getPort
            // Das führt natürlich dazu, dass dependingContainersList dann nicht mehr nur List<String> ist sondern
            // entsprechend List<DockerContainer>, was auch z.B. in prepareNewContainerAlreadyHandledList angepasst werden muss
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dep)
            if (!dockerAlreadyHandledList.contains(name)) {
                // TODO: "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}" -> in Methode auslagern (am besten in DependingContainer)
                startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", port, commandArgs, runningContainers)
                // TODO: am Ende sollte startContainer so aussehen:
                /*
                startContainer(
                dependingContainer.getName(),
                dependingContainer.getImageName(),
                dependingContainer.getPort(),
                getCommand()
                )
                 */
            }
        }

        if (dockerAlreadyHandledList.size() == 0) {
            ProgressHandler progressHandler = new ProgressHandler(dockerClient, dependingContainersList)
            progressHandler.waitUnilDependenciesRun()
        }
    }

    String getCommandArgs(List<String> dependingContainersList) {
        Set newHandledList = prepareNewContainerAlreadyHandledList(dependingContainersList)
        return "-P${Configuration.DOCKER_ALREADY_HANDLED_PROPERTY}=" + newHandledList.join(",")
    }

    List<String> getRunningContainers() {
        List<String> runningContainers = []
        def dockerHostStatus = dockerClient.ps()
        dockerHostStatus.each() { container ->
            def name = container.Names[0].substring(1, container.Names[0].length())
            if (container.Status.contains('Up')) {
                runningContainers.add(name)
            }
        }
        return runningContainers
    }

    Set<String> prepareNewContainerAlreadyHandledList(List<String> additionalDependencies) {
        Set newList = [] as Set
        newList.addAll(dockerAlreadyHandledList)
        newList.addAll additionalDependencies.collect { item ->
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(item)
            return name
        }
        return newList
    }

    void startContainer(String name, String image, String port, String commandArgs, List<String> runningContainers) {
        if (runningContainers.contains(name)) {
            startDependenciesNonBlockingExec(name, commandArgs)
        } else {
            Map hostConf = prepareHostConfig(port)
            log.info("Start Container: " + name + " => " + image + " => " + hostConf)
            dockerClient.run(image.toString(), ["HostConfig": hostConf, "Cmd":commandArgs], versionTag, name)
        }
    }

    void startDependenciesNonBlockingExec(String containerName, String commandArgs) {
        List command = ["./gradlew", Configuration.TASK_NAME_START_DEPENDENCIES, commandArgs]
        Map execConfig = [
        "AttachStdin" : false,
        "Detach"      : true,
        "Tty"         : false]

        log.info "Update " + containerName + " CommandArgs: "+"./gradlew startDependencies '" + commandArgs + "'"
        dockerClient.exec(containerName, command, execConfig)
    }

    Map prepareHostConfig(String portConfig) {
        def port = getPort(portConfig)
        def tcpPort = "${port[1]}/tcp".toString()
        def hostConf = ["PortBindings": [:]]
        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])
        return hostConf
    }

    String[] getPort(String port) {
        if (port.contains("-")) {
            return port.split("-").toList()
        }
        return [port, port]
    }
}
