import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

class Server {
    private ServerSocket serverSocket;
    private ArrayList<Connection> connections = new ArrayList<>();

    Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Created server bounded to port "+port);
        while(true){
            try{
                createSocket();
            }catch (IOException e){
                e.printStackTrace();//todo add user-friendly exception handling
                break;
            }
        }
    }
    private void createSocket() throws IOException {
        Connection connection = new Connection(serverSocket);
        connections.add(connection);
        System.out.println(connection.socket.getRemoteSocketAddress()+" connected");
        sendAll(connection.socket.getRemoteSocketAddress()+" connected");
    }
    synchronized void sendAll(String message){
        for (int i = 0;i<connections.size();i++) {
            try {
                connections.get(i).send(message);
            } catch (IOException ignored) {
                connections.get(i).disconnect();
            }
        }
    }
    class Connection {
        Socket socket;
        private boolean connected = true;
        private DataOutputStream send;

        Connection(ServerSocket serverSocket) throws IOException {
            socket = serverSocket.accept();
            send = new DataOutputStream(socket.getOutputStream());
            new Thread(new Receive(new DataInputStream(socket.getInputStream()))).start();
        }

        void disconnect() {
            connected = false;
            System.out.println(socket.getRemoteSocketAddress()+" disconnected.");
            connections.remove(this);
            sendAll(socket.getRemoteSocketAddress()+" disconnected.");
        }

        synchronized void send(String message) throws IOException{
            send.writeUTF(message);
        }
        class Receive implements Runnable{
            private DataInputStream receive;
            Receive(DataInputStream receive){
                this.receive = receive;
            }
            @Override
            public void run() {
                while(connected){
                    try {
                        sendAll(String.format("<%s>: %s", socket.getRemoteSocketAddress(), receive()));
                    } catch (EOFException ignored){
                    } catch (SocketException | SocketTimeoutException ignored){
                        disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();//todo add user-friendly exception handling
                        disconnect();
                    }
                }
            }
            String receive() throws IOException {
                return receive.readUTF();
            }
        }
    }
}
