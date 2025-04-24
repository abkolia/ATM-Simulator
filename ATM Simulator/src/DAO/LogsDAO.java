package DAO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogsDAO {

    public static void log(String username1, String username2, double amount) {
        LocalDateTime currentTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yy:hh:mm:ss");
        String formattedDate = currentTime.format(formatter);

        String log = "";
        log += "Transfered from: " + username1 + ", to: " + username2 + ", amount: " + amount + ", at time: " + formattedDate;
        System.out.println(log);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/DAO/Logs.txt", true));
            writer.write(log);
            writer.newLine();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
