package com.devbliss.docker.handler

import org.fusesource.jansi.Ansi

/**
 *
 * @author Dennis Schumann <dennis.schumann@devbliss.com>
 */
class ProgressOutputGenerator {

    Ansi ansi
    Long lastRender

    ProgressOutputGenerator() {
        this.ansi = Ansi.ansi()
        lastRender = 0l
    }

    public void printServices(Map<String, Map<String,Boolean>> containerList) {
        if (System.currentTimeMillis() - lastRender < 1000 && lastRender != 0) {
            return
        }
        lastRender = System.currentTimeMillis()
        ansi.eraseScreen()
        containerList.each { container ->
            printService(container.getKey(), container.getValue().get(ProgressHandler.RUNNING))
        }
        ansi.reset()
    }

    void printService(String serviceName, boolean isRunning) {
        if (isRunning) {
            print ansi.fg(Ansi.Color.GREEN).a("${serviceName} : Running").newline()
        } else {
            print ansi.fg(Ansi.Color.RED).a("${serviceName} : Waiting").newline()
        }
    }
}
