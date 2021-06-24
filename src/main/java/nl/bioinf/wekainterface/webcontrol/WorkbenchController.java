package nl.bioinf.wekainterface.webcontrol;

import nl.bioinf.wekainterface.model.AlgorithmsInformation;
import nl.bioinf.wekainterface.model.InstanceReader;
import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.model.WekaClassifier;
import nl.bioinf.wekainterface.service.SerializationService;
import nl.bioinf.wekainterface.service.SerializationServiceUploadedFiles;
import nl.bioinf.wekainterface.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.core.Instances;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marijke Eggink, Jelle Becirspahic, Bart Engels
 */

@Controller
public class WorkbenchController {
    @Autowired
    private InstanceReader instanceReader;
    @Autowired
    private WekaClassifier wekaClassifier;
    @Autowired
    private SerializationService serializationService;
    @Autowired
    private SerializationServiceUploadedFiles serializationServiceUploadedFiles;
    @Autowired
    private LabelCounter labelCounter;
    @Autowired
    private SessionService sessionService;

    @GetMapping(value = "/workbench")
    public String getWorkbench(Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        List<String> filenames = instanceReader.getDataSetNames();
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
                                RedirectAttributes redirect, HttpSession httpSession) throws Exception {

        sessionService.createSessionObjects(httpSession, demoFileName);

        ArrayList<AlgorithmsInformation> history = sessionService.setHistory(httpSession, demoFileName, multipart);
        serializationService.serialization(history, (File) httpSession.getAttribute("uniqueIdHistory"));

        ArrayList<String> uploadedFiles = sessionService.setUploadedFile(httpSession, multipart);
        serializationServiceUploadedFiles.serialization(uploadedFiles, (File) httpSession.getAttribute("uniqueIdUpload"));

        Instances instances = sessionService.setInstances(httpSession, multipart, demoFileName);

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
}
