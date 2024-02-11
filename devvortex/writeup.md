

running ffuf for discovery with raft-large-files.txt, quickhits.txt, dns
```
ffuf -u http://devvortex.htb/FUZZ -w ~/seclists/Discovery/Web-Content/raft-large-files.txt -r -o fuzz_directories.json -e .html .php. .js
fuff -u http://FUZZ.devvortex.htb/ -w ~/seclists/Discovery/Web-Content/combined-subdomains.txt -r -o fuzz_subdomains.json
```
