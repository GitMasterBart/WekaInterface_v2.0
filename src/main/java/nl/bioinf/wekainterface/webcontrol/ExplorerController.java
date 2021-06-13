package nl.bioinf.wekainterface.webcontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.bioinf.wekainterface.model.*;
import nl.bioinf.wekainterface.service.FileService;
import nl.bioinf.wekainterface.service.SerializationService;
import nl.bioinf.wekainterface.service.SerializationServiceUploadedFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.net.http.HttpRequest;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Marijke Eggink, Jelle Becirspahic, Bart Engels
 */

@Controller
public class ExplorerController {
    @Value("${example.data.path}")
    private String exampleFilesFolder;
    @Value("${temp.data.path}")
    private String tempFolder;
    @Autowired
    private DataReader dataReader;
    @Autowired
    private LabelCounter labelCounter;
    @Autowired
    private WekaClassifier wekaClassifier;
    @Autowired
    private SerializationService serializationService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SerializationServiceUploadedFiles serializationServiceUploadedFiles;

    @GetMapping(value = "/workbench")
    public String getWorkbench(Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) throws JsonProcessingException {
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        try {
            ArrayList<AlgorithmsInformation> deserializationObjectHistory = serializationService.deserialization((File) httpSession.getAttribute("uniqueIdHistory"));
            ArrayList<String> deserializationObjectUploadedFile = serializationServiceUploadedFiles.deserialization((File) httpSession.getAttribute("uniqueIdUpload"));
            model.addAttribute("info", deserializationObjectHistory);
            model.addAttribute("uploadedFile", deserializationObjectUploadedFile);
            redirectAttributes.addFlashAttribute("info", deserializationObjectHistory);
            redirectAttributes.addFlashAttribute("uploadedFile", deserializationObjectUploadedFile);
        } catch (NullPointerException e) {
            model.addAttribute("msg", "No History");
            model.addAttribute("uploadedFile", "No uploaded files");
            redirectAttributes.addFlashAttribute("msg", "No History");
            redirectAttributes.addFlashAttribute("uploadedFile", "No uploaded files");
        }

        return "workbench";
    }

    @PostMapping(value = "/workbench")
    public String postWorkbench(@RequestParam(name = "inputFile", required = false) MultipartFile multipart,
                                @RequestParam(name = "filename", required = false) String demoFileName,
                                Model model, RedirectAttributes redirect, HttpSession httpSession) throws Exception {

        if (httpSession.getAttribute("history") == null) {
            ArrayList<AlgorithmsInformation> algorithmsInformation = new ArrayList<>();
            httpSession.setAttribute("history", algorithmsInformation);
        }

        if (httpSession.getAttribute("uniqueIdHistory") == null) {
            String uniqueId = UUID.randomUUID().toString();
            File serFile = File.createTempFile(uniqueId, ".ser", new File("/tmp/"));
            httpSession.setAttribute("uniqueIdHistory", serFile);
        }
        if (httpSession.getAttribute("uniqueIdUpload") == null) {
            String uniqueId = UUID.randomUUID().toString();
            File serFile = File.createTempFile(uniqueId, ".ser", new File("/tmp/"));
            httpSession.setAttribute("uniqueIdUpload", serFile);
        }

        if (httpSession.getAttribute("UloadedFiles")  == null){
            ArrayList<String> uploadedFiles = new ArrayList<>();
            httpSession.setAttribute("UloadedFiles", uploadedFiles);
        }

        if (httpSession.getAttribute("demofile") == null) {
            String arffFilePath = exampleFilesFolder + '/' + demoFileName;
            File arffFile = new File(arffFilePath);
            httpSession.setAttribute("demofile", arffFile);
        }

        ArrayList<AlgorithmsInformation> history = (ArrayList<AlgorithmsInformation>) httpSession.getAttribute("history");
        if (demoFileName.equals("Select...")) demoFileName = multipart.getOriginalFilename();
        if (!demoFileName.equals("")) history.add(new AlgorithmsInformation(demoFileName, new SimpleDateFormat("HH:mm:ss")));
        serializationService.serialization(history, (File) httpSession.getAttribute("uniqueIdHistory"));

        ArrayList<String> uloadedFiles = (ArrayList<String>) httpSession.getAttribute("UloadedFiles");
        uloadedFiles.add(multipart.getOriginalFilename());
        serializationServiceUploadedFiles.serialization(uloadedFiles, (File) httpSession.getAttribute("uniqueIdUpload"));

        Instances instances;
        if (!multipart.isEmpty()){
            instances = fileService.getInstancesFromMultipart(multipart);
        } else {
            instances = fileService.getInstancesFromDemoFile(demoFileName);
        }

        if (httpSession.getAttribute("instances") == null) {
            httpSession.setAttribute("instances", instances);
        }

        labelCounter.setInstances(instances);
        labelCounter.setGroups();
        labelCounter.countLabels();

        redirect.addFlashAttribute("data", labelCounter.mapToJSON());
        redirect.addFlashAttribute("attributes", labelCounter.getAttributeArray());
        redirect.addFlashAttribute("classLabel", labelCounter.getClassLabel());
        redirect.addFlashAttribute("instances", instances);
        labelCounter.resetLabelCounter();

        return "redirect:/workbench/explore";
    }

    @GetMapping(value = "/workbench/explore")
    public String getExplorePage(Model model, HttpSession httpSession) {
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        try {
            ArrayList<AlgorithmsInformation> deserializationObjectHistory = serializationService.deserialization((File) httpSession.getAttribute("uniqueIdHistory"));
            ArrayList<String> deserializationObjectUploadedFile = serializationServiceUploadedFiles.deserialization((File) httpSession.getAttribute("uniqueIdUpload"));
            model.addAttribute("info", deserializationObjectHistory);
            model.addAttribute("uploadedFile", deserializationObjectUploadedFile);
        } catch (NullPointerException e) {
            model.addAttribute("msg", "No History");
            model.addAttribute("uploadedFile", "No uploaded files");

        }
        model.addAttribute("uploadedFile", httpSession.getAttribute("UloadedFiles"));

        return "workbench";
    }


    @PostMapping(value = "/workbench/explore")
    public String postExplorePage(@RequestParam(name = "classifier") String classifierName,
                                  Model model, RedirectAttributes redirect, HttpSession httpSession) throws Exception {

        if (httpSession.getAttribute("algorithm") == null) {
            httpSession.setAttribute("algorithm", classifierName);
        }
//        System.out.println(classifierName);
//        classifierFactory.createClassifier(classifierName, httpRequest);

        Instances instances =  (Instances)httpSession.getAttribute("instances");

        labelCounter.setInstances(instances);
        labelCounter.setGroups();
        labelCounter.countLabels();

        Evaluation evaluation = wekaClassifier.test(instances, classifierName);

        redirect.addFlashAttribute("data", labelCounter.mapToJSON());
        redirect.addFlashAttribute("attributes", labelCounter.getAttributeArray());
        redirect.addFlashAttribute("classLabel", labelCounter.getClassLabel());
        labelCounter.resetLabelCounter();
        redirect.addFlashAttribute("evaluation", evaluation);
        return "redirect:/workbench";
    }


    @GetMapping(value = "/results")
    public String getResultPage(Model model, HttpSession httpSession) throws JsonProcessingException {
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();

        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        return "workbench";
    }
}
