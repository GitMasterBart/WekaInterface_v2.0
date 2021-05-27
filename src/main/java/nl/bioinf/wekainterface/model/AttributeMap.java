package nl.bioinf.wekainterface.model;

import java.util.HashMap;
import java.util.Map;

/**
 @author: Jelle 387615
 This class holds a Map with attribute names as keys and a LabelMap as its value.
 */
public class AttributeMap {
    private Map<String, LabelMap> attributeMap = new HashMap<>();

    /**
     * Add an attribute to attributeMap and set its value to the given labelMap
     * @param attribute Attribute name
     * @param labelMap Attribute label
     */
    public void addAttribute(String attribute, LabelMap labelMap){
        attributeMap.put(attribute, labelMap);
    }

    /**
     * Return the labelMap for a given attribute name
     * @param attribute Attribute name
     * @return LabelMap
     */
    public LabelMap getLabelMap(String attribute){
        return attributeMap.get(attribute);
    }

    public Map<String, LabelMap> getAttributeMap() {
        return attributeMap;
    }

    /**
     * convert the attributeMap to a JSON format and return it.
     * @return attributeMap in JSON format
     */
    public Map<String, Map<String, Integer>> getJsonMap(){
        Map<String, Map<String, Integer>> JsonMap = new HashMap<>();
        attributeMap.forEach((attribute, labelMap) ->
                JsonMap.put(attribute, labelMap.getLabelMap()));
        return JsonMap;
    }
}
