package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.AlgorithmsInformation;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is a setup for the SerializationAlgorithmInformation
 * @author Bart Engels
 */

@Service
public class SerializationService {

    public void serialization(ArrayList<AlgorithmsInformation> algorithmsInformation, File uniqueID) {
        try {
            FileOutputStream fileOutputStream =
                    new FileOutputStream(uniqueID);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(algorithmsInformation);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public ArrayList<AlgorithmsInformation> deserialization(File uniqueID){
        try {
        FileInputStream fileIn = new FileInputStream(uniqueID);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileIn);
        AlgorithmsInformation algorithmsInformation;
        AlgorithmsInformation[] algorithmsInformations = new AlgorithmsInformation[5];
        ArrayList<AlgorithmsInformation> algorithmsInformationArrayList = new ArrayList<>();
        algorithmsInformationArrayList = (ArrayList<AlgorithmsInformation>)objectInputStream.readObject();
        objectInputStream.close();
        fileIn.close();
        return algorithmsInformationArrayList;
    } catch (IOException | ClassNotFoundException classNotFoundOrIoException) {
        classNotFoundOrIoException.printStackTrace();
       throw new RuntimeException("RuntimeException");
    }}
}