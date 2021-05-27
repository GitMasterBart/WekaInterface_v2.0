package nl.bioinf.wekainterface.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.Stats;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
@author jelle 387615
 Given a File, counts the occurrence of each label for each attribute and seperates it by classlabel
 */
@Component
public class LabelCounter {

    private Instances data;
    private List<String> attributeArray = new ArrayList<>();
    private Map<String, AttributeMap> groups = new HashMap<>();

    /**
     * Reads arff file and stores Instances in the class
     * @param file arff file
     * @throws IOException if file can't be found
     */
    public void readData(File file) throws IOException {
        DataReader reader = new DataReader();
        this.data = reader.readArff(file);
    }

    /**
     * Creates a Map<String, Map<String, Map<String, Integer>>> structure. First map holds each class label as key with
     * a second Map as value. This Map holds each attribute as key and a third Map as its value. This third Map holds
     * the labels for each attribute as its key and the occurrence of those labels as its value. The occurrence is set
     * at 0.
     */
    public void setGroups(){
        for (int classLabelIndex=0;classLabelIndex<this.data.classAttribute().numValues();classLabelIndex++) {
            // Setting the class label as the key for the first Map, in the case of weather.nominal = {yes,no}
            String classLabel = this.data.classAttribute().value(classLabelIndex);
            // creating the 2nd Map with attribute names as keys and labels as value's
            AttributeMap attributes = setAttributes();

            this.groups.put(classLabel, attributes);
        }
    }

    /**
     * For every attribute in the dataset creates an entry in a Map with the attribute name as the key and a Map as the
     * value for this key. This Map holds each attribute label as its key and the occurrence of this label as its value.
     * @return Map<Attribute name, Map<Attribute label, Label occurrence>>
     */
    private AttributeMap setAttributes(){
        AttributeMap attributes = new AttributeMap();
        int numAttributes = this.data.numAttributes();
        for (int attributeIndex = 0; attributeIndex < numAttributes; attributeIndex++){

            String attributeName = this.data.attribute(attributeIndex).name();

            // attributeName array to later count the occurrence of each label for each attributeName
            if (!attributeArray.contains(attributeName)){
                attributeArray.add(attributeName);
            }

            boolean isNominal = this.data.attribute(attributeIndex).isNominal();
            boolean isNumeric = this.data.attribute(attributeIndex).isNumeric();
            // Setting labels when the attributeName isn't the class attributeName I.E. the attributeName that is being classified
            if (attributeIndex != this.data.classIndex()){
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
        int numValues = this.data.attribute(attributeIndex).numValues();
        for (int valueIndex = 0;valueIndex < numValues; valueIndex++){
            String label = this.data.attribute(attributeIndex).value(valueIndex);
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
        Stats stats = this.data.attributeStats(attributeIndex).numericStats;
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
        for (int instanceIndex = 0; instanceIndex < data.numInstances(); instanceIndex++){

            Instance instance = data.instance(instanceIndex);
            String[] values = instance.toString().split(",");

            for (int valueIndex = 0; valueIndex < instance.numValues(); valueIndex++){
                AttributeMap attributeMap = groups.get(values[instance.classIndex()]);
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

    public List<String> getAttributeArray() {
        return attributeArray;
    }

    public String getClassLabel(){
        return data.classAttribute().toString().split(" ")[1];
    }

    /**
     * Converts the class label Map into a JSON format to be used in JavaScript
     * @return Map<Class label, Map<Attribute name, Map<Attribute label, Label occurrence>>> in JSON format
     * @throws JsonProcessingException ...
     */
    public String mapToJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Map<String, Integer>>> countMap = new HashMap<>();
        groups.forEach((classLabel, attributeMap) ->
                countMap.put(classLabel, attributeMap.getJsonMap()));
        return objectMapper.writeValueAsString(countMap);
    }

    /**
     * Main function for testing class
     * @param args no args
     * @throws IOException if file doesn't exist
     */
    public static void main(String[] args) throws IOException {
        String file = "C:/Program Files/Weka-3-8-4/data/iris-missing.arff";
        LabelCounter labelCounter = new LabelCounter();
        labelCounter.readData(new File(file));
        labelCounter.setGroups();
        labelCounter.countLabels();
        System.out.println(labelCounter.mapToJSON());
    }
}
