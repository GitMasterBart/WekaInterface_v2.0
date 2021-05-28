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

    public void serialization(ArrayList<Object> e) {
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
    AlgortihmsInformation e = null;
        try {
        FileInputStream fileIn = new FileInputStream(serializationPath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        AlgortihmsInformation algortihmsInformation = null;
        AlgortihmsInformation[] woj = new AlgortihmsInformation[5];
        ArrayList<AlgortihmsInformation> woi = new ArrayList<>();
        woi = (ArrayList<AlgortihmsInformation>)in.readObject();
        //e = (AlgortihmsInformation) in.readObject();
        in.close();
        fileIn.close();
        return woi;
    } catch (IOException | ClassNotFoundException i) {
        i.printStackTrace();
       throw new RuntimeException("oops");
    }}
}



