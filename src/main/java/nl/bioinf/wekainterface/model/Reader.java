package nl.bioinf.wekainterface.model;

import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface for a class that reads and saves files
 */
public interface Reader {

    /**
     * Given an Arff file, read its contents and return an Instances object with those contents
     */
    Instances readArff(File file) throws IOException;

    /**
     * Given a CSV, convert the CSV file to an Arff file and save it to a directory.
     */
    Instances readCsv(File file, String delimiter) throws IOException;


    /**
     * Gets the names of all demo datasets that are included in WEKA
     * @return List of names
     */
    List<String> getDataSetNames();

    /**
     * Saves given file to a directory
     * @param file Arff file
     */
    String saveArff(File file) throws IOException;
}

