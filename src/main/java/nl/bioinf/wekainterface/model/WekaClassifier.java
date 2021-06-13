package nl.bioinf.wekainterface.model;

import org.springframework.stereotype.Component;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.neighboursearch.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Marijke Eggink 30419
 */

@Component
public class WekaClassifier {
    /**
     * TODO ZONDER PARAMETERS DEZE KAN ER LATER UIT ALS DE PARAMETERS ZIJN TOEGEVOEGD.
     * This method classifies instances with a 10-fold cross validation.
     * @param instances instances to be classified.
     * @param classifier name of the classifier.
     * @return String with results of 10-fold cross validation.
     * @throws Exception
     */
    public Evaluation test(Instances instances, String classifier) throws Exception {
        weka.classifiers.Classifier rule;
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
                throw new IllegalArgumentException();
        }
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(rule, instances, 10, new Random(1));
        return evaluation;
    }

    /**
     * This method classifies instances with a 10-fold cross validation, and with the given parameters.
     * @param instances instances to be classified.
     * @param classifier name of the classifier.
     * @param parameters String array with the needed parameters.
     * @return Evaluation object with results of 10-fold cross validation.
     * @throws Exception
     */
    public Evaluation classify(Instances instances, String classifier, String[] parameters) throws Exception {
        weka.classifiers.Classifier rule;
        switch (classifier){
            case "ZeroR":
                rule = zeroR(parameters);
                break;
            case "OneR":
                rule = oneR(parameters);
                break;
            case "NaiveBayes":
                rule = naiveBayes(parameters);
                break;
            case "J48":
                rule = j48(parameters);
                break;
            case "IBK":
                rule = iBk(parameters);
                break;
            default:
                throw new IllegalArgumentException();
        }
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(rule, instances, 10, new Random(1));
        return evaluation;
    }

    /**
     * This method creates an ZeroR rule and sets the parameters
     * @param parameters String array with the parameters
     * @return ZeroR rule
     */
    public ZeroR zeroR(String[] parameters) {
        ZeroR rule = new ZeroR();
        rule.setBatchSize(parameters[0]);
        rule.setDebug(Boolean.parseBoolean(parameters[1]));
        rule.setDoNotCheckCapabilities(Boolean.parseBoolean(parameters[2]));
        rule.setNumDecimalPlaces(Integer.parseInt(parameters[3]));
        return rule;
    }

    /**
     * This method creates an OneR rule and sets the parameters
     * @param parameters String array with the parameters
     * @return OneR rule
     */
    public OneR oneR(String[] parameters) {
        OneR rule = new OneR();
        rule.setBatchSize(parameters[0]);
        rule.setDebug(Boolean.parseBoolean(parameters[1]));
        rule.setDoNotCheckCapabilities(Boolean.parseBoolean(parameters[2]));
        rule.setMinBucketSize(Integer.parseInt(parameters[3]));
        rule.setNumDecimalPlaces(Integer.parseInt(parameters[4]));
        return rule;
    }

    /**
     * This method creates an Naive Bayes rule and sets the parameters
     * @param parameters String array with the parameters
     * @return NaiveBayes rule
     */
    public NaiveBayes naiveBayes(String[] parameters) {
        NaiveBayes rule = new NaiveBayes();
        rule.setBatchSize(parameters[0]);
        rule.setDebug(Boolean.parseBoolean(parameters[1]));
        rule.setDoNotCheckCapabilities(Boolean.parseBoolean(parameters[2]));
        rule.setNumDecimalPlaces(Integer.parseInt(parameters[3]));
        return rule;
    }

    /**
     * This method creates an J48 rule and sets the parameters
     * @param parameters String array with the parameters
     * @return J48 rule
     */
    public J48 j48(String[] parameters) {
        J48 rule = new J48();
        rule.setBatchSize(parameters[0]);
        rule.setConfidenceFactor(Float.parseFloat(parameters[1]));
        rule.setDebug(Boolean.parseBoolean(parameters[2]));
        rule.setDoNotCheckCapabilities(Boolean.parseBoolean(parameters[3]));
        rule.setMinNumObj(Integer.parseInt(parameters[4]));
        rule.setNumDecimalPlaces(Integer.parseInt(parameters[5]));
        rule.setNumFolds(Integer.parseInt(parameters[6]));
        rule.setUnpruned(!Boolean.parseBoolean(parameters[7]));
        return rule;
    }

    /**
     * This method creates an IBK rule and sets the parameters
     * @param parameters String array with the parameters
     * @return IBK rule
     */
    public IBk iBk(String[] parameters) {
        IBk rule = new IBk();
        rule.setKNN(Integer.parseInt(parameters[0]));
        rule.setBatchSize(parameters[1]);
        rule.setCrossValidate(Boolean.parseBoolean(parameters[2]));
        rule.setDebug(Boolean.parseBoolean(parameters[3]));
        rule.setDoNotCheckCapabilities(Boolean.parseBoolean(parameters[4]));
        NearestNeighbourSearch neighbourSearch = getSearchAlgorithm(parameters[5]);
        rule.setNearestNeighbourSearchAlgorithm(neighbourSearch);
        rule.setNumDecimalPlaces(Integer.parseInt(parameters[6]));
        return rule;
    }

    /**
     * This method gets the search nearest neighbour search algorithm for IBK.
     * @param algorithm name of the search algorithm
     * @return NearestNeighbourSearch algorithm
     */
    public NearestNeighbourSearch getSearchAlgorithm(String algorithm){
        NearestNeighbourSearch nearestNeighbourSearch;
        switch (algorithm){
            default:
                nearestNeighbourSearch = new BallTree();
                break;
            case "CoverTree":
                nearestNeighbourSearch = new CoverTree();
                break;
            case "FilteredNeighbourSearch":
                nearestNeighbourSearch = new FilteredNeighbourSearch();
                break;
            case "KDTree":
                nearestNeighbourSearch = new KDTree();
                break;
            case "LinearNNSearch":
                nearestNeighbourSearch = new LinearNNSearch();
                break;
        }
        return nearestNeighbourSearch;
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
