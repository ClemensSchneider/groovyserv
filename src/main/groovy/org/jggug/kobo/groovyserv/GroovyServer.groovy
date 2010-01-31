#!/usr/bin/env groovy
/*
 * Copyright 2009 the original author or authors.
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
package org.jggug.kobo.groovyserv

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager;
//import groovy.ui.GroovyMain;
import org.codehaus.groovy.tools.GroovyStarter;


/**
 * GroovyServer runs groovy command background.
 * This makes groovy response time at startup very quicker.
 *
 * Communication Protocol summary:
 * <pre>
 * [Client -> Server]
 *
 * Initiation
 *
 *    INITIATE<CR><LF>
 *    Cwd: <cwd><CR><LF>
 *    Arg: <argn><CR><LF>
 *    Arg: <arg1><CR><LF>
 *    Arg: <arg2><CR><LF>
 *    <CR><LF>
 *    <data from stdin><EOF>
 *
 *    where
 *     <cwd> is current working directory.
 *     <argn> is number of given commandline arguments
 *            for groovy command.
 *     <arg1><arg2>.. are commandline arguments.
 *     <CR> is carridge return (0x0d ^M).
 *     <LF> is line feed (0x0a, '\n').
 *     <data from stdin> is byte sequence from standard input.
 *
 * Heartbeat
 *    HEARTBEAT<CR><LF>
 *    Status: <CR><LF>
 *
 * [Server -> Client]
 *
 * Stream
 *
 *    STREAM<CR><LF>
 *    Channel: <id><CR><LF>
 *    Size: <size><CR><LF>
 *    ... chunk data(size bytes) ....
 *    
 *    where <id> is 'o' / 'e'.
 *               'o' means standard output of the program.
 *               'e' means standard error of the program.
 *
 * Exit
 *
 *    EXIT<CR><LF>
 *    Satus: <status><CR><LF>
 *
 * </pre>
 *
 * @author UEHARA Junji
 */
class GroovyServer implements Runnable {

  final static String HEADER_CURRENT_WORKING_DIR = "Cwd";
  final static String HEADER_ARG = "Arg";
  final static String HEADER_STATUS = "Status";
  final static int DEFAULT_PORT = 1961

  final int CR = 0x0d
  final int LF = 0x0a

  static BufferedInputStream originalIn = System.in
  static OutputStream originalOut = System.out
  static OutputStream originalErr = System.err

  Map<String, List<String>> readHeaders(ins) {
    BufferedReader bis = new BufferedReader(new InputStreamReader(ins))

    def result = [:]
    def line
    while ((line = bis.readLine()) != "") {
      def kv = line.split(':', 2);
      def key = kv[0]
      def value = kv[1]
      if (!result.containsKey(key)) {
        result[key] = []
      }
      if (value.charAt(0) == ' ') {
        value = value.substring(1);
      }
      result[key] += value
    }
    result
  }

  def soc

  def changeDir(headers) {
    def currentDir = headers[HEADER_CURRENT_WORKING_DIR][0]
    System.setProperty('user.dir', currentDir)
    PlatformMethods.chdir(currentDir)
  }

  def setupStandardStreams(ins, outs) {
        System.setIn(new MultiplexedInputStream(ins));
        System.setOut(new PrintStream(new ChunkedOutputStream(outs, 'o' as char)));
        System.setErr(new PrintStream(new ChunkedOutputStream(outs, 'e' as char)));
  }

  void run() {
    try {
      soc.withStreams { ins, outs ->
        Map<String, List<String>> headers = readHeaders(ins);

        if (System.getProperty("groovyserver.verbose") == "true") {
          headers.each {k,v ->
            originalErr.println " $k = $v"
          }
        }

        changeDir(headers);
        setupStandardStreams(ins, outs);

        try {
          List args = headers[HEADER_ARG];
          for (Iterator<String> it = headers[HEADER_ARG].iterator(); it.hasNext(); ) {
            String s = it.next();
            if (s == "-cp") {
              it.remove();
              String classpath = it.next();
              System.setProperty("groovy.classpath", classpath);
              it.remove();
            }
          }
          GroovyMain2.main(args as String[])
        }
        catch (ExitException e) {
          // GroovyMain2 throws ExitException when
          // it catches ExitException.
          outs.write((HEADER_STATUS+": "+e.exitStatus+ "\n").bytes);
          outs.write("\n".bytes);
        }
        catch (Throwable t) {
          t.printStackTrace(originalErr)
          t.printStackTrace(System.err)
        }
      }
    }
    finally {
      if (System.getProperty("groovyserver.verbose") == "true") {
        originalErr.println("socket close")
      }
      soc.close()
    }
  }

  static void main(String[] args) {

    def port = DEFAULT_PORT;
    // TODO: specify port number with commandline option.
    // But command line include options are pass to
    // original groovy command tranparently. So
    // special option is not good idea(in future,
    // it could conflict to groovy's options).
    // I hope original groovy support like "groovy -s(erver) port"
    // to "run as server" option :).

    if (System.getProperty('groovy.server.port') != null) {
      port = Integer.parseInt(System.getProperty('groovy.server.port'))
    }

    System.setProperty('groovy.runningmode', "server")

    System.setSecurityManager(new NoExitSecurityManager2());

    def serverSocket = new ServerSocket(port)

    Thread worker = null;
    while (true) {
      def soc = serverSocket.accept()

      if (soc.localSocketAddress.address.isLoopbackAddress()) {
        if (System.getProperty("groovyserver.verbose") == "true") {
          originalErr.println "accept soc="+soc
        }

        // Now, create new thraed for each connections.
        // Don't use ExecutorService or any thread pool system.
        // Because the System.in/out/err streams are used distinctly
        // by thread instance.
        // So 'new Thread()' is nesessary.
        // (moreover, new Thread created in script can't handle
        // System.in/out/err correctly.)
        //
        worker = new Thread(new GroovyServer(soc:soc), "worker").start()
      }
      else {
        System.err.println("allow connection from loopback address only")
      }
    }
  }

}

