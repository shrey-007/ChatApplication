import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    Socket socket;

    BufferedReader reader;
    PrintWriter writer;


    public Client(){
        try{
            //sending request to server's 7777 port
            this.socket=new Socket("127.0.0.1",7777);


            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer=new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

        }
        catch (Exception e){

        }
    }

    public void startReading(){
        Runnable r1=()->{
            //iske under jo bhi code likhoge vo thread execute krega
            //since continously read krna hai toh while true mai ddal diya

                try {
                    while (true) {
                        String message = reader.readLine();
                        if (message.equals("exit")) {
                            System.out.println("Server exited the chat");
                            socket.close();
                            break;
                        }
                        System.out.println("Server :" + message);
                    }
                }
                catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("connection closed");
                }

        };

        new Thread(r1).start();
    }

    public void startWriting(){
        Runnable r2=()->{

                try{
                    while (true && !socket.isClosed()){
                    //to read from keyboard
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));

                    //toh server ke keyboard se jo bhi enter hoga vo yaha aaega.
                    String messageFromKeyboard=br1.readLine();

                    writer.println(messageFromKeyboard);
                    writer.flush();

                    if(messageFromKeyboard.equals("exit")){socket.close();break;}
                   }

                }

                catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("connection closed");
                }


        };

        new Thread(r2).start();
    }
    public static void main(String[] args) {
        new Client();

    }
}
