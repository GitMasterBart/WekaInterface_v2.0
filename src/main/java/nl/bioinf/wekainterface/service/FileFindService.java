package nl.bioinf.wekainterface.service;

import java.io.*;

/**
 * @author Bart Engels
 */

public class FileFindService {
    public String findFile(String fileName, File direcotryPath) {
        String filePath = null;
        File[] fileListIndirecotry = direcotryPath.listFiles();
        if (fileListIndirecotry != null)
            for (File fileNamesInDirecotry : fileListIndirecotry) {
                if (fileNamesInDirecotry.getName().contains(fileName)) {
                    filePath = fileNamesInDirecotry.getAbsolutePath();
                }
            }
        return filePath;
    }
}
