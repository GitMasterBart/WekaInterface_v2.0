package nl.bioinf.wekainterface.webcontrol;

import nl.bioinf.wekainterface.model.WekaClassifier;
import org.springframework.stereotype.Component;

import java.net.http.HttpRequest;

@Component
public class ClassifierFactory {
    public static WekaClassifier createClassifier(String classifierName, HttpRequest httpRequest){
        switch (classifierName){
            case "ZeroR": return createZeroRClassifier(httpRequest);
        }
        return null;
    }

    private static WekaClassifier createZeroRClassifier(HttpRequest httpRequest) {
        System.out.println(httpRequest);
        return null;
    }
}
