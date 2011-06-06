/*
 * Copyright 2009-2011 the original author or authors.
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

#if defined(_MSC_VER) || defined(__MINGW32__)
#define WINDOWS
#else
#define UNIX
#endif

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

#include <sys/param.h>
#include <unistd.h>
#include <signal.h>
#include <sys/stat.h>

#include "config.h"

#ifdef WINDOWS
#include <windows.h>
#include <winsock2.h>
#include <process.h>
#include <sys/fcntl.h>
#else
#include <sys/socket.h> // AF_INET
#include <netinet/in.h> // sockaddr_in
#include <netdb.h>      // gethostbyname
#include <sys/uio.h>
#include <sys/errno.h>
#endif

#include "option.h"
#include "bool.h"
#include "session.h"

static void get_cookie(char* cookie)
{
    char* cookie_work = getenv("GROOVYSERV_COOKIE");
    if (cookie_work == NULL) {
        fprintf(stderr, "ERROR: Invalid cookie %s of GROOVYSERV_COOKIE\n", cookie_work);
        exit(1);
    }
    strcpy(cookie, cookie_work);
}

static int get_port()
{
    if (client_option.port != PORT_NOT_SPECIFIED) {
        return client_option.port;
    }

    char* port_str = getenv("GROOVYSERVER_PORT");
    if (port_str != NULL) {
        int port;
        if (sscanf(port_str, "%d", &port) != 1) {
            fprintf(stderr, "ERROR: Invalid port number %s of GROOVYSERV_PORT\n", port_str);
            exit(1);
        }
        return port;
    }

    return DESTPORT;
}

static int connect_server(char* argv0, int port)
{
    int fd = open_socket(DESTSERV, port);

    if (client_option.without_invocation_server == TRUE) {
        if (fd == -1) {
            fprintf(stderr, "DOWN: groovyserver isn't running\n");
            exit(9);
        } else {
            fprintf(stderr, "UP: groovyserver is running\n");
            exit(0);
        }
    }

    if (fd == -1) {
        fprintf(stderr, "ERROR: Failed to start up groovyserver\n");
        exit(1);
    }
    return fd;
}

static int fd_soc;

static void signal_handler(int sig) {
#ifdef WINDOWS
    send(fd_soc, "Size: -1\n\n", 10, 0);
    closesocket(fd_soc);
#else
    write(fd_soc, "Size: -1\n\n", 10);
    close(fd_soc);
#endif
    exit(1);
}

/*
 * open socket and initiate session.
 */
int main(int argc, char** argv)
{
#ifdef WINDOWS
    WSADATA wsadata;
    if (WSAStartup(MAKEWORD(1,1), &wsadata) == SOCKET_ERROR) {
        fprintf(stderr, "ERROR: Failed to create socket");
        exit(1);
    }

    // make standard output to binary mode.
    if (_setmode(_fileno(stdout), _O_BINARY) < 0) {
        fprintf(stderr, "ERROR: Failed to setmode stdout");
        exit(1);
    }
#endif

    scan_options(&client_option, argc, argv);

    int port = get_port();
    fd_soc = connect_server(argv[0], port);
    signal(SIGINT, signal_handler); // using fd_soc in handler

    char cookie[BUFFER_SIZE];
    get_cookie(cookie);

    send_header(fd_soc, argc, argv, cookie);
    int status = start_session(fd_soc);

#ifdef WINDOWS
    WSACleanup();
#endif

    return status;
}

