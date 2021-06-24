package nl.bioinf.wekainterface.model;

import nl.bioinf.wekainterface.errorhandling.InvalidDataSetException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class LabelCounterTest {
    @Value("${example.data.path}")
    private String exampleFilesFolder;
    InstanceReader instanceReader;
    LabelCounter labelCounter;

    @BeforeEach
    @Test
    public void setInstanceReader(){
        instanceReader = new InstanceReader();
    }

    @BeforeEach
    @Test
    public void setLabelCounter(){
        labelCounter = new LabelCounter();
    }

    @DisplayName("Testing with nominal class attribute")
    @Test
    public void nominalClassAttribute() throws IOException {
        Instances instances = instanceReader.readArff(new File(exampleFilesFolder + "/weather.nominal.arff"));
        labelCounter.setInstances(instances);
        labelCounter.setGroups();
        labelCounter.countLabels();
        System.out.println("Weather nominal dataset as a Map\n" + labelCounter.mapToJSON());
    }

    @DisplayName("Testing with numeric class attribute")
    @Test
    public void numericClassAttribute() throws IOException {
        Instances instances = instanceReader.readArff(new File(exampleFilesFolder + "/cpu.arff"));
        labelCounter.setInstances(instances);
        labelCounter.setGroups();
        labelCounter.countLabels();
        System.out.println("cpu dataset as a Map\n" + labelCounter.mapToJSON().replaceAll("},", "},\n"));
    }

    @DisplayName("Testing InstanceReader with a dataset with only 1 attribute")
    @Test
    public void illegalDataset(){
        Throwable exception = Assertions.assertThrows(InvalidDataSetException.class,
                () -> instanceReader.readCsv(new File("src/test/java/nl/bioinf/wekainterface/model/invalidDataset.csv"), ","));
        Assertions.assertEquals(exception.getMessage(), "Dataset only contains 1 or 0 attributes");
    }

}