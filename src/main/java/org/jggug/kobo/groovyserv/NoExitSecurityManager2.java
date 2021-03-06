/*
 * Copyright (C) 2006-2007 the original author or authors.
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
package org.jggug.kobo.groovyserv;

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager;


/**
 * @author UEHARA Junji
 * @author NAKANO Yasuharu
 */
public class NoExitSecurityManager2 extends NoExitSecurityManager {

    /**
     * Always throws {@link SystemExitException}.
     * @throws SystemExitException when System.exit() is called
     */
    public void checkExit(final int code) {
        throw new SystemExitException(code, "System.exit(" + code + ") is called");
    }

}
