import View.ATMConsoleView;
import View.UserInputHandler;

public class Main {
    public static void main(String[] args) {
        ATMConsoleView view = new ATMConsoleView();
        UserInputHandler uih = new UserInputHandler();
        view.greetingScreen();
        uih.handleGreetingScreen();

        uih.handleUserActions();
    }
}
