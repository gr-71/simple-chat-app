package rga.client.server.chat.app.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

    static JTextArea serverLog;
    static JFrame serverWindow;
    static JScrollPane scrollPane;
    static JButton onlineBtn = new JButton("Show online users");
    static JButton offlineBtn = new JButton("Show offline users");
    static ArrayList<String> usersNames = new ArrayList<String>();
    static ArrayList<String> offlineUsersNames = new ArrayList<String>();

    static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
    static Map<String, PrintWriter> nameWriter = new HashMap();

    public static void main(String[] args) throws Exception {

        JFrame serverWindow = null;
        serverWindow = new JFrame("Chat-Server");
        serverWindow.setSize(700, 450);
        serverWindow.setLayout(new FlowLayout());
        serverWindow.setResizable(false);
        serverLog = new JTextArea(25, 60);

        serverLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(serverLog);
        serverWindow.add(scrollPane);
        serverWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        serverWindow.setVisible(true);
        serverWindow.add(onlineBtn);
        serverWindow.add(offlineBtn);

        System.out.println("Waiting for new clients...");

        ServerSocket serverSocket = new ServerSocket(9806);
        serverLog.append("Server has started on port 9806" + "\n\n");
        onlineBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "Online users: ";
                for (String users : usersNames) {
                    message += "\n" + users;
                }
                JOptionPane.showMessageDialog(Server.serverWindow, message);
            }
        });
        offlineBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = "Offline users: ";
                for (String users : offlineUsersNames) {
                    message += "\n" + users;
                }
                JOptionPane.showMessageDialog(Server.serverWindow, message);
            }
        });
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connection has been successfully set.");
            ConversationHandler conversationHandler = new ConversationHandler(socket);
            conversationHandler.start();
        }
    }
}
