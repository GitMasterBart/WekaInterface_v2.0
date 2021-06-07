package nl.bioinf.wekainterface.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AlgortihmsInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String algorithmsName;
    private String uploadFileDemoFileUsedAlgorithme;
    private SimpleDateFormat simpleDateFormat;

    public AlgortihmsInformation(String algortihmsInformation, SimpleDateFormat simpleDateFormat) {
        this.algorithmsName = algortihmsInformation;
        //this.uploadFileDemoFileUsedAlgorithme = algorithmsParameters;
        this.simpleDateFormat = simpleDateFormat;
    }

    public void Algorithm(String algortihmsInformation, String algorithmsParameters, SimpleDateFormat simpleDateFormat) {
        this.algorithmsName = algortihmsInformation;
        this.uploadFileDemoFileUsedAlgorithme = algorithmsParameters;
        this.simpleDateFormat = simpleDateFormat;
    }

    public String getAlgorithmsName() {
        return algorithmsName;
    }

    public String getUploadFileDemoFileUsedAlgorithme() {
        return uploadFileDemoFileUsedAlgorithme;
    }

    public String getSimpleDateFormat() {
        Date date = Calendar.getInstance().getTime();
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }


}