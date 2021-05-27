package nl.bioinf.wekainterface.model;

import java.io.Serializable;

public class AlgortihmsInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String algorithmsInformation;
    private final String[] algorithmsParameters;



    public AlgortihmsInformation(String algortihmsInformation, String[] algorithmsParameters) {
        this.algorithmsInformation = algortihmsInformation;
        this.algorithmsParameters = algorithmsParameters;
    }

    public String getAlgorithmsInformation() {
        return algorithmsInformation;
    }

    public String[] getAlgorithmsParameters() {
        return algorithmsParameters;
    }



}