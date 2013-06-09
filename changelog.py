import sys
import os
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
    parser.add_argument('-v', '--verbose', action='store_true', help='verbose mode')
    args = parser.parse_args()

    print("Obtaining version information from git")
    cmd = "git describe --tags"
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        vers, _ = process.communicate()
    except OSError:
        vers = "v0.1-0-default"

    (major, minor, rev, githash) = re.match("v(\d+).(\d+)-(\d+)-(.*)", vers).groups()

    tag2 = "HEAD"
    tag1 = "v" + major + "." + minor

    if args.verbose:
        print("Obtaining changelog from git")

    cmd = "git checkout master"
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

    log = ["#", "#Changelog for EPLUS v" + major + "." + minor, "#", ""]

    for line in table:
        if not line.startswith('-'): continue
        log.append(line)

    file = open("./resources/changelog", 'wb')
    for line in log:
        file.write('%s\n' % line)
    file.close()


if __name__ == '__main__':
    main()
