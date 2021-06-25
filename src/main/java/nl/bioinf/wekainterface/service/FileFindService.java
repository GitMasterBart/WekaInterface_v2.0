package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.InstanceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Bart Engels
 */

public class FileFindService {
    public String findFile(String fileName, File directoryPath) {
        Logger logger = LoggerFactory.getLogger(FileFindService.class);
        String filePath = null;
        File[] fileListInDirectory = directoryPath.listFiles();
        if (fileListInDirectory != null){
            for (File fileNamesInDirectory : fileListInDirectory) {
                if (fileNamesInDirectory.getName().contains(fileName)) {
                    logger.info("Temporary file " + fileName + " found in " + directoryPath);
                    filePath = fileNamesInDirectory.getAbsolutePath();
                }
            }
        } else {
            logger.warn("Temporary file " + fileName + " not found in " + directoryPath);
        }
        return filePath;
    }
}
