package nl.bioinf.wekainterface.service;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;


import nl.bioinf.wekainterface.model.AlgorithmsInformation;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is a setup for the SerializationAlgorithmInformation
 * @author Bart Engels
 */

@Service
public class SerializationServiceUploadedFiles {

    public void serialization(ArrayList<String> algortihmsInformations, File uniqueID) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(uniqueID);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(algortihmsInformations);
            out.close();
            fileOut.close();
            //System.out.print("Serialized data is saved in /tmp/{random.string}.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public ArrayList<String> deserialization(File uniqueID){
        try {
            FileInputStream fileIn = new FileInputStream(uniqueID);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            String UloadedFile;
            AlgorithmsInformation[] UloadedFiles = new AlgorithmsInformation[5];
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList = (ArrayList<String>)in.readObject();
            in.close();
            fileIn.close();
            return stringArrayList;
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
            throw new RuntimeException("oops");
        }}
}
