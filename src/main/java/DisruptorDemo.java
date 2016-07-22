import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Sidath Weerasinghe and Udaka Manawadu
 *This DisruptorDemo contains the main method.
 *
 */

public class DisruptorDemo {
    private static Logger logger = LoggerFactory.getLogger(DisruptorDemo.class);

    public static void main(String[] args) throws FileNotFoundException {
        LMAXWriter lmaxWriter = new LMAXWriter();
        logger.info("Initializing lmax disruptor.");
        lmaxWriter.setRingBufferSize(8); //deliberately set. Final ring buffer size would be 8.
        lmaxWriter.init();

        //Set File path
        String fileName = "message.txt";
        String line = null;
        int lines = 0;
        // variable lines Calculate number of lines of Selected file.
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        try {
            //count how many line on  the  txy file.
            while (reader.readLine() != null){
                lines++;}
            reader.close();
            // FileReader reads text files in the default encoding..
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            for (int i = 0; i < lines; i++) {
                line = bufferedReader.readLine(); //read line by line.
                // submit messages to write concurrently using disruptor
                lmaxWriter.submitMessage(line);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("All message submitted.");

        lmaxWriter.close();
        logger.info("Program executed successfully.");
    }
}