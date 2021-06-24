package nl.bioinf.wekainterface.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;




class FileFindServiceTest {
    @Value(value = "${temp.data.path}")
    private String tempUploadedFilesFolder;

    @Test
    void findFile() {
        String filePath = null;
        // tempUploadedFilesFolder doesnt work theirfore change the filepath to the
        // temp.data.path form the applications.properties
        File direcotryPath = new File("/Users/bengels/Desktop/temp");
        File[] fileListIndirecotry = direcotryPath.listFiles();
        String searchFile = "";
        if (fileListIndirecotry != null)
            for (File fileNamesInDirecotry : fileListIndirecotry) {
                if (fileNamesInDirecotry.getName().contains("")) {
                    searchFile = fileNamesInDirecotry.getName();
                    filePath = fileNamesInDirecotry.getAbsolutePath();
                }
            }
        assertEquals(filePath, direcotryPath + "/" + searchFile );
    }
}
