package nl.bioinf.wekainterface.model;

import java.util.HashMap;
import java.util.Map;


/**
 @author: Jelle 387615
 This class holds a Map with labels and their counts
 */
public class LabelMap {
    private Map<String, Integer> labelMap = new HashMap<>();

    /**
     * Adds the label to the map and sets its count to 0
     * @param label Attribute label
     */
    public void addLabel(String label){
        labelMap.put(label, 0);
    }

    /**
     * Increases the count of the label by 1
     * @param label Attribute label
     */
    public void incrementLabel(String label){
        labelMap.computeIfPresent(label, (key, oldValue) -> oldValue+1);
    }

    public Map<String, Integer> getLabelMap(){
        return labelMap;
    }
}
