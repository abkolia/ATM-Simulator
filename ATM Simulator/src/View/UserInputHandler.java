package View;

import DAO.LogsDAO;
import DAO.UserDAO;
import Service.CurrentUser;
import Service.UserService;

import java.util.Scanner;

enum Actions {
    WITHDRAW_DEPOSIT(1), BALANCE(2), CHANGE_PIN(3), TRANSACTION(4), EXIT(5);

    private int number;

    Actions(int number){
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    public static Actions fromNumber(int number){
        for(Actions action : Actions.values()){
            if(action.getNumber() == number){
                return action;
            }
        }
        return null;
    }
}

enum GreetingActions {
    LOGIN(1), REGISTER(2), EXIT(3);

    private int number;

    GreetingActions(int number){
        this.number = number;
    }

    public int getNumber(){
        return number;
    }

    public static GreetingActions fromNumber(int number){
        for(GreetingActions action : GreetingActions.values()){
            if(action.getNumber() == number){
                return action;
            }
        }
        return null;
    }
}

public class UserInputHandler implements HandleUserActionsInterface{

    ATMConsoleView view = new ATMConsoleView();

    public void handleGreetingScreen(){
        Scanner scanner = new Scanner(System.in);
        GreetingActions action = null;

        while(action != GreetingActions.EXIT){
            System.out.print("Введите число: ");
            int input = scanner.nextInt();

            action = GreetingActions.fromNumber(input);

            if(action == null){System.out.println("Вы ввели неверное число!");}
            else if(action == GreetingActions.EXIT){break;}
            else{
                switch (action){
                    case LOGIN -> {
                        handleLogin();

                    }
                    case REGISTER -> {
                        handleRegistration();
                    }
                }
                break;
            }
        }
    }

    public void handleLogin(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите логин: ");
        String username = scanner.nextLine();

        boolean isInDB = UserDAO.isUniqueUsername(username);
        while(!isInDB){
            System.out.println("Пользователя с таким именем не существует!");

            System.out.print("Введите логин: ");
            username = scanner.nextLine();

            isInDB = UserDAO.isUniqueUsername(username);
        }

        System.out.print("Введите PIN: ");
        int pin = scanner.nextInt();
        boolean matchPinWithUsername = UserDAO.pinMatcher(username, pin);
        while(!matchPinWithUsername){
            System.out.println("Неверный PIN!");

            System.out.print("Введите PIN: ");
            pin = scanner.nextInt();
            matchPinWithUsername = UserDAO.pinMatcher(username, pin);
        }

        CurrentUser.setUsername(username);
        System.out.println("Вы успешно вошли в систему!");
    }

    public void handleRegistration(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Придумайте логин: ");
        String username = scanner.nextLine();

        // Проверить, есть ли логин в бд
        boolean isUniqueLogin = UserDAO.isUniqueUsername(username);
        while(isUniqueLogin){
            System.out.println("Логин уже занят!");
            System.out.print("Придумайте логин: ");
            username = scanner.nextLine();
            isUniqueLogin = UserDAO.isUniqueUsername(username);
        }

        int pin;
        while(true){
            System.out.print("Придумайте PIN (4 цифры): ");
            if(scanner.hasNextInt()){
                pin = scanner.nextInt();
                if(pin >= 1000 && pin <= 9999){
                    break;
                }
                else{
                    System.out.println("PIN должен содержать 4 цифры!");
                }
            }
            else {
                System.out.println("PIN должен быть числом!");
                scanner.next();
            }
        }

        // Добавить юзера в бд
        UserService currentUser = new UserService();
        currentUser.setUsername(username);
        currentUser.setPin(pin);
        currentUser.setBalance(0);
        UserDAO.saveToDB(currentUser);

        CurrentUser.setUsername(currentUser.getUsername());

        System.out.println("Вы успешно зарегистрировались!");

    }

