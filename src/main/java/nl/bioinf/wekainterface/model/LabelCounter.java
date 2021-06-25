package nl.bioinf.wekainterface.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.bioinf.wekainterface.errorhandling.InvalidDataSetProcessException;
import org.springframework.stereotype.Component;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.Stats;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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

    public void setInstances(Instances instances){
        this.instances = instances;
    }

    /**
     * Creates a Map<String, Map<String, Map<String, Integer>>> structure. First map holds each class label as key with
     * a second Map as value. This Map holds each attribute as key and a third Map as its value. This third Map holds
     * the labels for each attribute as its key and the occurrence of those labels as its value. The occurrence is set
     * at 0.
     * TODO if the class attribute is numeric, discretize the group keys as an interval, just like in setLabelsNumeric()
     * TODO This class can only handle datasets with numeric, nominal or Date class attributes. A String class attribute will cause an exception. This should be handled in a better way.
     */
    public void setGroups(){
        if (this.instances.numAttributes() == 2){
            setGroupsTwoAttributes();
        }
        if (this.instances.numAttributes() > 2){
            if (this.instances.classAttribute().isDate()){
                setGroupsDate();
            }
            if (this.instances.classAttribute().isNumeric()){
                setGroupsNumeric();
            }
            else {
                setGroupsNominal();
            }
        }
    }

    /**
     * If the class attribute is numeric, discretizes the class labels into intervals.
     */
    private void setGroupsNumeric() {
        Stats stats = instances.attributeStats(instances.classIndex()).numericStats;
        IntervalStats intervalStats = setIntervalStats(stats);
        for (int groupIndex = 0; groupIndex < intervalStats.getNumGroups(); groupIndex++){
            String intervalLabel = getIntervalLabel(stats, intervalStats, groupIndex);
            AttributeMap attributes = setAttributes();
            groups.put(intervalLabel, attributes);
        }
    }

    /**
     * Sets the groups for a dataset where the class Attribute is nominal
     */
    private void setGroupsNominal() {
        int numberOfValues = this.instances.classAttribute().numValues();
        for (int classLabelIndex = 0; classLabelIndex < numberOfValues; classLabelIndex++) {
            // Setting the class label as the key for the first Map, in the case of weather.nominal = {yes,no}
            String classLabel = this.instances.classAttribute().value(classLabelIndex);
            // creating the 2nd Map with attribute names as keys and labels as value's
            AttributeMap attributes = setAttributes();
            this.groups.put(classLabel, attributes);
        }
    }

    /**
     * Sets the groups for class attributes that are not nominal I.E. are numeric or dates. Only used when dataset has
     * more than 2 attributes
     */
    private void setGroupsDate() {
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

    /**
     * TODO This method was specifically designed for airline.arff which is a dataset with a Date class attribute. Other datasets with only 2 attributes can't be used with this method. Adjust it so that it can.
     * Set twoAttributesGroups for a dataset with only two attributes.
     */
    private void setGroupsTwoAttributes() {
        onlyTwoAttributes = true;
        AttributeStats stats = this.instances.attributeStats(this.instances.classIndex());
        if (this.instances.classAttribute().isDate() && stats.totalCount == stats.uniqueCount){
            for (int classLabelIndex=0;classLabelIndex< stats.totalCount;classLabelIndex++) {
                // Setting the class label as the key for the first Map, in the case of weather.nominal = {yes,no}
                double classValue = this.instances.instance(classLabelIndex).value(this.instances.classIndex());
                String dateFormat = this.instances.classAttribute().getDateFormat();
                String classLabel = Util.parseDate(classValue, dateFormat);
                // creating the 2nd Map with attribute names as keys and labels as value's
                try{
                    this.twoAttributeGroups.put(classLabel,
                            Double.parseDouble(this.instances.instance(classLabelIndex).toString().split(",")[this.instances.classIndex()-1]));
                }catch (NumberFormatException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * For every attribute in the dataset creates an entry in a Map with the attribute name as the key and a Map as the
     * value for this key. This Map holds each attribute label as its key and the occurrence of this label as its value.
     * TODO values that are actually dates are parsed as numeric values, expand the method so that it parses dates correctly.
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

        IntervalStats intervalStats = setIntervalStats(stats);

        for (int groupIndex = 0; groupIndex < intervalStats.getNumGroups(); groupIndex++){
            String intervalLabel = getIntervalLabel(stats, intervalStats, groupIndex);
            labelMap.addLabel(intervalLabel);
        }
        attributeMap.addAttribute(attribute, labelMap);
    }

    private String getIntervalLabel(Stats stats, IntervalStats intervalStats, int groupIndex) {
        double intervalStart = intervalStats.getIntervalStart();
        double groupInterval = intervalStats.getGroupInterval();
        double numGroups = intervalStats.getNumGroups();
        int numDecimals = intervalStats.getNumDecimals();

        String labelGroup;
        double intervalEnd = Util.roundTo((intervalStart + groupInterval), numDecimals);
        if (groupIndex == numGroups-1){
            labelGroup = intervalStart + "-" + Util.roundTo((stats.max + Util.roundTo((groupInterval/10), numDecimals)), numDecimals);
        }else {
            labelGroup = intervalStart + "-" + intervalEnd;
        }
        intervalStats.setIntervalStart(Util.roundTo(groupInterval + intervalStart, numDecimals));
        return labelGroup;
    }

    private double getNumIntervals(Stats stats) {
        double numGroups = Math.round((stats.max - stats.min) / stats.stdDev);
        if (numGroups == 0) {
            numGroups = 1;
        }
        return numGroups;
    }


    private IntervalStats setIntervalStats(Stats stats){
        // Number of groups is based the amount of times the standard deviation fits into the interval between the
        // minimum and maximum value of the attribute
        double numGroups = getNumIntervals(stats);
        // Number of decimals used in the dataset
        int numDecimals = Util.numDecimals(stats.min);

        double groupInterval = Util.roundTo((stats.max - stats.min) / numGroups, numDecimals);
        double intervalStart = stats.min;

        return new IntervalStats(numGroups, numDecimals, groupInterval, intervalStart);
    }

    /**
     * For each label in the instance, increase it's occurrence count by 1, depending on which class label it has.
     */
    public void countLabels(){
        if (!onlyTwoAttributes){
            for (int instanceIndex = 0; instanceIndex < instances.numInstances(); instanceIndex++){
                Instance instance = instances.instance(instanceIndex);
                String key = instance.toString().split(",")[instances.classIndex()];
                System.out.println("Key = " + key);
                for (int valueIndex = 0; valueIndex < instance.numValues(); valueIndex++){
                    AttributeMap attributeMap = new AttributeMap();
                    attributeMap = getAttributeMap(key, attributeMap);
                    if(valueIndex != instance.classIndex()){
                        String value = instance.toString().split(",")[valueIndex];
                        incrementLabel(value, valueIndex, attributeMap);
                    }
                }
            }
        }
    }

    /**
     * given a value
     * @param key a split instance
     * @param attributeMap empty attribute map that is to be set to the one corresponding with the given key
     * @return the attributeMap corresponding with the given key
     */
    private AttributeMap getAttributeMap(String key, AttributeMap attributeMap) {
        if (instances.classAttribute().isNumeric()){
            double numericValue = Double.parseDouble(key);
            attributeMap = groups.get(getGroupInterval(numericValue));
        }else if (instances.classAttribute().isNominal()){
            attributeMap = groups.get(key);
        }
        return attributeMap;
    }

    /**
     * given a stringValue, increment the occurrence of that
     * TODO add a possibility to also count the occurrence of a date. That is currently not implemented.
     * @param stringValue instance value of index 'valueIndex'
     * @param valueIndex index of the attribute of which the value must be incremented
     * @param attributeMap the attributeMap that holds the labels
     */
    private void incrementLabel(String stringValue, int valueIndex, AttributeMap attributeMap) {
        String attribute = attributeArray.get(valueIndex);
        LabelMap labelMap = attributeMap.getLabelMap(attribute);
        try{//If stringValue is numeric
            double value = Double.parseDouble(stringValue);
            System.out.println("Counting Numeric");
            countNumeric(value, labelMap);
        }catch (NumberFormatException e){//Not numeric
            System.out.println("Counting Nominal!");
            countNominal(stringValue, labelMap);
        }
    }

    /**
     * Checks wether the given numericValue is fits in the given interval.
     * @param interval String[]: an interval in [x, y] format
     * @param numericValue a numeric value, possibly between x and y
     * @return boolean
     */
    private boolean isBetweenInterval(String[] interval, double numericValue){
        return numericValue >= Double.parseDouble(interval[0]) && numericValue < Double.parseDouble(interval[1]);
    }

    /**
     * given a numeric class value, return the key interval it fits in from the groups map.
     * @param numericValue
     * @return
     */
    private String getGroupInterval(double numericValue){
        for (Map.Entry<String, AttributeMap> entry : groups.entrySet()){
            String[] interval = entry.getKey().split("-");
            if (isBetweenInterval(interval, numericValue)){
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Given a numericValue determine in which [x-y] interval it fits and increment the occurrence of that [x-y] label
     * @param numericValue numeric numericValue
     * @param labelMap Map<[x-y], [Occurrence of numericValue between x and y]>
     */
    private void countNumeric(double numericValue, LabelMap labelMap){
        for (Map.Entry<String, Integer> entry: labelMap.getLabelMap().entrySet()){
            String[] interval = entry.getKey().split("-");// Iterate over labels in labelMap

            if (isBetweenInterval(interval, numericValue)){
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
        System.out.println("Label = " + label);
        labelMap.incrementLabel(label);
    }

    public Instances getInstances() {
        return instances;
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

    public void setupLabelCounter(Instances instances, RedirectAttributes redirect) throws JsonProcessingException {
        LabelCounter labelCounter = new LabelCounter();
        labelCounter.setInstances(instances);
        labelCounter.setGroups();
        labelCounter.countLabels();
        redirect.addFlashAttribute("data", labelCounter.mapToJSON());
        redirect.addFlashAttribute("attributes", labelCounter.getAttributeArray());
        redirect.addFlashAttribute("classLabel", labelCounter.getClassLabel());
        labelCounter.resetLabelCounter();
    }
}