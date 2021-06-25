package nl.bioinf.wekainterface.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is a setup for the SerializationAlgorithmInformation.
 * Used for creating a list for all uploaded files.
 * @author Bart Engels
 */

@Service
public class SerializationServiceUploadedFiles {
    Logger logger = LoggerFactory.getLogger(SerializationServiceUploadedFiles.class);

    public void serialization(ArrayList<String> algorithmsInformations, File uniqueID) {
        try {
            logger.info("Writing filename to new temporary file: " + uniqueID.getName());
            FileOutputStream fileOut =
                    new FileOutputStream(uniqueID);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(algorithmsInformations);
            out.close();
            fileOut.close();
            logger.info("Finished writing filename to new temporary file: " + uniqueID.getName());
        } catch (IOException i) {
            logger.error("Error! Something went wrong with writing to temporary file: " + uniqueID.getName());
            i.printStackTrace();
        }
    }

    public ArrayList<String> deserialization(File uniqueID){
        try {
            logger.info("Deserializing file: " + uniqueID.getName());
            FileInputStream fileIn = new FileInputStream(uniqueID);
            ObjectInputStream in = new ObjectInputStream(fileIn);
//            String UloadedFile;
//            AlgorithmsInformation[] UloadedFiles = new AlgorithmsInformation[5];
//            ArrayList<String> stringArrayList = new ArrayList<>();
            ArrayList<String> stringArrayList = (ArrayList<String>)in.readObject();
            in.close();
            fileIn.close();
            logger.info("Finished deserializing file: " + uniqueID.getName());
            return stringArrayList;
        } catch (IOException | ClassNotFoundException i) {
            logger.error("Error! Something went wrong with deserializing file: " + uniqueID.getName());
            i.printStackTrace();
            throw new RuntimeException();
        }}
}
