package nl.bioinf.wekainterface.webcontrol;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.logging.Logger;

@Controller
public class LoggerController {
    Logger logger = LoggerFactory.getLogger(LoggingCon.class);


}
