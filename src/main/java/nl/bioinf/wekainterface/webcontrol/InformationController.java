package nl.bioinf.wekainterface.webcontrol;

import nl.bioinf.wekainterface.model.WekaClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author Bart Engels 382612
 */

@Controller

public class InformationController {
    @Value("#{'${youtube.link}'.split(',')}")
    private List<String> youtubeLink;
    @Autowired
    private WekaClassifier wekaClassifier;
    private final Logger logger = LoggerFactory.getLogger(InformationController.class);

    @RequestMapping(value="information")
    public String getInfoPage(Model model){
        logger.info("Serving information page..");
        List<String> AlgoList = wekaClassifier.getClassifierNames();
        model.addAttribute("algonames", AlgoList);
        model.addAttribute("youtubelink", youtubeLink);
        return "infAlgorithms";
    }


}
