package nl.bioinf.wekainterface.webcontrol;

import nl.bioinf.wekainterface.model.DataReader;
import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.model.WekaClassifier;
import nl.bioinf.wekainterface.service.ClassificationService;
import nl.bioinf.wekainterface.service.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Marijke Eggink, Jelle Becirspahic
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
    private ClassificationService classificationService;
    @Autowired
    private FileService fileService;

    @GetMapping(value = "/workbench")
    public String getWorkbench(Model model){
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        return "workbench";
    }

    @PostMapping(value = "/workbench")
    public String postWorkbench(@RequestParam(name = "filename", required = false) String demoFileName,
                                Model model, RedirectAttributes redirect) throws Exception {
        String arffFilePath = exampleFilesFolder + '/' + demoFileName;

        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);

        labelCounter.readData(new File(arffFilePath));
        labelCounter.setGroups();
        labelCounter.countLabels();
        model.addAttribute("data", labelCounter.mapToJSON());
        model.addAttribute("attributes", labelCounter.getAttributeArray());
        model.addAttribute("classLabel", labelCounter.getClassLabel());
        return "workbench";
    }

//
//    @PostMapping(value = "/upload")
//    public String postFileUpload(@RequestParam(name = "filename") String fileName,
//                                 Model model, RedirectAttributes redirect) throws Exception {
//        String arffFilePath = exampleFilesFolder + '/' + fileName;
//
//        labelCounter.readData(new File(arffFilePath));
//        labelCounter.setGroups();
//        labelCounter.countLabels();
//        //redirect.addFlashAttribute("filename", fileName);
//        redirect.addFlashAttribute("data", labelCounter.mapToJSON());
//        redirect.addFlashAttribute("attributes", labelCounter.getAttributeArray());
//        redirect.addFlashAttribute("classLabel", labelCounter.getClassLabel());
//        return "redirect:/explorer";
//    }

    @GetMapping(value = "/explorer")
    public String getExplorerPage(Model model){
        List<String> filenames = dataReader.getDataSetNames();
        List<String> classifierNames = wekaClassifier.getClassifierNames();
        model.addAttribute("filenames", filenames);
        model.addAttribute("classifierNames", classifierNames);
        //String filename = (String)model.getAttribute("filename");
        //System.out.println(filename);
        //model.addAttribute("filename", filename);
        return "explorer";
    }

    @PostMapping(value = "/explorer")
    public String postExplorerPage(@RequestParam(name = "inputFile", required = false) MultipartFile multipart,
                                   @RequestParam(name = "filename", required = false) String demoFileName,
                                   @RequestParam(name = "classifier") String classifierName,
                                   @RequestParam(name = "delimiter") String delimiter,
                                   Model model, RedirectAttributes redirect) throws Exception {
        Instances instances;

        if (!multipart.isEmpty()){
            instances = fileService.getInstancesFromMultipart(multipart, delimiter);
        } else {
            instances = fileService.getInstancesFromDemoFile(demoFileName);
        }

        Evaluation evaluation = wekaClassifier.test(instances, classifierName);

        redirect.addFlashAttribute("evaluation", evaluation);
        return "redirect:/explorer/results";
    }

    @GetMapping(value = "/explorer/results")
    public String getResultsPage(Model model){
        return "results";
    }

    @GetMapping(value = "/test")
    public String plotWeatherData(Model model) throws IOException {
        String file = exampleFilesFolder + '/' + "weather.nominal.arff";
        labelCounter.readData(new File(file));
        labelCounter.setGroups();
        labelCounter.countLabels();
        model.addAttribute("data", labelCounter.mapToJSON());
        model.addAttribute("attributes", labelCounter.getAttributeArray());
        model.addAttribute("classLabel", labelCounter.getClassLabel());
        return "dataExplorer";
    }
}