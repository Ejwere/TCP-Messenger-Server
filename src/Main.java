import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        while(true) {
            Scanner in = new Scanner(System.in);
            System.out.print("Port to use for server?: ");//todo grab from a config file
            String port = in.nextLine();
            try{
                Server server = new Server(Integer.parseInt(port));
                break;
            }catch (IOException e){
                e.printStackTrace();//todo deal with exceptions in a user-friendly way
            }
        }
    }
}
