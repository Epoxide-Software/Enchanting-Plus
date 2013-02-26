import sys
import os
import commands
import fnmatch
import re
import subprocess, shlex
import argparse

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-v', '--verbose',  action='store_true', help='verbose mode')
    parser.add_argument('--tags', nargs=2, metavar="tag", help='Sets the tags')
    args = parser.parse_args()

    if args.tags == None:
        tag1 = "master"
        tag2 = "beta"
    else:
        tag1 = args.tags[0]
        tag2 = args.tags[1]
        
    if args.verbose:    
        print("Obtaining changelog from git")
        
    cmd = "git checkout beta"
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        process.communicate()
    except OSError:
        print("Git not found")

    cmd = 'git log ' + tag1 + '..' + tag2 + ' --pretty=format:%s'
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        change, _ = process.communicate()
    except OSError:
        print("Git not found")
        
    table = change.split('\n')

    if args.verbose:
        print(change)

    log = [  ]

    for line in table:
        if line.startswith('#'): continue
        if line.startswith('-'): continue
        log.append(line)
        
    file = open("./resources/changelog", 'wb')
    for line in log:
        file.write('%s\n' % line)
    file.close()




if __name__ == '__main__':
    main()
