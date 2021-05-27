package nl.bioinf.wekainterface.webcontrol;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author Bart Engels
 */

@Controller

public class InformationController {

    @Value("#{'${youtube.link}'.split(',')}")
    private List<String> youtubeLink;

    @RequestMapping(value="information")
    public String getInfoPage(Model model){
        String[] AlgoList = {"ZeroR", "OneR","NaiveBayes","IBK","J48"};
        model.addAttribute("algonames", AlgoList);
        model.addAttribute("youtubelink", youtubeLink);
        return "infAlgorithms";
    }


}
