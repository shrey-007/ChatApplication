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
            while (true){
                try{
                    String message=reader.readLine();
                    if(message.equals("exit")){
                        System.out.println("Server exited the chat");
                        break;
                    }
                    System.out.println("Server :"+message);
                }
                catch (Exception e){e.printStackTrace();}
            }
        };

        new Thread(r1).start();
    }

    public void startWriting(){
        Runnable r2=()->{
            while (true){
                try{
                    //to read from keyboard
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));

                    //toh server ke keyboard se jo bhi enter hoga vo yaha aaega.
                    String messageFromKeyboard=br1.readLine();

                    writer.println(messageFromKeyboard);
                    writer.flush();
                }
                catch (Exception e){e.printStackTrace();}
            }

        };

        new Thread(r2).start();
    }
    public static void main(String[] args) {
        new Client();

    }
}
