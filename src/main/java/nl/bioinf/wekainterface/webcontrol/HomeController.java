package nl.bioinf.wekainterface.webcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Marijke Eggink, Jelle Becirspahic & Bart Engels
 * Controller responsible for serving static pages
  */

@Controller
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping(value = "/home")
    public String getLandingPage(){
        logger.info("Serving landing page..");
        return "static_pages/landingpage";
    }

    @GetMapping(value = "/about")
    public String getFileUploadPage(){
        logger.info("Serving about page..");
        return "static_pages/about";
    }

    @GetMapping(value = "/contact")
    public String getContactPage(Model model){
        logger.info("Serving contact page..");
        model.addAttribute("names", new String[]{"marijke", "bart", "jelle", "michiel"});
        return "static_pages/contact";
    }
}