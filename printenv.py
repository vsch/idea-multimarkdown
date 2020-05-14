#!/usr/bin/env python

# Dumps environment variables into specified file.
# Format: zero-separated "name=value" pairs in platform encoding.

import os
import sys

if len(sys.argv) != 2:
    raise Exception('Exactly one argument expected')

f = open(sys.argv[1], 'w')
try:
    for key, value in os.environ.items():
        try:
            f.writelines([key, '=', value, '\0'])
        except Exception as e:
            sys.stdout.write(key + '=' + value + '\n')
finally:
    f.close()
