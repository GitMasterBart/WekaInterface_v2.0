package nl.bioinf.wekainterface.webcontrol;


import nl.bioinf.wekainterface.model.AlgortihmsInformation;
import nl.bioinf.wekainterface.service.SerializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author Bart Engels
 */

@Controller
public class HistoryController {

    @Autowired
    private SerializationService serializationService;

    @GetMapping(value = "/history")
    public String getHistoryPage(Model model){
        String[] strings = {"9", "9"};
        serializationService.serialization(new AlgortihmsInformation("ONER", strings ));

        model.addAttribute("info" , serializationService.deserialization().toString());

        return "hisotrydummypage";
    }
    }
