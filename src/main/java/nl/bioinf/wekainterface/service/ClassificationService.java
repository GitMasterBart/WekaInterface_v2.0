package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.DataReader;
import nl.bioinf.wekainterface.model.WekaClassifier;
import org.springframework.stereotype.Service;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

import java.io.File;

/**
 * Deze class staat er nog in, maar wordt niet meer gebruikt.
 */
@Service
public class ClassificationService {
    public Evaluation classify(File arffFile, String classifierName){
        try {
            DataReader reader = new DataReader();
            Instances instances = reader.readArff(arffFile);
            WekaClassifier wekaClassifier = new WekaClassifier();
            return wekaClassifier.test(instances, classifierName);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("something went wrong " + e.getMessage());
        }
    }
}
