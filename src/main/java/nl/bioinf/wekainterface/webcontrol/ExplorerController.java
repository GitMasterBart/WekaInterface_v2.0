package nl.bioinf.wekainterface.webcontrol;

import nl.bioinf.wekainterface.model.*;
import nl.bioinf.wekainterface.service.SerializationService;
import nl.bioinf.wekainterface.service.SerializationServiceUploadedFiles;
import nl.bioinf.wekainterface.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Marijke Eggink, Jelle Becirspahic, Bart Engels
 */

@Controller
public class ExplorerController {
    @Autowired
    private InstanceReader instanceReader;
    @Autowired
    private LabelCounter labelCounter;
    @Autowired
    private WekaClassifier wekaClassifier;
    @Autowired
    private SerializationService serializationService;
    @Autowired
    private SerializationServiceUploadedFiles serializationServiceUploadedFiles;
    @Autowired
    private SessionService sessionService;

    @GetMapping(value = "/workbench/explore")
    public String getExplorePage(Model model, HttpSession httpSession) {
        List<String> filenames = instanceReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        try {
            ArrayList<AlgorithmsInformation> deserializationObjectHistory =
                    serializationService.deserialization((File) httpSession.getAttribute("uniqueIdHistory"));
            ArrayList<String> deserializationObjectUploadedFile =
                    serializationServiceUploadedFiles.deserialization((File) httpSession.getAttribute("uniqueIdUpload"));
            model.addAttribute("info", deserializationObjectHistory);
            model.addAttribute("uploadedFile", deserializationObjectUploadedFile);
        } catch (NullPointerException e) {
            model.addAttribute("msg", "No History");
            model.addAttribute("uploadedFile", "No uploaded files");

        }
        model.addAttribute("uploadedFile", httpSession.getAttribute("UploadedFiles"));
        return "workbench";
    }

    @PostMapping(value = "/workbench/explore")
    public String postExplorePage(@RequestParam Map<String, String> parameters, Model model,
                                  RedirectAttributes redirect, HttpSession httpSession) throws Exception {

        // get classifier name from parameters, and add it as a key value
        List<String> keys = new ArrayList<>(parameters.keySet());
        String classifierName = keys.get(0).split("-")[1];
        parameters.put("classifier", classifierName);

        sessionService.setClassifierName(httpSession, classifierName);

        Instances instances = (Instances)httpSession.getAttribute("instances");

        labelCounter.setInstances(instances);
        labelCounter.setGroups();
        labelCounter.countLabels();

        Evaluation evaluation = wekaClassifier.classify(instances, parameters);

        redirect.addFlashAttribute("data", labelCounter.mapToJSON());
        redirect.addFlashAttribute("attributes", labelCounter.getAttributeArray());
        redirect.addFlashAttribute("classLabel", labelCounter.getClassLabel());
        labelCounter.resetLabelCounter();
        redirect.addFlashAttribute("evaluation", evaluation);
        return "redirect:/workbench";
    }
}
