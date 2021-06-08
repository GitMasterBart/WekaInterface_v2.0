package nl.bioinf.wekainterface.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bioinf.wekainterface.errorhandling.InvalidDataSetProcessException;
import org.springframework.stereotype.Component;

import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.Stats;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;

/**
@author jelle 387615
 Given Instances, counts the occurrence of each label for each attribute and seperates it by classlabel.
 */
@Component
public class LabelCounter {

    private Instances instances;
    private List<String> attributeArray = new ArrayList<>();
    private Map<String, AttributeMap> groups = new HashMap<>();
    private Map<String, Double> twoAttributeGroups = new HashMap<>();

    private boolean onlyTwoAttributes = false;

    /**
     * Reads arff file and stores Instances in the class
     * @param instances instances
     */
    public void setInstances(Instances instances){
        this.instances = instances;
    }

    /**
     * Creates a Map<String, Map<String, Map<String, Integer>>> structure. First map holds each class label as key with
     * a second Map as value. This Map holds each attribute as key and a third Map as its value. This third Map holds
     * the labels for each attribute as its key and the occurrence of those labels as its value. The occurrence is set
     * at 0.
     */
    public void setGroups(){



        if (this.instances.numAttributes() == 1){
            throw new InvalidDataSetProcessException("Dataset only contains 1 Attribute");
        }


        if (this.instances.numAttributes() > 2){
            System.out.println("MORE THAN 2 ATTRIBUTES");
            if (this.instances.classAttribute().isDate() || this.instances.classAttribute().isNumeric()){
                System.out.println("Class attribute is Date or Numeric");
                AttributeStats stats = this.instances.attributeStats(this.instances.classIndex());
                int numberOfValues = stats.totalCount;
                for (int instanceIndex = 0; instanceIndex < numberOfValues; instanceIndex++){
                    AttributeMap attributes = setAttributes();
                    String classLabel = Double.toString(this.instances.instance(instanceIndex).value(this.instances.classIndex()));
                    if (!groups.containsKey(classLabel)){
                        this.groups.put(classLabel, attributes);
                    }
                }
            }
            else {
                System.out.println("Class Attribute is Nominal");
                int numberOfValues = this.instances.classAttribute().numValues();
                for (int classLabelIndex = 0; classLabelIndex < numberOfValues; classLabelIndex++) {
                    // Setting the class label as the key for the first Map, in the case of weather.nominal = {yes,no}
                    String classLabel = this.instances.classAttribute().value(classLabelIndex);
                    // creating the 2nd Map with attribute names as keys and labels as value's
                    AttributeMap attributes = setAttributes();
                    this.groups.put(classLabel, attributes);
                }
            }
        }else {
            onlyTwoAttributes = true;
            AttributeStats stats = this.instances.attributeStats(this.instances.classIndex());
            if (this.instances.classAttribute().isDate() && stats.totalCount == stats.uniqueCount){
                for (int classLabelIndex=0;classLabelIndex< stats.totalCount;classLabelIndex++) {
                    // Setting the class label as the key for the first Map, in the case of weather.nominal = {yes,no}
                    double classValue = this.instances.instance(classLabelIndex).value(this.instances.classIndex());
                    String dateFormat = this.instances.classAttribute().getDateFormat();
                    String classLabel = parseDate(classValue, dateFormat);
                    // creating the 2nd Map with attribute names as keys and labels as value's
                    try{
                        this.twoAttributeGroups.put(classLabel,
                                Double.parseDouble(this.instances.instance(classLabelIndex).toString().split(",")[this.instances.classIndex()-1]));
                    }catch (NumberFormatException e){
                        System.out.println("ERROR:\tValue for Attribute " + this.instances.attribute(this.instances.classIndex()-1) + "is not numeric.");
                    }
                }
            }
        }
    }

