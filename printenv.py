#!/usr/bin/env python

# Dumps environment variables into specified file.
# Format: zero-separated "name=value" pairs in platform encoding.

import os
import sys

if len(sys.argv) != 2:
    raise Exception('Exactly one argument expected')

f = open(sys.argv[1], 'wb')
try:
    for key, value in os.environ.items():
        s = '%s=%s\0' % (key, value)
        try:
            f.write(s.encode('utf-8'))
        except Exception as e:
            sys.stdout.write(key + '=' + value + '\n')
finally:
    f.close()
