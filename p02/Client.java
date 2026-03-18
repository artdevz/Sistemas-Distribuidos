package p02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    
    public static void main(String[] args) throws Exception {
        String nickname = "";

        if (args.length == 1) nickname = args[0];
        else return;

        Socket socket = new Socket("localhost", 8080);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("Sala Cheia")) {
                        System.out.println("[Server]: Sala Cheia. Encerrando...");

                        try { socket.close(); } catch (Exception ignored) {}
                        System.exit(0);
                    }

                    System.out.println(message);
                }
            }
            catch (Exception ignored) {}
        }).start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("/sair")) {
                System.out.println("Desconectando...");
                break;
            }
            out.println(input);
        }

        try {
            socket.close();
        }
        catch (Exception ignored) {}

        scanner.close();

        System.out.println("Client encerrado");
    }

}
