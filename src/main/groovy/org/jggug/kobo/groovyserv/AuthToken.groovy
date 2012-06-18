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
package org.jggug.kobo.groovyserv

/**
 * A connection between client process and server process in localhost
 * is authenticated by simple authToken mechanism.
 *
 * @author NAKANO Yasuharu
 */
class AuthToken {

    final String token

    AuthToken(token = null) {
        this.token = token ?: createNewAuthToken()
    }

    private static createNewAuthToken() {
        Long.toHexString(new Random().nextLong())
    }

    void save() {
        try {
            WorkFiles.AUTHTOKEN_FILE.text = token
            DebugUtils.verboseLog "Saved authToken: ${token}"
        } catch (IOException e) {
            throw new GServIOException("I/O error: AuthToken file cannot be written: ${WorkFiles.AUTHTOKEN_FILE}", e)
        }
    }

    boolean isValid(given) {
        token == given
    }

}
