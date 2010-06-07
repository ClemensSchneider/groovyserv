#!/usr/bin/env groovy
/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jggug.kobo.groovyserv

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager


/**
 * GroovyServer runs groovy command background.
 * This makes groovy response time at startup very quicker.
 *
 * @author UEHARA Junji
 * @author NAKANO Yasuharu
 */
class GroovyServer {

    private static final int DEFAULT_PORT = 1961

    private Cookie cookie

    static void main(String[] args) {
        new GroovyServer().start()
    }

    void start() {
        try {
            FileUtils.initWorkDir()
            setupStandardStreams()
            setupSecurityManager()
            setupRunningMode()
            setupCookie()
            startServer()
        }
        catch (GroovyServerException e) {
            DebugUtils.errLog("error", e)
            System.exit(1)
        }
        catch (Throwable e) {
            DebugUtils.errLog("unexpected error", e)
            System.exit(2)
        }
    }

    private void setupStandardStreams() {
        StandardStreams.init()
    }

    private void setupSecurityManager() {
        System.setSecurityManager(new NoExitSecurityManager2()) // TODO partially override
    }

    private void setupRunningMode() {
        System.setProperty("groovy.runningmode", "server")
    }

    private void setupCookie() {
        cookie = new Cookie()
        cookie.save()
    }

    private void startServer() {
        def serverSocket = new ServerSocket(getPort())
        while (true) {
            def socket = serverSocket.accept()
            if (!socket.localSocketAddress.address.isLoopbackAddress()) { // for security
                DebugUtils.errLog "cannot accept except loopback address: ${socket}"
                continue
            }
            DebugUtils.verboseLog "accepted socket=$socket"

            startWorkerThread(cookie, socket)
        }
    }

    private void startWorkerThread(cookie, socket) {
        def threadGroup = new ThreadGroup("groovyserver:${socket.port}")
        def connection = new ClientConnection(cookie, socket, threadGroup)
        new RequestWorker(connection, threadGroup).start()
    }

    private static int getPort() {
        return System.getProperty("groovyserver.port") as int ?: DEFAULT_PORT
    }

}
