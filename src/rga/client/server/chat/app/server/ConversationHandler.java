package rga.client.server.chat.app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConversationHandler extends Thread {

    Socket socket;

    BufferedReader bufferedReader;

    PrintWriter printWriter;

    String name;

    public ConversationHandler(Socket socket) throws IOException {
        this.socket = socket;
    }

    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            int count = 0;
            while (true) {
                if (count > 0) {
                    printWriter.println("NAMEALREADYEXISTS");
                } else {
                    printWriter.println("NAMEREQUIRED");
                }
                name = bufferedReader.readLine();
                if (name == null) {
                    return;
                }
                if (!Server.usersNames.contains(name)) {
                    Server.usersNames.add(name);
                    Server.offlineUsersNames.remove(name);
                    break;
                }
                count++;
            }
            printWriter.println("NAMEACCEPTED" + name);
            Server.serverLog.append(name + " has joined the chat" + "\n");
            Server.printWriters.add(printWriter);
            Server.nameWriter.put(name, printWriter);
            String activeNamesOne = " ";
            for (String nameOne : Server.usersNames) {
                activeNamesOne += nameOne + ",";
            }
            for (PrintWriter printWriter : Server.printWriters) {
                printWriter.println("USERLIST " + activeNamesOne);
            }
            for (PrintWriter printWriter : Server.printWriters) {
                printWriter.println("USERJOINED" + name);
            }

            while (true) {
                String message = bufferedReader.readLine();
                if (message == null) {
                    return;
                } else if (message.startsWith("LEFT")) {
                    Server.usersNames.remove(message.substring(4));
                    Server.offlineUsersNames.add(name);
                    Server.nameWriter.remove(message.substring(4));
                    String activeNamesTwo = " ";
                    for (String nameOne : Server.usersNames) {
                        activeNamesTwo += nameOne + ",";
                    }
                    for (PrintWriter printWriter : Server.printWriters) {
                        printWriter.println("USERLIST " + activeNamesTwo);
                    }
                    for (PrintWriter printWriter : Server.printWriters) {
                        printWriter.println("USERLEFT" + name);
                    }
                    message = message.substring(4) + " left";
                    Server.serverLog.append(name + " has left the chat" + "\n");
                    break;
                } else if (message.startsWith("REQUEST")) {

                    String activeNamesTwo = "";
                    for (String nameOne : Server.usersNames) {
                        activeNamesTwo += nameOne + ",";
                    }
                    PrintWriter printWriterTwo = Server.nameWriter.get(name);
                    printWriterTwo.println("USERSLOG" + activeNamesTwo);
                } else {
                    String str[] = message.split(":");
                    if (str[1].equals("One to one")) {
                        PrintWriter printWriter = Server.nameWriter.get(str[2]);
                        if (printWriter != null) {
                            printWriter.println("One to one:" + name + ": " + str[3]);
                        }
                    } else if (str[1].equals("One to many")) {
                        PrintWriter printWriter = Server.nameWriter.get(str[2]);
                        if (printWriter != null) {
                            printWriter.println("One to many:" + name + ": " + str[3]);
                        }
                    } else if (str[1].equals("Broadcast")) {
                        for (PrintWriter printWriter : Server.printWriters) {
                            printWriter.println(name + " to all: " + str[2]);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}