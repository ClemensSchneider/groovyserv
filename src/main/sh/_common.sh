#!/bin/bash
#
# Copyright 2009-2013 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

QUIET=${QUIET:-false}
DEBUG=${DEBUG:-false}

#-------------------------------------------
# OS specific support
#-------------------------------------------

OS_CYGWIN=false
OS_MSYS=false
OS_DARWIN=false
case "`uname`" in
  CYGWIN* )
    OS_CYGWIN=true
    ;;
  Darwin* )
    OS_DARWIN=true
    ;;
  MINGW* )
    OS_MSYS=true
    ;;
esac

# For Cygwin, ensure that paths are in UNIX format before anything is touched.
# When they are used by Groovy, Groovy will convert them appropriately.
if $OS_CYGWIN; then
    # Original Groovy's shell script uses only HOME instead of USERPROFILE.
    # In GroovyServ, let it be in order to unify the work directory for both cygwin and BAT.
    HOME=`cygpath --unix --ignore "$USERPROFILE"`
fi

#-------------------------------------------
# Common functions
#-------------------------------------------

# should be overridden
usage() {
    echo "usage: `basename $0`"
    exit 1
}

error_log() {
    local message="$*"
    /bin/echo "$message" 1>&2
}

info_log() {
    local message="$*"
    if ! $QUIET; then
        /bin/echo "$message" 1>&2
    fi
}

debug_log() {
    local message="$*"
    if $DEBUG; then
        /bin/echo "DEBUG: $message" 1>&2
    fi
}

die() {
    local message="$1"
    local hint="$2"
    error_log "$message"
    if [ -n "$hint" ]; then
        error_log "$hint"
    fi
    exit 1
}

is_file_exists() {
    local file=$1
    ls $file >/dev/null 2>&1
}

is_command_available() {
    local command=$1
    which $command >/dev/null 2>&1
}

is_port_listened() {
    local port=$1
    netstat -an | grep "[.:]${port} .* LISTEN" >/dev/null 2>&1
}

resolve_symlink() {
    local target=$1

    # if target is symbolic link
    if [ -L $target ]; then
        local original_filepath=`readlink $target`

        # if original is specified as absolute path
        if [ $(echo $original_filepath | cut -c 1) = "/" ]; then
            echo "$original_filepath"
        else
            echo "$(dirname $target)/$original_filepath"
        fi
    else
        echo "$target"
    fi
}

expand_path() {
    local target=$1
    if [ -d "$target" ]; then
        echo $(cd $target && pwd -P)
    elif [ -f "$target" ]; then
        local target_resolved=$(resolve_symlink $target)
        local filename=$(basename $target_resolved)
        local dir_expanded="$(expand_path $(dirname $target_resolved))"
        echo "$dir_expanded/$filename"
    else
        echo "$target"
    fi
}

get_authtoken_file() {
    echo "$GROOVYSERV_WORK_DIR/authtoken-$GROOVYSERV_PORT"
}

#-------------------------------------------
# Common variables
#-------------------------------------------

if [ "$GROOVYSERV_HOME" = "" ]; then
    GROOVYSERV_HOME="$(dirname $(dirname $(expand_path $0)))"

    # for Homebrew in Mac OS X
    if [ -d $GROOVYSERV_HOME/libexec ]; then
        GROOVYSERV_HOME="$GROOVYSERV_HOME/libexec"
    fi
else
    GROOVYSERV_HOME=`expand_path "$GROOVYSERV_HOME"`
fi
debug_log "GROOVYSERV_HOME: $GROOVYSERV_HOME"

GROOVYSERV_WORK_DIR=${GROOVYSERV_WORK_DIR:-"$HOME/.groovy/groovyserv"}
if [ ! -d "$GROOVYSERV_WORK_DIR" ]; then
    mkdir -p "$GROOVYSERV_WORK_DIR"
fi
debug_log "GROOVYSERV_WORK_DIR: $GROOVYSERV_WORK_DIR"

GROOVYSERV_HOST=${GROOVYSERV_HOST:-localhost}
debug_log "GROOVYSERV_HOST: $GROOVYSERV_HOST"

GROOVYSERV_PORT=${GROOVYSERV_PORT:-1961}
debug_log "GROOVYSERV_PORT: $GROOVYSERV_PORT"

