import os, os.path, sys, zipfile, shlex
import shutil, glob, fnmatch, subprocess
from pprint import pformat
from optparse import OptionParser

def main():
    base_dir = os.path.dirname(os.path.abspath(__file__))

    src_dir = os.path.join(base_dir, 'src')
    work_dir = os.path.join(base_dir, 'src_work')
    
    if os.path.isdir(work_dir):
        shutil.rmtree(work_dir)
        
    print 'Setting up source directories'    
    shutil.copytree(src_dir, work_dir)
    
    print 'Applying patches'
    if os.path.isdir(os.path.join(base_dir, 'patches')):
        apply_patches(os.path.join(base_dir, 'patches'), work_dir)


def apply_patches(patch_dir, target_dir, find=None, rep=None):
    # Attempts to apply a directory full of patch files onto a target directory.
    
    temp = os.path.abspath('temp.patch')
    cmd = cmdsplit('patch -p2 -i "%s" ' % temp)
    
    if os.name == 'nt':
        applydiff = os.path.abspath(os.path.join('applydiff.exe'))
        cmd = cmdsplit('"%s" -uf -p2 -i "%s"' % (applydiff, temp))
    
    for path, _, filelist in os.walk(patch_dir, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.patch'):
            patch_file = os.path.normpath(os.path.join(patch_dir, path[len(patch_dir)+1:], cur_file))
            target_file = os.path.join(target_dir, fix_patch(patch_file, temp, find, rep))
            process = subprocess.Popen(cmd, cwd=target_dir, bufsize=-1)
            process.communicate()

    if os.path.isfile(temp):
        os.remove(temp)
        
def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

def fix_patch(in_file, out_file, find=None, rep=None):
    in_file = os.path.normpath(in_file)
    if out_file is None:
        tmp_file = in_file + '.tmp'
    else:
        out_file = os.path.normpath(out_file)
        tmp_file = out_file
        dir_name = os.path.dirname(out_file)
        if dir_name:
            if not os.path.exists(dir_name):
                os.makedirs(dir_name)
                
    file = 'not found'
    with open(in_file, 'rb') as inpatch:
        with open(tmp_file, 'wb') as outpatch:
            for line in inpatch:
                line = line.rstrip('\r\n')
                if line[:3] in ['+++', '---', 'Onl', 'dif']:
                    if not find == None and not rep == None:
                        line = line.replace('\\', '/').replace(find, rep).replace('/', os.sep)
                    else:
                        line = line.replace('\\', '/').replace('/', os.sep)
                    outpatch.write(line + os.linesep)
                else:
                    outpatch.write(line + os.linesep)
                if line[:3] == '---':
                    file = line[line.find(os.sep, line.find(os.sep)+1)+1:]
                    
    if out_file is None:
        shutil.move(tmp_file, in_file)
    return file

if __name__ == '__main__':
    main()
