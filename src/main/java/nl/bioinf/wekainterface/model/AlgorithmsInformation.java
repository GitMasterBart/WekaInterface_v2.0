package nl.bioinf.wekainterface.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Bart Engels
 */

public class AlgorithmsInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String algorithmsName;
    private String uploadFileDemoFileUsedAlgorithm;
    private SimpleDateFormat simpleDateFormat;

    public AlgorithmsInformation(String algorithmsInformation, SimpleDateFormat simpleDateFormat) {
        this.algorithmsName = algorithmsInformation;
        //this.uploadFileDemoFileUsedAlgorithm = algorithmsParameters;
        this.simpleDateFormat = simpleDateFormat;
    }

    public void Algorithm(String algorithmsInformation, String algorithmsParameters, SimpleDateFormat simpleDateFormat) {
        this.algorithmsName = algorithmsInformation;
        this.uploadFileDemoFileUsedAlgorithm = algorithmsParameters;
        this.simpleDateFormat = simpleDateFormat;
    }

    public String getAlgorithmsName() {
        return algorithmsName;
    }

    public String getUploadFileDemoFileUsedAlgorithm() {
        return uploadFileDemoFileUsedAlgorithm;
    }

    public String getSimpleDateFormat() {
        Date date = Calendar.getInstance().getTime();
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

}