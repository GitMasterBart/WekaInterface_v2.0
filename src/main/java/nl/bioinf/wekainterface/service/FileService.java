package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.InstanceReader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private InstanceReader instanceReader;

    Logger logger = LoggerFactory.getLogger(FileService.class);

    public Instances getInstancesFromMultipart(MultipartFile multipart) throws IOException {
        Instances instances;
        InputStream inputStream = multipart.getInputStream();

        if (multipart.getOriginalFilename().endsWith(".csv")){
            logger.info(multipart.getOriginalFilename() + " is a .csv file");
            File csvFile = File.createTempFile(multipart.getOriginalFilename()+ "-", ".csv", new File(tempFolder));
            FileUtils.copyInputStreamToFile(inputStream, csvFile);
            instances = instanceReader.readCsv(csvFile, ";");
        } else if (multipart.getOriginalFilename().endsWith(".arff")){
            logger.info(multipart.getOriginalFilename() + " is a .arff file");
            File arffFile = File.createTempFile(multipart.getOriginalFilename()+ "-", ".arff", new File(tempFolder));
            FileUtils.copyInputStreamToFile(inputStream, arffFile);
            instances = instanceReader.readArff(arffFile);
        } else {
            logger.error(multipart.getOriginalFilename() + ", this file extension is not supported by this application");
            throw new IllegalArgumentException();
        }
        return instances;
    }

    public Instances getInstancesFromDemoFile(String demoFileName) throws IOException {
        String arffFilePath = exampleFilesFolder + '/' + demoFileName;
        File arffFile = new File(arffFilePath);
        return instanceReader.readArff(arffFile);
    }

    public Instances getInstancesFromUploadedDemoFile(String demoFileName) throws IOException {
        String arffFilePath = demoFileName;
        File arffFile = new File(arffFilePath);
        return instanceReader.readArff(arffFile);
    }
}