    /**
     * For every attribute in the dataset creates an entry in a Map with the attribute name as the key and a Map as the
     * value for this key. This Map holds each attribute label as its key and the occurrence of this label as its value.
     * @return Map<Attribute name, Map<Attribute label, Label occurrence>>
     */
    private AttributeMap setAttributes(){
        AttributeMap attributes = new AttributeMap();
        int numAttributes = this.instances.numAttributes();
        for (int attributeIndex = 0; attributeIndex < numAttributes; attributeIndex++){

            String attributeName = this.instances.attribute(attributeIndex).name();

            // attributeName array to later count the occurrence of each label for each attributeName
            if (!attributeArray.contains(attributeName)){
                attributeArray.add(attributeName);
            }

            boolean isNominal = this.instances.attribute(attributeIndex).isNominal();
            boolean isNumeric = this.instances.attribute(attributeIndex).isNumeric();
            // Setting labels when the attributeName isn't the class attributeName I.E. the attributeName that is being classified
            if (attributeIndex != this.instances.classIndex()){
                if (isNominal){
                    setLabelsNominal(attributeIndex, attributeName, attributes);
                }
                else if (isNumeric){
                    setLabelsNumeric(attributeIndex, attributeName, attributes);
                }
            }
        }
        return attributes;
    }

    /**
     * For the given attribute/attribute index, add each label to the Map and set its value to 0.
     * @param attributeIndex index of the attribute
     * @param attributeName attribute name
     * @param attributeMap Map<Attribute name, Map<Attribute label, Label occurrence>> where attribute labels need to be added
     */
    private void setLabelsNominal(int attributeIndex, String attributeName, AttributeMap attributeMap){
        LabelMap labelMap = new LabelMap();
        int numValues = this.instances.attribute(attributeIndex).numValues();
        for (int valueIndex = 0;valueIndex < numValues; valueIndex++){
            String label = this.instances.attribute(attributeIndex).value(valueIndex);
            labelMap.addLabel(label);
        }
        attributeMap.addAttribute(attributeName, labelMap);
    }

    /**
     * For the given attribute/attribute index, make intervals that represent a group of numeric values.
     * This is represented like: 'x - y' meaning the label represents values between x and y.
     * The amount of intervals is determined by how many times the Standard Deviation fits in the difference between
     * the minimum and maximum value of the given attribute.
     * @param attributeIndex index of the attribute
     * @param attribute attribute name
     * @param attributeMap Map<Attribute name, Map< [X - Y] Interval, Label occurrence>>
     */
    private void setLabelsNumeric(int attributeIndex, String attribute, AttributeMap attributeMap){
        LabelMap labelMap = new LabelMap();
        Stats stats = this.instances.attributeStats(attributeIndex).numericStats;
        // Number of groups is based the amount of times the standard deviation fits into the interval between the
        // minimum and maximum value of the attribute
        double numGroups = Math.round((stats.max - stats.min) / stats.stdDev);

        // Number of decimals used in the dataset
        int numDecimals = numDecimals(stats.min);
        double groupInterval = roundTo((stats.max - stats.min) / numGroups, numDecimals);
        double intervalStart = stats.min;

        for (int groupIndex = 0; groupIndex < numGroups; groupIndex++){

            String labelGroup;
            double intervalEnd = roundTo((intervalStart + groupInterval), numDecimals);
            if (groupIndex == numGroups-1){
                labelGroup = intervalStart + "-" + roundTo((stats.max + roundTo((groupInterval/10), numDecimals)), numDecimals);
            }else {
                labelGroup = intervalStart + "-" + intervalEnd;
            }
            labelMap.addLabel(labelGroup);
            intervalStart = roundTo(groupInterval + intervalStart, numDecimals);
        }
        attributeMap.addAttribute(attribute, labelMap);
    }

    /**
     * For each label in the instance, increase it's occurrence count by 1, depending on which class label it has.
     */
    public void countLabels(){
        if (!onlyTwoAttributes){
            for (int instanceIndex = 0; instanceIndex < instances.numInstances(); instanceIndex++){

                Instance instance = instances.instance(instanceIndex);
                String[] values = instance.toString().split(",");
                for (int valueIndex = 0; valueIndex < instance.numValues(); valueIndex++){
                    AttributeMap attributeMap = new AttributeMap();
                    if (instances.classAttribute().isNumeric()){
                        attributeMap = groups.get(Double.toString(Double.parseDouble(values[instance.classIndex()])));
                    }else if (instances.classAttribute().isNominal()){
                        attributeMap = groups.get(values[instance.classIndex()]);
                    }

                    if(valueIndex != instance.classIndex()){
                        String attribute = attributeArray.get(valueIndex);
                        LabelMap labelMap = attributeMap.getLabelMap(attribute);

                        try{//If value is numeric
                            double value = Double.parseDouble(values[valueIndex]);
                            countNumeric(value, labelMap);
                        }catch (NumberFormatException e){//Not numeric
                            countNominal(values[valueIndex], labelMap);
                        }
                    }
                }
            }
        }
    }

