/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package org.jggug.kobo.groovyserv.ui

import org.jggug.kobo.groovyserv.AuthToken
import org.jggug.kobo.groovyserv.ExitStatus
import org.jggug.kobo.groovyserv.GroovyClient
import org.jggug.kobo.groovyserv.GroovyServer
import org.jggug.kobo.groovyserv.WorkFiles
import org.jggug.kobo.groovyserv.exception.InvalidAuthTokenException
import org.jggug.kobo.groovyserv.platform.PlatformMethods
import org.jggug.kobo.groovyserv.utils.DebugUtils
import org.jggug.kobo.groovyserv.utils.Holders

/**
 * Facade for script of groovyserver.*
 *
 * @author Yasuharu NAKANO
 */
class ServerCLI {

    private id
    private File serverScriptPath
    private List<String> args
    private options
    private int port
    private boolean quiet

    static void main(String[] args) {
        assert args.size() > 0: "script path is required."
        new ServerCLI(args[0], args.tail().toList()).invoke()
    }

    ServerCLI(String serverScriptPath, List<String> args) {
        this.serverScriptPath = new File(serverScriptPath)
        this.args = args
        this.options = parseOptions(args)
        this.port = getPortNumber(options)
        this.quiet = options.quiet
        this.id = "ServerCLI:$port"
        WorkFiles.setUp(port) // for authtoken
        DebugUtils.verbose = options.verbose
        DebugUtils.verboseLog "${id}: Invoking by script: $serverScriptPath"
        DebugUtils.verboseLog "${id}: Server command: ${args}"
    }

    void invoke() {
        try {
            // If running as daemon, a server is started up directly.
            if (isDaemonized()) {
                startServerDirectlyAndWaitFor() // infinite blocking operation
                return
            }

            // Handling commands.
            if (options.restart) {
                shutdownServer()
            }
            if (options.kill) {
                shutdownServer()
                return
            }
            startServerAsDaemon()
        }
        catch (InvalidAuthTokenException e) {
            die "ERROR: Authtoken is invalid. Please specify a right token or just kill the process somehow."
        }
        catch (Throwable e) {
            die "ERROR: ${e.message}"
            DebugUtils.verboseLog "${id}: Failed to invoke server command", e
        }
    }

    private void shutdownServer() {
        def client = newGroovyClient()

        // If server isn't already running, it needs to do nothing.
        if (!client.isServerAvailable()) {
            println "WARN: Server is not running."
            return
        }

        // If there is no authtoken, to access to server is impossible.
        if (!WorkFiles.AUTHTOKEN_FILE.exists()) {
            die "ERROR: AuthToken file ${WorkFiles.AUTHTOKEN_FILE} is not found. Please kill the process somehow."
        }

        // Shutting down
        print "Shutting down server..."
        try {
            def exitStatus = client.connect().shutdown().waitFor().exitStatus
            if (exitStatus != ExitStatus.SUCCESS.code) {
                DebugUtils.errorLog "${id}: Failed to kill the server: exitStatus=${exitStatus}"
            }
        }
        catch (Throwable e) {
            DebugUtils.errorLog "${id}: Failed to kill the server", e
            println "" // clear for print
            die "ERROR: Failed to kill the server. Isn't the port being used by a non-groovyserv process?"
        }

        // Waiting for server down
        while (!client.isServerShutdown()) {
            print "."
            sleep 200
        }
        println "" // clear for print
        println "Server is successfully shut down."
    }

    private void startServerAsDaemon() {
        def client = newGroovyClient()

        // If server is already running, it needs to do nothing.
        if (client.isServerAvailable()) {
            println "WARN: Server is already running on ${port} port."
            return
        }

        // If there is authtoken, it might be old.
        if (WorkFiles.AUTHTOKEN_FILE.exists()) {
            println "WARN: AuthToken file ${WorkFiles.AUTHTOKEN_FILE} is found. It will be overwritten by new token."
            WorkFiles.AUTHTOKEN_FILE.delete()
        }

        // Starting
        print "Starting server..."
        daemonize() // start daemon process

        // Waiting for server up
        sleep 2000
        while (!client.isServerAvailable()) {
            print "."
            sleep 200
        }
        println "" // clear for print
        println "Server is successfully started up on $port port."
    }

    private void startServerDirectlyAndWaitFor() {
        if (Holders.groovyServer) {
            die "ERROR: unexpected status: GroovyServer is already started in the same JVM."
        }

        // Setup GroovyServer instance
        def groovyServer = new GroovyServer()
        groovyServer.port = port
        if (options.authtoken) groovyServer.authToken = new AuthToken(options.authtoken)
        if (options."allow-from") groovyServer.allowFrom = options["allow-from"]?.split(',')

        // Set holders for global access
        // This is necessary for RequestWorker's call of shutdown.
        Holders.groovyServer = groovyServer

        // Start server (blocking operation)
        groovyServer.start()
    }

    private GroovyClient newGroovyClient() {
        return new GroovyClient(host: "localhost", port: port)
    }

    private parseOptions(args) {
        def cli = new CliBuilder(usage: "${serverScriptPath.name} [options]")
        cli.with {
            h longOpt: 'help', 'show this usage'
            v longOpt: 'verbose', "verbose output to a log file"
            q longOpt: 'quiet', "suppress output to console except error message"
            k longOpt: 'kill', "kill the running groovyserver"
            r longOpt: 'restart', "restart the running groovyserver"
            p longOpt: 'port', args: 1, argName: 'port', "specify the port to listen"
            _ longOpt: 'allow-from', args: 1, argName: 'addresses', "specify optional acceptable client addresses (delimiter: comma)"
            _ longOpt: 'authtoken', args: 1, argName: 'authtoken', "specify authtoken (which is automatically generated if not specified)"
            _ longOpt: 'daemonized', "run a groovyserver as daemon (INTERNAL USE ONLY)"
        }
        def opt = cli.parse(args)
        if (!opt) die "ERROR: Failed to parse arguments."
        assert !opt.help: "help usage should be shown by a front script"
        if (opt.kill && opt.restart) die 'ERROR: options --kill and --restart cannot be specified at the same time.'
        return opt
    }

    private boolean isDaemonized() {
        return options.daemonized
    }

    private void daemonize() {
        def command = convertExecutableCommand([serverScriptPath.absolutePath, "--daemonized", "-q", * args])
        DebugUtils.verboseLog "${id}: Daemonizing by re-invoking a server script: $command"
        command.execute()
    }

    private static List<String> convertExecutableCommand(List<String> command) {
        if (PlatformMethods.isWindows()) {
            return ["cmd.exe", "/c"] + command
        }
        return command
    }

    private println(message) {
        if (!quiet) System.err.println message
    }

    private print(message) {
        if (!quiet) System.err.print message
    }

    private static die(message) {
        System.err.println message
        exit ExitStatus.COMMAND_ERROR
    }

    private static exit(ExitStatus status) {
        System.setSecurityManager(null)
        System.exit(status.code)
    }

    private static int getPortNumber(options) {
        return (options.port ?: GroovyServer.DEFAULT_PORT) as int
    }
}