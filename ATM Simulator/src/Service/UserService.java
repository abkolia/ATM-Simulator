package Service;

import DAO.UserDAO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserService {
    private String username;
    private int pin;
    private double balance;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBalance(int amount){
        this.balance = amount;
    }

    public void setPin(int pin){
        this.pin = pin;
    }

    public String getUsername(){
        return username;
    }

    public int getPin(){
        return pin;
    }

    public double getBalance(){
        return balance;
    }

    public void deposit(double amount){
        this.balance += amount;
    }

    public void withdraw(double amount){
        this.balance -= amount;
    }

    public static void changeBalance(String username, double amount, boolean isDeposit){
        UserService currentUser = UserDAO.getUserByUsername(username);
        if(currentUser != null){
            if(isDeposit){
                currentUser.deposit(amount);
                System.out.printf("Деньги успешно внесены на счет пользователя %s! \n", username);
            } else{
                if(currentUser.getBalance() >= amount){
                    currentUser.withdraw(amount);
                    System.out.printf("Вы успешно вывели деньги со счета %s! \n", username);
                }
                else {
                    System.out.println("Недостаточно средств!");
                }
            }
            UserDAO.updateUser(currentUser);
        }
        else {
            System.out.println("Пользователь не найден.");
        }
    }

    public static void changePin(String username, int newPin){
        UserService currentUser = new UserService();
        currentUser.setUsername(username);
        currentUser.setPin(newPin);

        UserDAO.changePinDB(currentUser);
    }

}
