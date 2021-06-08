package nl.bioinf.wekainterface.webcontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.bioinf.wekainterface.model.AlgortihmsInformation;
import nl.bioinf.wekainterface.model.DataReader;
import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.model.WekaClassifier;
import nl.bioinf.wekainterface.service.ClassificationService;
import nl.bioinf.wekainterface.service.FileService;
import nl.bioinf.wekainterface.service.SerializationService;
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;
import java.io.InputStream;
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
    private ClassifierFactory classifierFactory;
    @Autowired
    private SerializationService serializationService;
    @Autowired
    private FileService fileService;

    @GetMapping(value = "/workbench")
    public String getWorkbench(Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) throws JsonProcessingException {
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        try {
            ArrayList<AlgortihmsInformation> deserializationObject = serializationService.deserialization((File) httpSession.getAttribute("uniqueId"));
            model.addAttribute("info", deserializationObject);
            redirectAttributes.addFlashAttribute("info", deserializationObject);
        } catch (NullPointerException e) {
            model.addAttribute("msg", "No History");
            redirectAttributes.addFlashAttribute("msg", "No History");
        }

        return "workbench";
    }

    @PostMapping(value = "/workbench")
    public String postWorkbench(@RequestParam(name = "inputFile", required = false) MultipartFile multipart,
                                @RequestParam(name = "filename", required = false) String demoFileName,
                                Model model, RedirectAttributes redirect, HttpSession httpSession) throws Exception {

        if (httpSession.getAttribute("history") == null) {
            ArrayList<AlgortihmsInformation> algorithmsInformation = new ArrayList<>();
            httpSession.setAttribute("history", algorithmsInformation);
        }

        if (httpSession.getAttribute("uniqueId") == null) {
            String uniqueId = UUID.randomUUID().toString();
            File serFile = File.createTempFile(uniqueId, ".ser", new File("/tmp/"));
            httpSession.setAttribute("uniqueId", serFile);
        }

        if (httpSession.getAttribute("demofile") == null) {
            String arffFilePath = exampleFilesFolder + '/' + demoFileName;
            File arffFile = new File(arffFilePath);
            httpSession.setAttribute("demofile", arffFile);
        }

        ArrayList<AlgortihmsInformation> history = (ArrayList<AlgortihmsInformation>) httpSession.getAttribute("history");
        history.add(new AlgortihmsInformation(demoFileName, new SimpleDateFormat("HH:mm:ss")));
        serializationService.serialization(history, (File) httpSession.getAttribute("uniqueId"));

        // Read the given file and return instances
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
    public String getExplorePage(Model model, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        // get demo dataset names and classifier name
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        try {
            ArrayList<AlgortihmsInformation> deserializationObject = serializationService.deserialization((File) httpSession.getAttribute("uniqueId"));
            model.addAttribute("info", deserializationObject);
        } catch (NullPointerException e) {
            model.addAttribute("msg", "No History");

        }
        return "workbench";
    }


    @PostMapping(value = "/workbench/explore")
    public String postExplorePage(@RequestParam(name = "classifier") String classifierName,
                                  HttpRequest httpRequest,
                                  Model model, RedirectAttributes redirect, HttpSession httpSession) throws Exception {

        if (httpSession.getAttribute("algorithm") == null) {
            httpSession.setAttribute("algorithm", classifierName);
        }
        System.out.println(classifierName);
        classifierFactory.createClassifier(classifierName, httpRequest);

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