    public void handleWithdrawDeposit(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Внести деньги. 2. Вывести деньги");
        System.out.print("Введите число: ");
        int num = scanner.nextInt();
        while(num > 2 || num < 0){
            System.out.println("Вы ввели неверное число!");
            System.out.println("1. Внести деньги. 2. Вывести деньги");
            System.out.print("Введите число: ");

            num = scanner.nextInt();
        }

        if(num == 1) {
            System.out.print("Введите сумму, которую вы хотите внести: ");
            double amountOfMoney = scanner.nextDouble();

            while(amountOfMoney < 0.1){
                System.out.println("Количество должно быть больше 0!");

                amountOfMoney = scanner.nextDouble();
            }

            UserService.changeBalance(CurrentUser.getUsername(), amountOfMoney, true);
        }
        else if(num == 2){
            System.out.print("Введите сумму, которую вы хотите вывести: ");
            double amountOfMoney = scanner.nextDouble();

            while (amountOfMoney < 0.1) {
                System.out.println("Количество должно быть больше 0!");
                amountOfMoney = scanner.nextDouble();
            }

            UserService.changeBalance(CurrentUser.getUsername(), amountOfMoney, false);


        }
    }

    @Override
    public void handleUserActions() {
        Scanner scanner = new Scanner(System.in);
        Actions action = null;

        while(action != Actions.EXIT){
            new ATMConsoleView().displayMainMenu();

            System.out.print("Введите число: ");
            int input = scanner.nextInt();

            action = Actions.fromNumber(input);

            if(action == null){
                System.out.println("Вы ввели неверное число!");
            } else if(action == Actions.EXIT){
                System.out.println("Выход из программы...");
                break;
            }

            else {
                switch (action){
                    case BALANCE -> {
                        String currentUsername = CurrentUser.getUsername();
                        if(currentUsername != null){
                            double balance = UserDAO.getBalanceByUsername(currentUsername);
                            if(balance != -1){
                                System.out.println("Ваш баланс: " + balance);
                            } else {
                                System.out.println("Пользователь не найден.");
                            }
                        } else {
                            System.out.println("Пользователь не установлен.");
                        }
                    }
                    case WITHDRAW_DEPOSIT -> {
                        handleWithdrawDeposit();
                    }
                    case TRANSACTION -> {
                        handleTransaction();
                    }
                    case CHANGE_PIN -> {
                         String currentUsername = CurrentUser.getUsername();
                        if(currentUsername != null){
                            int newPin;
                            while(true){
                                System.out.print("Придумайте PIN (4 цифры): ");
                                if(scanner.hasNextInt()){
                                    newPin = scanner.nextInt();
                                    if(newPin >= 1000 && newPin <= 9999){
                                        UserService.changePin(currentUsername, newPin);
                                        System.out.println("PIN успешно изменён!");
                                        break;
                                    }
                                    else{
                                        System.out.println("PIN должен содержать 4 цифры!");
                                    }
                                }
                                else {
                                    System.out.println("PIN должен быть числом!");
                                    scanner.next();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void handleTransaction(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите имя пользователя для перевода: ");
        String usernameToTransfer = scanner.nextLine();
        boolean isUsernameInDB = UserDAO.isUniqueUsername(usernameToTransfer);

        while(!isUsernameInDB){
            System.out.println("Пользвателя с таким именем не существует!");

            System.out.print("Введите имя пользователя для перевода: ");
            usernameToTransfer = scanner.nextLine();
            isUsernameInDB = UserDAO.isUniqueUsername(usernameToTransfer);
        }
        String currentUsername = CurrentUser.getUsername();
        UserService currentUser = UserDAO.getUserByUsername(currentUsername);
        UserService userToTransfer = UserDAO.getUserByUsername(usernameToTransfer);

        if(currentUser != null && userToTransfer != null){
            double currentUserBalance = currentUser.getBalance();

            System.out.print("Введите количество денег для перевода: ");
            double balanceToTransfer = scanner.nextDouble();

            while(currentUserBalance < balanceToTransfer){
                System.out.println("Недостаточно средств!");

                System.out.print("Введите количество денег для перевода: ");
                balanceToTransfer = scanner.nextDouble();
            }

            UserService.changeBalance(currentUser.getUsername(), balanceToTransfer, false);
            UserService.changeBalance(userToTransfer.getUsername(), balanceToTransfer, true);
            LogsDAO.log(currentUser.getUsername(), userToTransfer.getUsername(), balanceToTransfer);
        }
        else {
            System.out.println("Пользователь не найден!");
        }
    }
}
