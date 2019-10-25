import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private ServerSocket server;//puerto y personas permitidas
    private Socket connection;

    public Servidor()  {

    }
    public static void main(String args[]){

    }
    public void runServer(){
        try {
            server = new ServerSocket(6789,100);
            while(true){
                System.out.println("connecting");
                connection = server.accept();
                connection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
