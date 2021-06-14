package nl.bioinf.wekainterface.service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileFindServiceTest {

    @Test
    void findFile() {
        String filePath = null;
        File direcotryPath = new File("/Users/bengels/§Desktop/temp");
        File[] fileListIndirecotry = direcotryPath.listFiles();
        if (fileListIndirecotry != null)
            for (File fileNamesInDirecotry : fileListIndirecotry) {
                if (fileNamesInDirecotry.getName().contains("")) {
                    filePath = fileNamesInDirecotry.getAbsolutePath();
                }
            }
    }
}
