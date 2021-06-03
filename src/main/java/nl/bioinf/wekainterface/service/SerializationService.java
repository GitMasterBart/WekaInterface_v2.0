package nl.bioinf.wekainterface.service;

import nl.bioinf.wekainterface.model.AlgortihmsInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is a setup for the SerializationAlgorithmInformation
 * @author Bart Engels
 */

@Service
public class SerializationService {
    @Value("${serialization.path}")
    private String serializationPath;

    public void serialization(ArrayList<AlgortihmsInformation> e) {
        //algorithmsInformation.add((AlgortihmsInformation) e);
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(serializationPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(e);
            out.close();
            fileOut.close();
            System.out.print("Serialized data is saved in /tmp/algorithmeData.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public ArrayList<AlgortihmsInformation> deserialization(){
        try {
        FileInputStream fileIn = new FileInputStream(serializationPath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        AlgortihmsInformation algortihmsInformation;
        AlgortihmsInformation[] algortihmsInformations = new AlgortihmsInformation[5];
        ArrayList<AlgortihmsInformation> algortihmsInformationArrayList = new ArrayList<>();
        algortihmsInformationArrayList = (ArrayList<AlgortihmsInformation>)in.readObject();
        in.close();
        fileIn.close();
        return algortihmsInformationArrayList;
    } catch (IOException | ClassNotFoundException i) {
        i.printStackTrace();
       throw new RuntimeException("oops");
    }}
}