package nl.bioinf.wekainterface.model;

import nl.bioinf.wekainterface.service.FileService;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ConfusionMatrix;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Marijke Eggink 370419
 */

@Component
public class WekaClassifier {
    /**
     * TODO: Zonder parameters, deze moet er nog uit gehaald worden wanneer hij nergens meer wordt gebruikt.
     * This method classifies instances with a 10-fold cross validation.
     * @param instances instances to be classified.
     * @param classifier name of the classifier.
     * @return String with results of 10-fold cross validation.
     * @throws Exception
     */
    public Evaluation test(Instances instances, String classifier) throws Exception {
        Classifier rule;
        switch (classifier){
            case "ZeroR":
                rule = new ZeroR();
                break;
            case "OneR":
                rule = new OneR();
                break;
            case "NaiveBayes":
                rule = new NaiveBayes();
                break;
            case "J48":
                rule = new J48();
                break;
            case "IBK":
                rule = new IBk();
                break;
            default:
                throw new IllegalArgumentException("Error: This classifier name is not supported");
        }
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(rule, instances, 10, new Random(1));
        return evaluation;
    }

    /**
     * This method classifies instances with a 10-fold cross validation, and with the given parameters.
     * @param instances instances to be classified.
     * @param parameterMap Map with the needed parameters.
     * @return Evaluation object with results of 10-fold cross validation.
     * @throws Exception
     */
    public Evaluation classify(Instances instances, Map<String, String> parameterMap) throws Exception {
        Classifier rule;
        switch (parameterMap.get("classifier")){
            case "ZeroR":
                rule = zeroR(parameterMap);
                break;
            case "OneR":
                rule = oneR(parameterMap);
                break;
            case "NaiveBayes":
                rule = naiveBayes(parameterMap);
                break;
            case "J48":
                rule = j48(parameterMap);
                break;
            case "IBK":
                rule = iBk(parameterMap);
                break;
            default:
                throw new IllegalArgumentException("Error: This classifier name is not supported");
        }
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(rule, instances, 10, new Random(1));
        return evaluation;
    }

    /**
     * This method creates an ZeroR rule and sets the parameters
     * @param parameterMap Map with the parameters
     * @return ZeroR rule
     */
    public ZeroR zeroR(Map<String,String> parameterMap) {
        ZeroR rule = new ZeroR();
        Parameters parameters = new Parameters(parameterMap);
        rule.setBatchSize(parameters.getBatchSize());
        rule.setDebug(parameters.isDebug());
        rule.setDoNotCheckCapabilities(parameters.isCapabilities());
        rule.setNumDecimalPlaces(parameters.getDecimal());
        return rule;
    }

    /**
     * This method creates an OneR rule and sets the parameters
     * @param parameterMap Map with the parameters
     * @return OneR rule
     */
    public OneR oneR(Map<String, String> parameterMap) {
        OneR rule = new OneR();
        Parameters parameters = new Parameters(parameterMap);
        rule.setBatchSize(parameters.getBatchSize());
        rule.setDebug(parameters.isDebug());
        rule.setDoNotCheckCapabilities(parameters.isCapabilities());
        rule.setMinBucketSize(parameters.getMinBucketSize());
        rule.setNumDecimalPlaces(parameters.getDecimal());
        return rule;
    }

    /**
     * This method creates an Naive Bayes rule and sets the parameters
     * @param parameterMap Map with the parameters
     * @return NaiveBayes rule
     */
    public NaiveBayes naiveBayes(Map<String, String> parameterMap) {
        NaiveBayes rule = new NaiveBayes();
        Parameters parameters = new Parameters(parameterMap);
        rule.setBatchSize(parameters.getBatchSize());
        rule.setDebug(parameters.isDebug());
        rule.setDoNotCheckCapabilities(parameters.isCapabilities());
        rule.setNumDecimalPlaces(parameters.getDecimal());
        return rule;
    }

    /**
     * This method creates an J48 rule and sets the parameters
     * @param parameterMap Map with the parameters
     * @return J48 rule
     */
    public J48 j48(Map<String, String> parameterMap) {
        J48 rule = new J48();
        Parameters parameters = new Parameters(parameterMap);
        rule.setBatchSize(parameters.getBatchSize());
        rule.setConfidenceFactor(parameters.getConfidenceFactor());
        rule.setDebug(parameters.isDebug());
        rule.setDoNotCheckCapabilities(parameters.isCapabilities());
        rule.setMinNumObj(parameters.getNumObj());
        rule.setNumDecimalPlaces(parameters.getDecimal());
        rule.setNumFolds(parameters.getNumFolds());
        rule.setUnpruned(parameters.getPruned());
        return rule;
    }

    /**
     * This method creates an IBK rule and sets the parameters
     * @param parameterMap Map with the parameters
     * @return IBK rule
     */
    public IBk iBk(Map<String, String> parameterMap) {
        IBk rule = new IBk();
        Parameters parameters = new Parameters(parameterMap);
        rule.setKNN(parameters.getKNN());
        rule.setBatchSize(parameters.getBatchSize());
        rule.setCrossValidate(parameters.getCrossValidate());
        rule.setDebug(parameters.isDebug());
        rule.setDoNotCheckCapabilities(parameters.isCapabilities());
        rule.setNearestNeighbourSearchAlgorithm(parameters.getSearchAlgorithm());
        rule.setNumDecimalPlaces(parameters.getDecimal());
        return rule;
    }

    /**
     * This method creates a list of al the algorithm names that are available.
     * @return List of strings
     */
    public List<String> getClassifierNames(){
        String[] names = {"ZeroR", "OneR", "NaiveBayes", "J48", "IBK"};
        return Arrays.asList(names);
    }
}
