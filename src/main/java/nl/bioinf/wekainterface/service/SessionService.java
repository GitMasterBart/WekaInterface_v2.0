package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.AlgorithmsInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Marijke Eggink, Bart Engels
 */

@Service
public class SessionService {
    @Value("${tmp.filePath}")
    private String exampleFilesFolder;

    @Autowired
    private FileService fileService;

    private final Logger logger = LoggerFactory.getLogger(SessionService.class);

    public void createSessionObjects(HttpSession httpSession, String demoFileName) throws IOException {
        if (httpSession.getAttribute("history") == null) {
            ArrayList<AlgorithmsInformation> algorithmsInformation = new ArrayList<>();
            httpSession.setAttribute("history", algorithmsInformation);
        }

        if (httpSession.getAttribute("uniqueIdHistory") == null) {
            String uniqueId = UUID.randomUUID().toString();
            File serFile = File.createTempFile(uniqueId, ".ser", new File(exampleFilesFolder));
            httpSession.setAttribute("uniqueIdHistory", serFile);
        }

        if (httpSession.getAttribute("uniqueIdUpload") == null) {
            String uniqueId = UUID.randomUUID().toString();
            File serFile = File.createTempFile(uniqueId, ".ser", new File(exampleFilesFolder));
            httpSession.setAttribute("uniqueIdUpload", serFile);
        }

        if (httpSession.getAttribute("UploadedFiles")  == null){
            ArrayList<String> uploadedFiles = new ArrayList<>();
            httpSession.setAttribute("UploadedFiles", uploadedFiles);
        }

        if (httpSession.getAttribute("demofile") == null) {
            String arffFilePath = exampleFilesFolder + '/' + demoFileName;
            File arffFile = new File(arffFilePath);
            httpSession.setAttribute("demofile", arffFile);
        }
    }

    public Instances setInstances(HttpSession httpSession, MultipartFile multipart, String demoFileName)
            throws IOException {
        Instances instances;
        if (!multipart.isEmpty()){
            logger.info("File is uploaded: " + multipart.getOriginalFilename());
            instances = fileService.getInstancesFromMultipart(multipart);
        } else {
            logger.info("Demo file is chosen: " + demoFileName);
            instances = fileService.getInstancesFromDemoFile(demoFileName);
        }

        if (httpSession.getAttribute("instances") == null) {
            httpSession.setAttribute("instances", instances);
        }
        return instances;
    }

    public ArrayList<AlgorithmsInformation> setHistory(HttpSession httpSession, String demoFileName, MultipartFile multipart){
        ArrayList<AlgorithmsInformation> history = (ArrayList<AlgorithmsInformation>) httpSession.getAttribute("history");
        if (demoFileName.equals("Select...")) demoFileName = multipart.getOriginalFilename();
        if (!demoFileName.equals("")) history.add(new AlgorithmsInformation(demoFileName, new SimpleDateFormat("HH:mm:ss")));
        return history;
    }

    public ArrayList<String> setUploadedFile(HttpSession httpSession, MultipartFile multipart) {
        ArrayList<String> uploadedFiles = (ArrayList<String>) httpSession.getAttribute("UploadedFiles");
        uploadedFiles.add(multipart.getOriginalFilename());
        return uploadedFiles;
    }

    public void setClassifierName(HttpSession httpSession, String classifierName){
        if (httpSession.getAttribute("algorithm") == null) {
            httpSession.setAttribute("algorithm", classifierName);
        }
    }
}
