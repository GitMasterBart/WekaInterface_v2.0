package nl.bioinf.wekainterface.model;

public class AlgortihmsInformation {
    private final String name;
    private final String[] information;

    public AlgortihmsInformation(String name, String[] informtation) {
        this.name = name;
        this.information = informtation;
    }

    public String getName() {
        return name;
    }

    public String[] getInformation() {
        return information;
    }
}
