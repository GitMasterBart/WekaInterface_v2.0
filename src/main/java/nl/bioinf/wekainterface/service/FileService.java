package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.DataReader;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {
    @Value("${example.data.path}")
    private String exampleFilesFolder;
    @Value("${temp.data.path}")
    private String tempFolder;


    @Autowired
    private DataReader dataReader;

    public Instances getInstancesFromMultipart(MultipartFile multipart) throws IOException {
        Instances instances;
        InputStream inputStream = multipart.getInputStream();

        if (multipart.getOriginalFilename().endsWith(".csv")){
            File csvFile = File.createTempFile(multipart.getOriginalFilename()+ "-", ".csv", new File(tempFolder));
            FileUtils.copyInputStreamToFile(inputStream, csvFile);
            instances = dataReader.readCsv(csvFile, ";");
        } else if (multipart.getOriginalFilename().endsWith(".arff")){
            File arffFile = File.createTempFile(multipart.getOriginalFilename()+ "-", ".arff", new File(tempFolder));
            FileUtils.copyInputStreamToFile(inputStream, arffFile);
            instances = dataReader.readArff(arffFile);
        } else {
            throw new IllegalArgumentException();
        }
        return instances;
    }

    public Instances getInstancesFromDemoFile(String demoFileName) throws IOException {
        String arffFilePath = exampleFilesFolder + '/' + demoFileName;
        File arffFile = new File(arffFilePath);
        return dataReader.readArff(arffFile);
    }

    public Instances getInstancesFromUloadedDemoFile(String demoFileName) throws IOException {
        String arffFilePath = demoFileName;
        File arffFile = new File(arffFilePath);
        return dataReader.readArff(arffFile);
    }
}
