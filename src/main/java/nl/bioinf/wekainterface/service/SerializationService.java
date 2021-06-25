package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.AlgorithmsInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is a setup for the SerializationAlgorithmInformation
 * Used for adding files to history list.
 * @author Bart Engels
 */

@Service
public class SerializationService {
    Logger logger = LoggerFactory.getLogger(SerializationService.class);

    public void serialization(ArrayList<AlgorithmsInformation> algorithmsInformation, File uniqueID) {
        try {
            logger.info("Writing information to new temporary file: " + uniqueID.getName());
            FileOutputStream fileOutputStream =
                    new FileOutputStream(uniqueID);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(algorithmsInformation);
            objectOutputStream.close();
            fileOutputStream.close();
            logger.info("Finished writing information to temporary file: " + uniqueID.getName());
        } catch (IOException ioException) {
            logger.error("Error! Something went wrong with writing to temporary file: " + uniqueID.getName());
            ioException.printStackTrace();
        }
    }

    public ArrayList<AlgorithmsInformation> deserialization(File uniqueID){
        try {
            logger.info("Deserializing file: " + uniqueID.getName());
            FileInputStream fileIn = new FileInputStream(uniqueID);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileIn);
//        AlgorithmsInformation algorithmsInformation;
//        AlgorithmsInformation[] algorithmsInformations = new AlgorithmsInformation[5];
//        ArrayList<AlgorithmsInformation> algorithmsInformationArrayList = new ArrayList<>();
            ArrayList<AlgorithmsInformation> algorithmsInformationArrayList = (ArrayList<AlgorithmsInformation>)objectInputStream.readObject();
            objectInputStream.close();
            fileIn.close();
            logger.info("Finished deserializing file: " + uniqueID.getName());
            return algorithmsInformationArrayList;
    } catch (IOException | ClassNotFoundException classNotFoundOrIoException) {
            logger.error("Error! Something went wrong with deserializing file: " + uniqueID.getName());
            classNotFoundOrIoException.printStackTrace();
            throw new RuntimeException();
    }}
}