package gameClient;

public class myMain {
    public static void main(String[] args) {
        loginMenu login = new loginMenu();
        login.chose();
        while(login.isOn != true) {
            System.out.print("");
        }
        Ex2 start=new Ex2(login.id,login.scenario);
        Thread client = new Thread(start);
        client.start();
    }
}
