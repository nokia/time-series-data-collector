/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author sebferrer
 * File util functions
 */
public class FileUtil {

    public static String readFile(String path, Charset encoding) {
        try {
            encoding = encoding == null ? StandardCharsets.UTF_8 : encoding;
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readFile(String path) {
        return FileUtil.readFile(path, StandardCharsets.UTF_8);
    }

    public static boolean createDirectory(String path) {
        File files = new File(path);
        if (!files.exists()) {
            if (files.mkdirs()) {
                return true;
            }
        }
        return false;
    }

    public static boolean fileExists(String filePathString) {
        File f = new File(filePathString);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    public static void emptyDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    emptyDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void write(String fileFullPath, String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileFullPath);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}