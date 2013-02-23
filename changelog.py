import sys
import os
import commands
import fnmatch
import re
import subprocess, shlex

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

def main():
    print("Obtaining changelog from git")
    cmd = "git checkout beta"
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        process.communicate()
    except OSError:
        print("Git not found")

    cmd = "git log master..HEAD --pretty=format:%s"
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        change, _ = process.communicate()
    except OSError:
        print("Git not found")
        
    table = change.split('\n')

    log = [  ]

    for line in table:
        if line.startswith('#'): continue
        log.append(line)
        
    file = open("./resources/changelog", 'wb')
    for line in log:
        file.write('%s\n' % line)
    file.close()




if __name__ == '__main__':
    main()
