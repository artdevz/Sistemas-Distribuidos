package p02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8080);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) System.out.println(message);
            }
            catch (Exception ignored) {}
        }).start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            out.println(input);
        }
    }

}
