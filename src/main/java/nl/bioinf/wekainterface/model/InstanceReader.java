package nl.bioinf.wekainterface.model;

import nl.bioinf.wekainterface.errorhandling.InvalidDataSetProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to process and save datasets as arff's
 * @author Jelle, Marijke
 */

@Component
public class InstanceReader implements Reader{
    private final Logger logger = LoggerFactory.getLogger(InstanceReader.class);
    @Value("${example.data.path}")
    private String exampleFilesFolder;
    //TODO: tempFolder wordt niet gebruikt in deze class
    @Value("${temp.data.path}")
    private String tempFolder;

    /**
     * This method reads an arff file and returns the instances.
     * TODO make it so that the class index is not the last attribute by default. Give the user the option to select a different attribute as class attribute.
     * @param file file as a File object
     * @return Dataset instances
     * @throws IOException if the file doesn't exist
     */
    @Override
    public Instances readArff(File file) {
        try {
            logger.info("Read arff file: " + file.getName() + ", and create instances");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(reader);
            Instances data = arffReader.getData();
            checkDatasetValidity(data);
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (IOException e){
            logger.error("Error! Something goes wrong with reading arff file " + file.getName());
            return null;
        }
    }

    /**
     * Convert a CSV file to arff Format
     * TODO The delimiter is currently always set as ',' in ExplorerController.java, this should be able to be changed by the user.
     * @param file file as File object
     * @param delimiter what the lines are separated by
     * @return Instances data
     * @throws IOException if file doesn't exist
     */
    @Override
    public Instances readCsv(File file, String delimiter) {
        try {
            logger.info("Read csv file: " + file.getName() + ", and create instances");
            CSVLoader loader = new CSVLoader();
            loader.setSource(file);
            loader.setFieldSeparator(delimiter);
            Instances data = loader.getDataSet();
            checkDatasetValidity(data);
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (IOException e){
            logger.error("Error! Something goes wrong with reading csv file " + file.getName());
            return null;
        }
    }

    /**
     * This method creates a list of the demo data names.
     * @return List of demo filenames
     */
    @Override
    public List<String> getDataSetNames() {
        try {
            File folder = new File(exampleFilesFolder);
            File[] listOfFiles = folder.listFiles();
            List<String> fileNames = new ArrayList<>();
            for (File file: listOfFiles){
                fileNames.add(file.getName());
            }
            return fileNames;
        } catch (Exception e){
            logger.error("Error! something goes wrong in reading the file directory. Please check your path");
            return null;
        }
    }

    /**
     * Checks wether the dataset can effectively be used for machine learning algorithms.
     * TODO add other checks to see if the dataset is valid, like if there are too many missing datapoints.
     * @param instances
     */
    private void checkDatasetValidity(Instances instances){
        if (instances.numAttributes() <= 1){
            logger.error("Dataset only contains 1 or 0 attributes");
            throw new InvalidDataSetProcessException("Dataset only contains 1 or 0 attributes");
        }
    }
}
