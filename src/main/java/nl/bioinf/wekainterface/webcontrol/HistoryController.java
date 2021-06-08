package nl.bioinf.wekainterface.webcontrol;


import nl.bioinf.wekainterface.model.AlgorithmsInformation;
import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.service.ClassificationService;
import nl.bioinf.wekainterface.service.FileService;
import nl.bioinf.wekainterface.service.SerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;


/**
 * @author Bart Engels
 */

@Controller
public class HistoryController {

    @Value("${example.data.path}")
    private String exampleFilesFolder;

    @Autowired
    private SerializationService serializationService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private LabelCounter labelCounter;

    @Autowired
    private FileService fileService;

    @GetMapping(value = "/history/{dataSet}/{algorithms}")
    public String postHistoryPage(@PathVariable("dataSet") String dataSet,
                                  @PathVariable("algorithms") String algorithms,
                                  Model model, RedirectAttributes redirect, HttpSession httpSession) throws Exception {
        // DEZE WORDT NIET GEBRUIKT NOG, EN MOET NOG AANGEPAST WORDEN AAN DE NIEUWE MANIER VAN DATA INLEZEN
        File arffFile;
        String arffFilePath = exampleFilesFolder + '/' + dataSet;
        arffFile = new File(arffFilePath);
        Evaluation evaluation = classificationService.classify(arffFile, algorithms);
        redirect.addFlashAttribute("evaluation", evaluation);

        return "redirect:/workbench";
    }

    @GetMapping(value = "/history/{dataSet}")
    public String plotHisotryPlots(@PathVariable("dataSet") String dataset, Model model, RedirectAttributes redirect) throws Exception{
        Instances instances = fileService.getInstancesFromDemoFile(dataset);
        String arffFilePath = exampleFilesFolder + '/' + dataset;
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