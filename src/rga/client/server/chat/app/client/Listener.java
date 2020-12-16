package rga.client.server.chat.app.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Listener implements ActionListener {

    private String username;

    public Listener(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<String> options = new ArrayList<>();
        options.add("One to one");
        options.add("One to many");
        options.add("Broadcast");
        JList list = new JList(options.toArray());

        String optionChosen = (String) JOptionPane.showInputDialog(Client.chatWindow,
                "Please, choose option for sending.",
                "MENU", JOptionPane.PLAIN_MESSAGE, null, options.toArray(), list);

        if (optionChosen.equals("One to one")) {
            String name = JOptionPane.showInputDialog(
                    Client.chatWindow,
                    "Please, enter your name:",
                    "Your name is required here.",
                    JOptionPane.PLAIN_MESSAGE);
            if (Client.usersNames.contains(name)) {
                Client.printWriter.println("Me: " + "One to one:" + name + ":" + Client.textField.getText());
                Client.chatArea.append("Me: " + "One to one:" + name + ":" + Client.textField.getText() + "\n");
            } else {
                JOptionPane.showMessageDialog(Client.chatWindow, "This recipient is not valid or offline now.");
            }
            Client.textField.setText(" ");
        } else if (optionChosen.equals("One to many")) {
            String name = JOptionPane.showInputDialog(
                    Client.chatWindow,
                    "Please, enter recipient names separated by commas:",
                    "Here is your name required!",
                    JOptionPane.PLAIN_MESSAGE);
            String str[] = name.split(",");
            for (String str1 : str) {
                if (Client.usersNames.contains(str1)) {
                    Client.printWriter.println("Me: " + "One to many:" + str1 + ":" + Client.textField.getText());
                    Client.chatArea.append("Me: " + "One to many:" + str1 + ":" + Client.textField.getText() + "\n");
                } else {
                    JOptionPane.showMessageDialog(Client.chatWindow,
                            str1 + " is not valid or offline now.");
                }
            }
            Client.textField.setText("");
        } else if (optionChosen.equals("Broadcast")) {
            Client.printWriter.println("Me: BROADCAST" + ":" + Client.textField.getText());
        }
    }
}