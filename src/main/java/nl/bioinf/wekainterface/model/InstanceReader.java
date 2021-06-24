package nl.bioinf.wekainterface.model;

import nl.bioinf.wekainterface.errorhandling.InvalidDataSetProcessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.experiment.Stats;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to process and save datasets as arff's
 * @author Jelle, Marijke
 */

@Component
public class InstanceReader implements Reader{
    @Value("${example.data.path}")
    private String exampleFilesFolder;
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
    public Instances readArff(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArffLoader.ArffReader arffReader = new ArffLoader.ArffReader(reader);
        Instances data = arffReader.getData();
        checkDatasetValidity(data);
        data.setClassIndex(data.numAttributes() - 1);
        return data;
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
    public Instances readCsv(File file, String delimiter) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setSource(file);
        loader.setFieldSeparator(delimiter);
        Instances data = loader.getDataSet();
        checkDatasetValidity(data);
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /**
     * This method creates a list of the demo data names.
     * @return List of demo filenames
     */
    @Override
    public List<String> getDataSetNames() {
        File folder = new File(exampleFilesFolder);
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File file: listOfFiles){
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    /**
     * Checks wether the dataset can effectively be used for machine learning algorithms.
     * TODO add other checks to see if the dataset is valid, like if there are too many missing datapoints.
     * @param instances
     */
    private void checkDatasetValidity(Instances instances){
        if (instances.numAttributes() <= 1){
            throw new InvalidDataSetProcessException("Dataset only contains 1 or 0 attributes");
        }
    }
}
