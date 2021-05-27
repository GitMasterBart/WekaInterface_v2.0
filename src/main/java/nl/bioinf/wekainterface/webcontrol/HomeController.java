package nl.bioinf.wekainterface.webcontrol;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Marijke Eggink, Jelle Becirspahic & Bart Engels
  */

@Controller
public class HomeController {

    @GetMapping(value = "/home")
    public String getLandingPage(){
        return "landingpage";
    }

    @GetMapping(value = "/about")
    public String getFileUploadPage(){
        return "about";
    }

    @GetMapping(value = "/contact")
    public String getContactPage(){
        return "contact";
    }
}