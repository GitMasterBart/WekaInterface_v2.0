package nl.bioinf.wekainterface.model;

import weka.classifiers.evaluation.Evaluation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class AlgortihmsInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String algorithmsName;
    private final String uploadFileDemoFileUsedAlgorithme;
    private final SimpleDateFormat simpleDateFormat;
    private final Evaluation evaluationAlgorithm;



    public AlgortihmsInformation(String algortihmsInformation, String algorithmsParameters, SimpleDateFormat simpleDateFormat, Evaluation evaluationAlgorithm) {
        this.algorithmsName = algortihmsInformation;
        this.uploadFileDemoFileUsedAlgorithme = algorithmsParameters;
        this.simpleDateFormat = simpleDateFormat;
        this.evaluationAlgorithm = evaluationAlgorithm;

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

    public Evaluation getEvaluationAlgorithm() {
        return evaluationAlgorithm;
    }
}