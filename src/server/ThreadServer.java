package server;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
/**
 * Thread for Server
 */
public class ThreadServer extends Thread {

    private Socket socket;
    private ArrayList<Socket> clients;
    private HashMap<Socket, String> clientNameList;

    public ThreadServer(Socket socket, ArrayList<Socket> clients, HashMap<Socket, String> clientNameList) {
        this.socket = socket;
        this.clients = clients;
        this.clientNameList = clientNameList;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String line;
//            while ((line = input.readLine()) != null) {
//                BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
//                for (Socket s : clients) {
//                    OutputStream out = s.getOutputStream();
//
//                    PrintWriter writer = new PrintWriter(out, true);
//                    writer.println(line);
//                    ImageIO.write(image, "jpg", out);
//                }
//            }

            while (true) {
                String outputString = input.readLine();
                if (outputString.equals("logout")) {
                    throw new SocketException();
                }
                if (!clientNameList.containsKey(socket)) {
                    String[] messageString = outputString.split(":", 2);
                    clientNameList.put(socket, messageString[0]);
                    System.out.println(messageString[0] + messageString[1]);
                    showMessageToAllClients(socket, messageString[0] + messageString[1]);
                } else {
                    System.out.println(outputString);
                    showMessageToAllClients(socket, outputString);
                }
            }
        } catch (SocketException e) {
            String printMessage = clientNameList.get(socket) + " left the chat room";
            System.out.println(printMessage);
            showMessageToAllClients(socket, printMessage);
            clients.remove(socket);
            clientNameList.remove(socket);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    private void showMessageToAllClients(Socket sender, String outputString) {
        Socket socket;
        PrintWriter printWriter;
        int i = 0;
        while (i < clients.size()) {
            socket = clients.get(i);
            i++;
            try {
                if (socket != sender) {
                    printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println(outputString);
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
}

