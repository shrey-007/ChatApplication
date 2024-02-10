import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Server extends JFrame {
    ServerSocket serverSocket;
    Socket socket;

    //these are not streams.
    //these are used to read and write data into streams
    BufferedReader reader;
    PrintWriter writer;

    //declare components
    private JLabel heading=new JLabel("Disha Patani");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();

    private Font font=new Font("Roboto",Font.PLAIN,20);




    //Constructor
    public Server(){
        try {
            //server se baat krne ke liye 7777 hi use krna padega ab.Toh client agar request bheje toh 7777 pr bheje
            this.serverSocket=new ServerSocket(7777);
            System.out.println("Waiting for client");

            //ab jaise hi client request bhejega toh ye accept method usko accept krega and us client ka socket ka r
            //refernce variable return krega. is socket variable se apan client ko access kr skte hai.
            this.socket=serverSocket.accept();


            //socket se input stream nikaali, and use inputStreamReader ko de diya, toh jo bytes mai data aaega usko
            //inputStreamReader character mai change kr dega,fir uska buffer banega
            reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //jaa streams are unidirectional means ki ek hi stream se i/o nhi hota alag se input and output stream bananai pdti hai
            writer=new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
        }
        catch (Exception e){e.printStackTrace();}
    }

    private void createGUI(){
        this.setTitle("ShreyNet");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
//      heading.setIcon(new ImageIcon("mmvy_status.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.CENTER);

        messageArea.setEditable(false);


        //layout of frame
        this.setLayout(new BorderLayout());

        //adding components to frame
        //north=top, center=center,south=bottom
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);




        this.setVisible(true);

    }

    //startReading se hume continously read krna hai and startWriting se hume continuosly write krna hai means dono kaam
    // saath mai krne hai toh , we have to use multithreading

    public void startReading(){
        Runnable r1=()->{
            //iske under jo bhi code likhoge vo thread execute krega
            //since continously read krna hai toh while true mai ddal diya
            try{
                while (true){

                    String message=reader.readLine();
                    if(message.equals("exit")){
                        System.out.println("Client exited the chat");
                        JOptionPane.showMessageDialog(this,"Server exited the chat");
                        messageInput.setEnabled(false);
                        //close the connection
                        socket.close();
                        break;
                    }
//                    System.out.println("Client :"+message);
                    messageArea.append("Server: "+message+"\n");


                }

            }
            catch (Exception e){
//                e.printStackTrace();
                System.out.println("connection is closed");
            }

        };

        new Thread(r1).start();
    }

    public void startWriting(){
        Runnable r2=()->{
            try {
                while (true && !socket.isClosed()){

                    //to read from keyboard
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));

                    //toh server ke keyboard se jo bhi enter hoga vo yaha aaega.
                    String messageFromKeyboard=br1.readLine();

                    writer.println(messageFromKeyboard);
                    writer.flush();

                    if(messageFromKeyboard.equals("exit")){socket.close();break; }


                }
            }
            catch (Exception e){
//                e.printStackTrace();
                System.out.println("connection is closed");
            }
        };

        new Thread(r2).start();
    }

    private void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                //enter ka key code 10 hota hai toh enter dabate se hi ye chal jaaega
                if(e.getKeyCode()==10){
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me :"+contentToSend+"\n");
                    writer.println(contentToSend);
                    writer.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();

                }

            }
        });
    }


    public static void main(String[] args) {
        System.out.println("Server is starting.....");
        new Server();
    }

}
