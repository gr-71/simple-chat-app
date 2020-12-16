package rga.client.server.chat.app.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Client {

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.startChat();
    }

    String username;
    volatile static Set<String> usersNames = new HashSet();

    static JFrame chatWindow = new JFrame("Chat-Client Application");
    static JTextArea chatArea = new JTextArea(25, 40);
    static JTextField textField = new JTextField(40);
    static JLabel blankLabel = new JLabel("                          ");
    static JButton sendBtn = new JButton("SEND");


    static BufferedReader bufferedReader;
    static PrintWriter printWriter;

    static JLabel nameLabel = new JLabel("                             ");
    static JButton closeBtn = new JButton("CLOSE");

    Client(){

        chatWindow.setLayout(new FlowLayout());
        chatWindow.add(nameLabel);
        chatWindow.add(new JScrollPane(chatArea));
        chatWindow.add(blankLabel);
        chatWindow.add(textField);
        chatWindow.add(sendBtn);

        chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatWindow.setSize(500, 550);
        chatWindow.setVisible(true);

        chatWindow.add(closeBtn, BorderLayout.SOUTH);

        chatArea.setEditable(false);
        textField.setEditable(false);

        sendBtn.addActionListener(new Listener(this.username));

        textField.addActionListener(new Listener(this.username));
    }
    void startChat() throws Exception {

        Socket socket = new Socket("localhost", 9806);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printWriter.println("LEFT" + username);
                usersNames.remove(username);
                System.exit(0);
            }
        });
        while (true) {
            String string = bufferedReader.readLine();
            if (string.equals("NAMEREQUIRED")) {
                String name = JOptionPane.showInputDialog(
                        chatWindow,
                        "Enter a unique name:",
                        "Your name is required here.",
                        JOptionPane.PLAIN_MESSAGE);
                printWriter.println(name);
            } else if (string.equals("NAMEALREADYEXISTS")) {
                String name = JOptionPane.showInputDialog(
                        chatWindow,
                        "Please, enter another name:",
                        "This is already existing name!",
                        JOptionPane.WARNING_MESSAGE);
                printWriter.println(name);
            } else if (string.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
                nameLabel.setText("You are logged in as: " + string.substring(12));
                this.username = string.substring(12);
                getActiveUsersList();
            } else if (string.startsWith("USERLIST")) {
                String names_string = string.substring(9);
                String username[] = names_string.split(",");
                System.out.println("inside userlist");
                usersNames.clear();
                for (String text : username) {
                    usersNames.add(text);
                }
            } else if (string.startsWith("USERJOINED")) {
                String connectedUser = string.substring(10);
                chatArea.append(connectedUser + " joined the chat" + "\n");
            } else if (string.startsWith("USERLEFT")) {
                String goneUser = string.substring(8);
                chatArea.append(goneUser + " has already left the chat" + "\n");
            } else if (string.startsWith("USERSLOG")) {
                String names_string = string.substring(8);
                String usernames[] = names_string.split(",");
                usersNames.clear();
                for (String text : usernames) {
                    usersNames.add(text);
                }
                for (String text : usersNames) {
                    if (text.equals(username))
                        continue;
                    chatArea.append(text + " has joined this chat" + "\n");
                }
            } else {
                chatArea.append(string + "\n");
            }
        }
    }

    public static void getActiveUsersList() throws IOException {
        printWriter.println("REQUESTUSERS");
        String stringList = bufferedReader.readLine();
    }
}
