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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

#include "option.h"
#include "bool.h"
#include "config.h"

struct option_info_t option_info[] = {
    { "without-invoking-server", OPT_WITHOUT_INVOCATION_SERVER, FALSE },
    { "p", OPT_PORT, TRUE },
    { "port", OPT_PORT, TRUE },
    { "env", OPT_ENV, TRUE },
    { "env-all", OPT_ENV_ALL, FALSE },
    { "env-exclude", OPT_ENV_EXCLUDE, TRUE },
};

struct option_t client_option = {
    FALSE,
    PORT_NOT_SPECIFIED, // if -Cp not specified.
    FALSE,
    {}, // each array elements are expected to be filled with NULLs
    {}  // each array elements are expected to be filled with NULLs
};

static BOOL is_client_option(char* s)
{
    return strncmp(s, CLIENT_OPTION_PREFIX,
                   strlen(CLIENT_OPTION_PREFIX)) == 0;
}

static void set_mask_option(char ** env_mask, char* opt, char* value)
{
    char** p;
    for (p = env_mask; p-env_mask < MAX_MASK && *p != NULL; p++) {
        ;
    }
    if (p-env_mask == MAX_MASK) {
        fprintf(stderr, "ERROR: Too many option: %s %s\n", opt, value);
        exit(1);
    }
    *p = value;
}

static struct option_info_t* what_option(char* name)
{
    int j = 0;
    for (j=0; j<sizeof(option_info)/sizeof(struct option_info_t); j++) {
        if (strcmp(option_info[j].name, name) == 0) {
            return &option_info[j];
        }
    }
    return NULL;
}

void scan_options(struct option_t* option, int argc, char **argv)
{
    int i;
    if (argc <= 1) {
        return;
    }
    for (i = 1; i < argc; i++) {
        if (is_client_option(argv[i])) {
            char* name = argv[i] + strlen(CLIENT_OPTION_PREFIX);
            char* argvi_copy = argv[i];
            argv[i] = NULL;
            struct option_info_t* opt = what_option(name);

            if (opt == NULL) {
                // Just only ingore unknow -C* option.
                continue;
            }

            char* value = NULL;
            if (opt->take_value == TRUE) {
                if (i >= argc-1) {
                    fprintf(stderr, "ERROR: Option %s requires a parameter\n", argvi_copy);
                    exit(1);
                }
                i++;
                value = argv[i];
                argv[i] = NULL;
            }

            switch (opt->type) {
            case OPT_WITHOUT_INVOCATION_SERVER:
                option->without_invocation_server = TRUE;
                break;
            case OPT_PORT:
                if (sscanf(value, "%d", &option->port) != 1) {
                    fprintf(stderr, "ERROR: Invalid port number %s of option %s\n", value, argvi_copy);
                    exit(1);
                }
                break;
            case OPT_ENV:
                assert(opt->take_value == TRUE);
                set_mask_option(option->env_include_mask, name, value);
                break;
            case OPT_ENV_ALL:
                option->env_all = TRUE;
                break;
            case OPT_ENV_EXCLUDE:
                assert(opt->take_value == TRUE);
                set_mask_option(option->env_exclude_mask, name, value);
                break;
            default:
                assert(FALSE);
            }
        }
    }
}