    /**
     * Given a value determine in which [x-y] interval it sits and increment the occurrence of that [x-y] label
     * @param value numeric value
     * @param labelMap Map<[x-y], [Occurrence of value between x and y]>
     */
    private void countNumeric(double value, LabelMap labelMap){
        for (Map.Entry<String, Integer> entry: labelMap.getLabelMap().entrySet()){
            String[] interval = entry.getKey().split("-");// Iterate over labels in labelMap
            boolean betweenInterval = value >= Double.parseDouble(interval[0]) && value < Double.parseDouble(interval[1]);

            if (betweenInterval){
                labelMap.incrementLabel(entry.getKey());
                break;
            }
        }
    }

    /**
     * Given a label, increase the occurrence of that label in the labelMap
     * @param label String
     * @param labelMap labelMap<label, occurrence of label>
     */
    private void countNominal(String label, LabelMap labelMap){
        labelMap.incrementLabel(label);
    }

    /**
     * Given a value round the value to 'numDecimals' decimals
     * @param value double
     * @param numDecimals int number of decimals the value should be rounded to
     * @return double, rounded value
     */
    private double roundTo(double value, int numDecimals){
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols();
        dfSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#." + "#".repeat(Math.max(0, numDecimals)), dfSymbols);
        String decimals = Double.toString(value).split("\\.")[1];

        if(numDecimals < decimals.length()){

            int lastDigit = Character.getNumericValue(decimals.toCharArray()[numDecimals]);
            if (lastDigit >= 5){
                df.setRoundingMode(RoundingMode.UP);
            }else {
                df.setRoundingMode(RoundingMode.DOWN);
            }
            return Double.parseDouble(df.format(value));
        }
        // If the value is already rounded to the right amount of decimals return the given value
        return value;
    }

    /**
     * given a value, determine the amount of decimals
     * @param d double
     * @return int, the amount of decimals
     */
    private int numDecimals(double d){
        String text = Double.toString(Math.abs(d));
        int integerPlaces = text.indexOf('.');
        return text.length() - integerPlaces - 1;
    }

    private String parseDate(double date, String dateformat){
        long lDate = (long) date;
        return new SimpleDateFormat(dateformat).format(lDate);
    }

    public List<String> getAttributeArray() {
        return attributeArray;
    }

    public String getClassLabel(){
        return instances.classAttribute().toString().split(" ")[1];
    }

    public boolean isOnlyTwoAttributes() {
        return onlyTwoAttributes;
    }

    /**
     * Converts the class label Map into a JSON format to be used in JavaScript
     * @return Map<Class label, Map<Attribute name, Map<Attribute label, Label occurrence>>> in JSON format
     * @throws JsonProcessingException ...
     */
    public String mapToJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        if (onlyTwoAttributes){
            return objectMapper.writeValueAsString(twoAttributeGroups);
        }else {
            Map<String, Map<String, Map<String, Integer>>> countMap = new HashMap<>();
            groups.forEach((classLabel, attributeMap) ->
                    countMap.put(classLabel, attributeMap.getJsonMap()));
            return objectMapper.writeValueAsString(countMap);
        }

    }

    /**
     * Resets the class variables. Use after setting the mapToJSON output to the webcontext to clear the class variables
     * for the next dataset.
     */
    public void resetLabelCounter(){
        instances = null;
        attributeArray = new ArrayList<>();
        groups = new HashMap<>();
        twoAttributeGroups = new HashMap<>();
        onlyTwoAttributes = false;
    }

    /**
     * Main function for testing class
     * @param args no args
     * @throws IOException if file doesn't exist
     */
    public static void main(String[] args) throws IOException{

        String file = "C:\\Program Files\\Weka-3-8-4\\data\\credit-g.arff";
        DataReader dataReader = new DataReader();
        LabelCounter labelCounter = new LabelCounter();
        labelCounter.setInstances(dataReader.readArff(new File(file)));
        labelCounter.setGroups();
        labelCounter.countLabels();

        System.out.println(labelCounter.mapToJSON());
        labelCounter.resetLabelCounter();
    }
}