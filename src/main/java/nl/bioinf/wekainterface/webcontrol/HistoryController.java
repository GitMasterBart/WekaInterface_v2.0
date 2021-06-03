package nl.bioinf.wekainterface.webcontrol;


import nl.bioinf.wekainterface.model.AlgortihmsInformation;
import nl.bioinf.wekainterface.model.DataReader;
import nl.bioinf.wekainterface.model.WekaClassifier;
import nl.bioinf.wekainterface.service.ClassificationService;
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

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Bart Engels
 */

@Controller
public class HistoryController {

    @Autowired
    private SerializationService serializationService;

    @GetMapping(value = "/history")
    public String getHistoryPage(Model model, HttpSession httpSession){
        model.addAttribute("info" , serializationService.deserialization((File) httpSession.getAttribute("uniqueId")));
        return "hisotrydummypage";
    }

    @PostMapping(value = "/history")

    public String postHistoryPage(Model model, RedirectAttributes redirect,
                                   HttpSession httpSession) throws Exception {
        ArrayList<AlgortihmsInformation> evaluation =  serializationService.deserialization((File) httpSession.getAttribute("uniqueId"));
        for (int i = 0; i < evaluation.size(); i++) {
            System.out.println(evaluation.get(i));
            redirect.addFlashAttribute("evaluation", evaluation.get(i).getEvaluationAlgorithm());
        }
        return "redirect:/history/results";
    }
    @GetMapping(value = "/history/results")
    public String getHistoryResultsPage(Model model) throws Exception {
        return "results";
    }
    }
