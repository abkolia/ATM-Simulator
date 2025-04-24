package View;

public class ATMConsoleView implements DisplayMainMenuInterface {

    public void greetingScreen(){
        System.out.println("ATM project v.1");
        System.out.println("1. Для входа 2. Для регистрации. 3. Для закрытия программы.");
    }

    public void displayRegistration(){
        System.out.println("Вы незарегистрированны!");
        System.out.print("Введите логин и PIN: \n");
    }

    @Override
    public void displayMainMenu() {
        System.out.printf("""
                Напиши цифру для выполнения действия:\s
                1. Пополнить/Снять деньги\s
                2. Проверить баланс\s
                3. Сменить PIN\s
                4. Перевод средств\s
                5. Выйти
                """
        );
    }
}
