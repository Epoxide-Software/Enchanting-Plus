import os
import sys
import fnmatch
import shlex
import difflib
import time
from optparse import OptionParser

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)
                    
def cleanDirs(path):
    if not os.path.isdir(path):
        return
 
    files = os.listdir(path)
    if len(files):
        for f in files:
            fullpath = os.path.join(path, f)
            if os.path.isdir(fullpath):
                cleanDirs(fullpath)
 
    files = os.listdir(path)
    if len(files) == 0:
        os.rmdir(path)

def createPatches(base_dir, patchd, base, work):
    for path, _, filelist in os.walk(work, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.java'):
            file_base = os.path.normpath(os.path.join(base, path[len(work)+1:], cur_file)).replace(os.path.sep, '/')
            file_work = os.path.normpath(os.path.join(work, path[len(work)+1:], cur_file)).replace(os.path.sep, '/')
            if not os.path.isfile(file_base):
                print("Missing base file %s"%(file_base))
                continue
            fromlines = open(file_base, 'U').readlines()
            tolines = open(file_work, 'U').readlines()
            
            patch = ''.join(difflib.unified_diff(fromlines, tolines, '../' + file_base[len(base_dir)+1:], '../' + file_work[len(base_dir)+1:], '', '', n=3))
            patch_dir = os.path.join(patchd, path[len(work)+1:])
            patch_file = os.path.join(patch_dir, cur_file + '.patch')
            
            if len(patch) > 0:
                print patch_file[len(patchd)+1:]
                patch = patch.replace('\r\n', '\n')
                
                if not os.path.exists(patch_dir):
                    os.makedirs(patch_dir)
                with open(patch_file, 'wb') as fh:
                    fh.write(patch)
            else:
                if os.path.isfile(patch_file):
                    print("Deleting empty patch: %s"%(patch_file))
                    os.remove(patch_file)
                
        
def main():
    print("Creating patches")
        
    base_dir = os.path.dirname(os.path.abspath(__file__))
    
    patchd = os.path.normpath(os.path.join(base_dir, 'patches', '1_6'))
    base = os.path.normpath(os.path.join(base_dir, 'src'))
    work = os.path.normpath(os.path.join(base_dir, 'src_work6'))

    createPatches(base_dir, patchd, base, work)    
    cleanDirs(patchd)

    patchd = os.path.normpath(os.path.join(base_dir, 'patches', '1_7'))
    base = os.path.normpath(os.path.join(base_dir, 'src_work6'))
    work = os.path.normpath(os.path.join(base_dir, 'src_work7'))

    createPatches(base_dir, patchd, base, work)

    cleanDirs(patchd)
    
if __name__ == '__main__':
    main()
