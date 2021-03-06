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
package org.jggug.kobo.groovyserv

import org.jggug.kobo.groovyserv.exception.ClientNotAllowedException
import org.jggug.kobo.groovyserv.exception.GServIOException
import org.jggug.kobo.groovyserv.exception.GServIllegalStateException
import org.jggug.kobo.groovyserv.exception.InvalidAuthTokenException
import org.jggug.kobo.groovyserv.exception.InvalidRequestHeaderException
import org.jggug.kobo.groovyserv.stream.StreamRequestInputStream
import org.jggug.kobo.groovyserv.stream.StreamResponseOutputStream
import org.jggug.kobo.groovyserv.utils.LogUtils
import org.jggug.kobo.groovyserv.utils.Holders
import org.jggug.kobo.groovyserv.utils.IOUtils

/**
 * @author NAKANO Yasuharu
 */
class ClientConnection implements Closeable {

    private static InheritableThreadLocal<ClientConnection> connectionHolder = new InheritableThreadLocal<ClientConnection>()

    final AuthToken authToken
    Socket socket

    private PipedOutputStream pipedOutputStream // to transfer from socket.inputStream
    private PipedInputStream pipedInputStream   // connected to socket.inputStream indirectly via pipedInputStream and used as System.in
    private OutputStream socketOutputStream

    private boolean closed = false
    boolean toreDownPipes = false
    private boolean silentExitStatus = false

    // They are used as System.xxx
    final InputStream ins
    final PrintStream out
    final PrintStream err

    ClientConnection(AuthToken authToken, Socket socket) {
        this.authToken = authToken
        this.socket = socket

        this.pipedOutputStream = new PipedOutputStream()
        this.pipedInputStream = new PipedInputStream(pipedOutputStream)
        this.socketOutputStream = new BufferedOutputStream(socket.outputStream)

        this.ins = StreamRequestInputStream.newIn(pipedInputStream)
        this.out = new PrintStream(StreamResponseOutputStream.newOut(socketOutputStream))
        this.err = new PrintStream(StreamResponseOutputStream.newErr(socketOutputStream))

        connectionHolder.set(this)
    }

    /**
     * @throws InvalidAuthTokenException
     * @throws InvalidRequestHeaderException
     * @throws GServIOException
     */
    InvocationRequest openSession() {
        checkAllowedClientAddress()
        def request = ClientProtocols.readInvocationRequest(this)
        LogUtils.debugLog "Protocol: ${request.protocol}"
        if (request.protocol == "simple") {
            LogUtils.debugLog "Detected 'simple' protocol"
            silentExitStatus = true
            this.out.out.noHeader = true
            this.err.out.noHeader = true
        }
        request
    }

    /**
     * @throws InvalidRequestHeaderException
     * @throws GServIOException
     */
    StreamRequest readStreamRequest() {
        ClientProtocols.readStreamRequest(this)
    }

    /**
     * @throws GServIOException
     */
    void transferStreamRequest(byte[] buff, int offset, int result) {
        try {
            pipedOutputStream.write(buff, offset, result)
            pipedOutputStream.flush()
        } catch (InterruptedIOException e) {
            throw new GServIOException("Interrupted to write to piped stream", e)
        } catch (IOException e) {
            throw new GServIOException("Failed to write to piped stream", e)
        }
    }

    /**
     * @throws GServIOException
     */
    void sendExit(int status, String message = null) {
        if (silentExitStatus) return
        try {
            socketOutputStream.with { // not to close yet
                def data = ClientProtocols.formatAsExitHeader(status, message)
                write(data)
                flush()
            }
            LogUtils.debugLog "Sent exit status: ${status} ${message ? " with the message: $message" : ""}"
        } catch (IOException e) {
            throw new GServIOException("Failed to send exit status", e)
        }
    }

    /**
     * To close socket and piped I/O stream, and tear down some relational environment.
     * This method closes the actual socket.
     */
    synchronized void close() {
        if (closed) {
            LogUtils.debugLog "Already closed"
            return
        }
        tearDownTransferringPipes()
        if (pipedInputStream) {
            IOUtils.close(pipedInputStream)
            LogUtils.debugLog "PipedInputStream is closed"
            pipedInputStream = null
        }
        if (socket) {
            // closing output stream because it needs to flush.
            // socket and socket.inputStream are also closed by closing output stream which is gotten from socket.
            IOUtils.close(socketOutputStream)
            LogUtils.debugLog "Socket is closed"
            socket = null
        }
        connectionHolder.set(null)
        closed = true
    }

    /**
     * To close PipedOutputStream as 'stdin'.
     * It's import to stop an user script safety and quietly.
     * When an user script is terminated, you must call this method.
     * Or, IOException with the message of "Write end dead" will be occurred.
     * This method doesn't close the actual socket.
     * The piped input stream isn't closed here. because it's used by a user
     * script after a source of 'stdin' is closed.
     */
    synchronized void tearDownTransferringPipes() {
        if (toreDownPipes) {
            LogUtils.debugLog "Pipes to transfer a stream request already tore down"
            return
        }
        if (pipedOutputStream) {
            IOUtils.close(pipedOutputStream)
            LogUtils.debugLog "PipedOutputStream is closed"
            pipedOutputStream = null
        }
        toreDownPipes = true
    }

    static getCurrentConnection() {
        def connection = connectionHolder.get()
        if (connection == null) {
            throw new GServIllegalStateException("Not found client connection: ${Thread.currentThread()}")
        }
        return connection
    }

    private checkAllowedClientAddress() {
        if (!isAllowedClientAddress(socket)) {
            throw new ClientNotAllowedException("Cannot accept address: actual=${socket}, allowFrom=${getAllowedAddresses()}")
        }
    }

    private static boolean isAllowedClientAddress(socket) {
        // always OK from loopback address
        if (socket.localSocketAddress.address.isLoopbackAddress()) {
            return true
        }
        return getAllowedAddresses().any { address ->
            socket.inetAddress.hostAddress == address
        }
    }

    private static List<String> getAllowedAddresses() {
        return Holders.groovyServer.allowFrom
    }
}

