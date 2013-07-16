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
    cmd = "git tag"
    try:
        process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        tags, _ = process.communicate()
    except OSError:
        print("Git not found")

    tagList = tags.split('\n')
    last = tagList[-3]
    tag = tagList[-2]

    (majorLast, minorLast, _) = re.match(r"v(\d+).(\d+(\.\d+|\w)?)", last).groups()
    (major, minor, _) = re.match(r"v(\d+).(\d+(\.\d+|\w)?)", tag).groups()

    tag2 = "HEAD"
    tag1 = tag

    if args.verbose:
        print("Obtaining changelog from git")

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

    file = open("./resources/changelog.txt", 'wb')
    for line in log:
        file.write('%s\r\n' % line)
    file.close()


if __name__ == '__main__':
    main()
