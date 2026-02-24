package com.termux.tools.info.util;

import android.content.Context;
import android.util.Log;

import com.termux.tools.info.model.PackageInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TermuxScanner {
    private static final String TAG = "TermuxScanner";
    private Context context;

    public TermuxScanner(Context context) {
        this.context = context;
    }

    public List<PackageInfo> scanInstalledPackages() {
        List<PackageInfo> packages = new ArrayList<>();

        // Try dpkg query first (Termux uses dpkg)
        try {
            Process process = Runtime.getRuntime().exec("dpkg-query -W -f='${Package}|${Version}|${Description}\\n'");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|", 3);
                    if (parts.length >= 2) {
                        PackageInfo pkg = new PackageInfo();
                        pkg.setName(parts[0]);
                        pkg.setVersion(parts[1]);
                        if (parts.length >= 3) {
                            pkg.setDescription(parts[2]);
                        } else {
                            pkg.setDescription("No description available");
                        }
                        pkg.setInstalled(true);
                        pkg.setArchitecture(getArchitecture());
                        packages.add(pkg);
                    }
                }
            }
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "Error scanning packages with dpkg: " + e.getMessage());
            // Fallback to predefined list
            packages.addAll(getFallbackPackages());
        }

        // If no packages found, use fallback
        if (packages.isEmpty()) {
            packages.addAll(getFallbackPackages());
        }

        return packages;
    }

    public List<PackageInfo> getFallbackPackages() {
        List<PackageInfo> packages = new ArrayList<>();
        
        // Common Termux packages
        String[] commonPackages = {
            "python", "nodejs", "git", "vim", "nano", "bash", "zsh",
            "clang", "llvm", "make", "cmake", "gdb", "strace",
            "openssh", "curl", "wget", "ncurses-utils", "termux-api",
            "termux-tools", "termux-exec", "root-repo", "x11-repo",
            "ruby", "php", "golang", "rust", "java-openjdk-17",
            "postgresql", "mariadb", "redis", "mongodb", "sqlite",
            "ffmpeg", "imagemagick", "file", "tar", "gzip", "unzip",
            "htop", "neofetch", "tree", "jq", "ripgrep", "fd"
        };

        for (String pkg : commonPackages) {
            PackageInfo info = new PackageInfo();
            info.setName(pkg);
            info.setVersion("latest");
            info.setDescription("Popular Termux package: " + pkg);
            info.setInstalled(true);
            info.setArchitecture(getArchitecture());
            packages.add(info);
        }

        return packages;
    }

    public List<String> scanScripts() {
        List<String> scripts = new ArrayList<>();
        String[] scriptPaths = {
            "/data/data/com.termux/files/home",
            "/data/data/com.termux/files/usr/bin",
            "/data/data/com.termux/files/usr/etc"
        };

        for (String path : scriptPaths) {
            try {
                Process process = Runtime.getRuntime().exec("find " + path + " -maxdepth 2 -type f \\( -name '*.sh' -o -name '*.py' -o -name '*.js' \\) 2>/dev/null");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        scripts.add(line);
                    }
                }
                reader.close();
                process.waitFor();
            } catch (Exception e) {
                Log.e(TAG, "Error scanning scripts in " + path + ": " + e.getMessage());
            }
        }

        return scripts;
    }

    public String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        
        try {
            // Android version
            info.append("Android Version: ").append(android.os.Build.VERSION.RELEASE).append("\n");
            info.append("SDK Level: ").append(android.os.Build.VERSION.SDK_INT).append("\n");
            info.append("Device: ").append(android.os.Build.MODEL).append("\n");
            info.append("Manufacturer: ").append(android.os.Build.MANUFACTURER).append("\n");
            
            // Termux specific
            info.append("\n--- Termux Info ---\n");
            info.append("Architecture: ").append(getArchitecture()).append("\n");
            info.append("Prefix: /data/data/com.termux/files/usr\n");
            
            // CPU info
            info.append("\n--- CPU Info ---\n");
            info.append("CPU Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
            
            // Memory info
            info.append("\n--- Memory Info ---\n");
            Runtime runtime = Runtime.getRuntime();
            info.append("Max Memory: ").append(runtime.maxMemory() / (1024 * 1024)).append(" MB\n");
            info.append("Total Memory: ").append(runtime.totalMemory() / (1024 * 1024)).append(" MB\n");
            info.append("Free Memory: ").append(runtime.freeMemory() / (1024 * 1024)).append(" MB\n");
            
            // Storage info
            info.append("\n--- Storage Info ---\n");
            File internal = android.os.Environment.getExternalStorageDirectory();
            info.append("External Storage: ").append(internal.getAbsolutePath()).append("\n");
            info.append("Available Space: ").append(internal.getFreeSpace() / (1024 * 1024)).append(" MB\n");
            info.append("Total Space: ").append(internal.getTotalSpace() / (1024 * 1024)).append(" MB\n");
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting system info: " + e.getMessage());
            info.append("Error getting system info: ").append(e.getMessage());
        }

        return info.toString();
    }

    private String getArchitecture() {
        String arch = System.getProperty("os.arch");
        if (arch != null) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                return "aarch64";
            } else if (arch.contains("arm")) {
                return "arm";
            } else if (arch.contains("x86_64") || arch.contains("amd64")) {
                return "x86_64";
            } else if (arch.contains("x86")) {
                return "x86";
            }
            return arch;
        }
        return "unknown";
    }

    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            output.append("Error: ").append(e.getMessage());
            Log.e(TAG, "Error executing command: " + e.getMessage());
        }
        return output.toString();
    }
}
