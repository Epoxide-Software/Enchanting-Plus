package com.aesireanempire.eplus.handlers;

/**
 * Created by freyja
 */
public class ModVersion implements Comparable<ModVersion>
{
    protected String version;
    public ModVersion(String version)
    {
        this.version = version;
    }

    @Override public int compareTo(ModVersion otherModVersion)
    {
        String[] versionSplit = version.split(".");
        String[] otherModVersionSplit = otherModVersion.version.split(".");


        return 0;
    }

    @Override public boolean equals(Object obj)
    {
        return obj instanceof ModVersion && ((ModVersion) obj).version.equals(this.version);

    }

    public String getAsFileExtension()
    {
        return version.replace(".", "_");
    }

    @Override public String toString()
    {
        return version;
    }
}
