package dao;

import service.SSS;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the sssfile manager component used in the dao layer.
 */
public class SSSFileManager implements FileLoader<SSS>{

    private static final String TXT_FILE_PATH = "src/main/resources/SSSCont1.csv";
    
    /**
     * Loads file.
     * @return resulting value produced by this method.
     */
    @Override
    public List<SSS> loadFile() {
        List<SSS> deductionRecord = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(TXT_FILE_PATH))) {
            bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] record = line.split(",");
                String compensationRange = record[0];
                double contribution = Double.parseDouble(record[1]);

                SSS deductionRecordItem = new SSS(compensationRange, contribution);
                deductionRecord.add(deductionRecordItem);
            }
        } catch (IOException e) {
            handleException(e);
        }
        return deductionRecord;
    }

    /**
     * Handles handle exception.
     * @param e input value needed by this method.
     */
    private static void handleException(Exception e) {
            e.printStackTrace();
        }
}
