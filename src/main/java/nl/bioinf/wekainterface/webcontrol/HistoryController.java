package nl.bioinf.wekainterface.webcontrol;


import nl.bioinf.wekainterface.model.LabelCounter;
import nl.bioinf.wekainterface.service.FileFindService;
import nl.bioinf.wekainterface.service.FileService;
import nl.bioinf.wekainterface.service.SerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.core.Instances;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * @author Bart Engels 382612
 */

@Controller
public class HistoryController {

    @Value("${example.data.path}")
    private String exampleFilesFolder;

    @Value("${temp.data.path}")
    private String tempUploadedFilesFolder;

    @Autowired
    private SerializationService serializationService;

    @Autowired
    private LabelCounter labelCounter;

    @Autowired
    private FileService fileService;


    @GetMapping(value = "/history/{dataSet}")
    public String plotHisotryPlots(@PathVariable("dataSet") String dataset, Model model, RedirectAttributes redirect , HttpSession httpSession) throws Exception{

       Instances instances;
        try {
             instances = fileService.getInstancesFromDemoFile(dataset);
        }catch (FileNotFoundException fileNotFoundException) {
            FileFindService fileFindService = new FileFindService();
            String uploadedFile = fileFindService.findFile(dataset,new File(tempUploadedFilesFolder));
            instances = fileService.getInstancesFromUloadedDemoFile(uploadedFile);

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
}