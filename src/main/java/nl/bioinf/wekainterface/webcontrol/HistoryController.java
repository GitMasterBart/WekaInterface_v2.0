package nl.bioinf.wekainterface.webcontrol;


import nl.bioinf.wekainterface.model.AlgortihmsInformation;
import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.service.ClassificationService;
import nl.bioinf.wekainterface.service.SerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.evaluation.Evaluation;

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

    @GetMapping(value = "/history")
    public String getHistoryPage(Model model, HttpSession httpSession){
        try {
            ArrayList<AlgortihmsInformation> deserializationObject = serializationService.deserialization((File) httpSession.getAttribute("uniqueId"));
            model.addAttribute("info", deserializationObject);
        }  catch (NullPointerException e){
            model.addAttribute("msg", "No History");
        }
        return "hisotrydummypage";
    }



    @GetMapping(value = "/history/{dataSet}/{algorithms}")

    public String postHistoryPage(@PathVariable("dataSet") String dataSet, @PathVariable("algorithms") String algorithms, Model model, RedirectAttributes redirect,
                                  HttpSession httpSession) throws Exception {
        File arffFile;
        String arffFilePath = exampleFilesFolder + '/' + dataSet;
        arffFile = new File(arffFilePath);
        Evaluation evaluation = classificationService.classify(arffFile, algorithms);
        redirect.addFlashAttribute("evaluation", evaluation);

        return "redirect:/history/results";
    }

    @GetMapping(value = "/history/results")
    public String getHistoryResultsPage(Model model) throws Exception {
        return "results";
    }

    @GetMapping(value = "/history/{dataSet}")
    public String plotHisotryPlots(@PathVariable("dataSet") String dataset, Model model) throws Exception{
        String file = exampleFilesFolder + '/' + dataset;
        System.out.println(file);
        labelCounter.readData(new File(file));
        labelCounter.setGroups();
        labelCounter.countLabels();
        model.addAttribute("data", labelCounter.mapToJSON());
        model.addAttribute("attributes", labelCounter.getAttributeArray());
        model.addAttribute("classLabel", labelCounter.getClassLabel());
        return "dataExplorer";

    }




    }