package nl.bioinf.wekainterface.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class AlgortihmsInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String algorithmsName;
    private final String uploadFileDemoFileUsedAlgorithme;



    public AlgortihmsInformation(String algortihmsInformation, String algorithmsParameters) {
        this.algorithmsName = algortihmsInformation;
        this.uploadFileDemoFileUsedAlgorithme = algorithmsParameters;
    }

    public String getAlgorithmsName() {
        return algorithmsName;
    }

    public String getUploadFileDemoFileUsedAlgorithme() {
        return uploadFileDemoFileUsedAlgorithme;
    }



}