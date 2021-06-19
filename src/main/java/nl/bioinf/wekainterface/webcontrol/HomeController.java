package nl.bioinf.wekainterface.webcontrol;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Marijke Eggink, Jelle Becirspahic & Bart Engels
 * Controller responsible for serving static pages
  */

@Controller
public class HomeController {

    @GetMapping(value = "/home")
    public String getLandingPage(){
        return "static_pages/landingpage";
    }

    @GetMapping(value = "/about")
    public String getFileUploadPage(){
        return "static_pages/about";
    }

    @GetMapping(value = "/contact")
    public String getContactPage(){
        return "static_pages/contact";
    }
}