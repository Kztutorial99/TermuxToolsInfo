package com.termux.tools.info.model;

import java.io.Serializable;

public class PackageInfo implements Serializable {
    private String name;
    private String version;
    private String description;
    private String architecture;
    private String maintainer;
    private String homepage;
    private long installedSize;
    private boolean isInstalled;

    public PackageInfo() {
    }

    public PackageInfo(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.isInstalled = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public long getInstalledSize() {
        return installedSize;
    }

    public void setInstalledSize(long installedSize) {
        this.installedSize = installedSize;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public String getFormattedSize() {
        if (installedSize <= 0) return "Unknown";
        if (installedSize < 1024) return installedSize + " B";
        if (installedSize < 1024 * 1024) return (installedSize / 1024) + " KB";
        if (installedSize < 1024 * 1024 * 1024) return (installedSize / (1024 * 1024)) + " MB";
        return (installedSize / (1024 * 1024 * 1024)) + " GB";
    }
}
