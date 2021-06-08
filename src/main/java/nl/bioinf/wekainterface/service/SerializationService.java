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
            FileOutputStream fileOut =
                    new FileOutputStream(uniqueID);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(algorithmsInformation);
            out.close();
            fileOut.close();
            //System.out.print("Serialized data is saved in /tmp/{random.string}.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public ArrayList<AlgorithmsInformation> deserialization(File uniqueID){
        try {
        FileInputStream fileIn = new FileInputStream(uniqueID);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        AlgorithmsInformation algorithmsInformation;
        AlgorithmsInformation[] algorithmsInformations = new AlgorithmsInformation[5];
        ArrayList<AlgorithmsInformation> algorithmsInformationArrayList = new ArrayList<>();
        algorithmsInformationArrayList = (ArrayList<AlgorithmsInformation>)in.readObject();
        in.close();
        fileIn.close();
        return algorithmsInformationArrayList;
    } catch (IOException | ClassNotFoundException i) {
        i.printStackTrace();
       throw new RuntimeException("oops");
    }}
}