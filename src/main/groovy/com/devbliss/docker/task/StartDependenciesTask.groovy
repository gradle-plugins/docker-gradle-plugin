package com.devbliss.docker.task

import com.devbliss.docker.Configuration
import com.devbliss.docker.util.DependencyStringUtils
import de.gesellix.gradle.docker.tasks.AbstractDockerTask
import groovy.util.logging.Log
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * This class pulls and run depending containers in your host vm.
 * The depending containers are configured in you gradle build file.
 *
 * Created by Christian Soth <christian.soth@devbliss.com> on 09.01.15.
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
        description = "Pull images and start depending containers for this Project"
        // TODO: move group into abstract base class and rename to "Docker"
        group = "Devbliss"

        // TODO: auslagern in sprechende methode, ggf. in Basisklasse, da von mehreren Klassen so benutzt
        if (getProject().hasProperty(Configuration.dockerAlreadyHandledProperty)) {
            String dockerAlreadyHandled = getProject().getProperty(Configuration.dockerAlreadyHandledProperty)
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

        // TODO: zusammen mit dem Logging auslagern in logListOfRunningContainers
        List<String> runningContainers = getRunningContainers()

        // TODO: in getCommand oder getCommandArgs auslagern und direkt an startContainer übergeben
        Set newHandledList = prepareNewDockerAlreadyHandledList(dependingContainersList)

        String commandArgs = "-P${Configuration.dockerAlreadyHandledProperty}=" + newHandledList.join(",")

        log.info "Running containers => " + runningContainers

        dependingContainersList.each() { dep ->
            // TODO: Klasse (z.B. DockerContainer) bauen mit getName und getPort
            // Das führt natürlich dazu, dass dependingContainersList dann nicht mehr nur List<String> ist sondern
            // entsprechend List<DockerContainer>, was auch z.B. in prepareNewDockerAlreadyHandledList angepasst werden muss
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(dep)
            if (!dockerAlreadyHandledList.contains(name)) {
                // TODO: "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}" -> in Methode auslagern (am besten in DependingContainer)
                startContainer(name, "${dockerRegistry}/${dockerRepository}/${name.split("_")[0]}", port, commandArgs)
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
    }

    // TODO: weiter nach unten. Wird erst in startContainer() benutzt, also erst danach. Da prepareNewDockerAlreadyHandledList
    // in run benutzt wird, kommt prepareHostConfig also auch erst danach
    Map prepareHostConfig(String portConfig) {
        def port = getPort(portConfig)
        def tcpPort = "${port[1]}/tcp".toString()
        def hostConf = ["PortBindings": [:]]
        hostConf["PortBindings"].put(tcpPort, [["HostPort": port[0]]])
        return hostConf
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

    void startContainer(String name, String image, String port, command) {
        if (runningContainers.contains(name)) {
            log.info "Update " + name + " CommandArgs: "+"./gradlew startDependencies '" + command + "'"
            dockerClient.exec(name, ["./gradlew", Configuration.TASK_NAME_START_DEPENDENCIES, command])
        } else {
            Map hostConf = prepareHostConfig(port)
            log.info("Start Container: " + name + " => " + image + " => " + hostConf)
            dockerClient.run(image.toString(), ["HostConfig": hostConf, "Cmd":command], versionTag, name)
        }
    }

    String[] getPort(String port) {
        if (port.contains("-")) {
            return port.split("-").toList()
        }
        return [port, port]
    }

    Set<String> prepareNewDockerAlreadyHandledList(List<String> additionalDependencies) {
        Set newList = [] as Set
        newList.addAll(dockerAlreadyHandledList)
        newList.addAll additionalDependencies.collect { item ->
            def (name, port) = DependencyStringUtils.getDependencyNameAndPort(item)
            return name
        }
        return newList
    }
}
