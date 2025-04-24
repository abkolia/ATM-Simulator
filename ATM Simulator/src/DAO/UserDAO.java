package DAO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Service.CurrentUser;
import Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDAO {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void saveToDB(UserService currentUser){
        try{
            String json = objectMapper.writeValueAsString(currentUser);

            // Запись в файл
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/DAO/UserDB.txt", true));
            writer.write(json);
            writer.newLine();
            writer.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static UserService getUserByUsername(String username){
        try (BufferedReader reader = new BufferedReader(new FileReader("src/DAO/UserDB.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UserService userFromFile = objectMapper.readValue(line, UserService.class);

                if (username.equals(userFromFile.getUsername())) {
                    return userFromFile;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUser(UserService updatedUser){
        List<String> lines = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader("src/DAO/UserDB.txt"))){
            String line;
            while((line = reader.readLine()) != null){
                if (line.trim().isEmpty()) {
                    continue;
                }

                UserService userFromFile = objectMapper.readValue(line, UserService.class);

                if(updatedUser.getUsername().equals(userFromFile.getUsername())){
                    line = objectMapper.writeValueAsString(updatedUser);
                }

                lines.add(line);
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("src/DAO/UserDB.txt"))){
            for(String line : lines){
                writer.write(line);
                writer.newLine();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static double getBalanceByUsername(String username){
        try(BufferedReader reader = new BufferedReader(new FileReader("src/DAO/UserDB.txt"))) {
            String line;
            while((line = reader.readLine()) != null){
                UserService userFromFile = objectMapper.readValue(line, UserService.class);
                if(username.equals(userFromFile.getUsername())){
                    return userFromFile.getBalance();
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Пользователь не найден!");
        return -1;
    }

    public static void changePinDB(UserService currentUser){
        List<String> lines = new ArrayList<>();

        // Читаем файл
        try(BufferedReader reader = new BufferedReader(new FileReader("src/DAO/UserDB.txt"))){
            String line;
            while((line = reader.readLine()) != null){
                UserService userFromFile = objectMapper.readValue(line, UserService.class);

                if(currentUser.getUsername().equals(userFromFile.getUsername())){
                    userFromFile.setPin(currentUser.getPin());
                    line = objectMapper.writeValueAsString(userFromFile);
                }

                lines.add(line);
            }
        } catch(IOException e){
            e.printStackTrace();
        }

        // Перезапись
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("src/DAO/UserDB.txt"))){
            for(String line : lines){
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static boolean isUniqueUsername(String username){
        try(BufferedReader reader = new BufferedReader(new FileReader("src/DAO/UserDB.txt"))) {
            String line;

            while((line = reader.readLine()) != null){
                UserService userFromFile = objectMapper.readValue(line, UserService.class);

                if(username.equals(userFromFile.getUsername())) return true;
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean pinMatcher(String username, int pin){
        try(BufferedReader reader = new BufferedReader(new FileReader("src/DAO/UserDB.txt"))) {
            String line;

            while((line = reader.readLine()) != null){
                UserService userFromFile = objectMapper.readValue(line, UserService.class);

                if(username.equals(userFromFile.getUsername())) {
                    if(pin == userFromFile.getPin()){
                        return true;
                    }
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
}
