import entity.Identity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame{

    Socket socket;

    BufferedReader reader;
    PrintWriter writer;


    //declare components
    private JLabel heading=new JLabel("ShreyNet");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();

    private Font font=new Font("Roboto",Font.PLAIN,20);


    public Client(String IpAddressOfServer,int portNumberOfServer,Identity myidentity){
        try{
//            sending request to server's 7777 port
            this.socket=new Socket(IpAddressOfServer,portNumberOfServer);


            reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); //this is ins

            writer=new PrintWriter(socket.getOutputStream());  //this is outs

            //send my identity
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(myidentity);

            //get the server identity
            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
            Identity serverIdentity=(Identity) objectInputStream.readObject();
//
              createGUI();
              handleEvents();
              startReading(serverIdentity.getName());
//            startWriting();

        }
        catch (Exception e){

        }
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
        ImageIcon imageIcon=new ImageIcon("Batman-Logo-1946.png");
        Image image=imageIcon.getImage();
        Image scaledImage=image.getScaledInstance(50,30,Image.SCALE_SMOOTH);
        heading.setIcon(new ImageIcon(scaledImage));
        heading.setHorizontalTextPosition(SwingConstants.RIGHT);
        heading.setVerticalTextPosition(SwingConstants.CENTER);

        messageArea.setEditable(false);
        messageArea.setBackground(Color.BLACK);
        messageArea.setForeground(Color.YELLOW);


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

    public void startReading(String serverName){
        Runnable r1=()->{
            //iske under jo bhi code likhoge vo thread execute krega
            //since continously read krna hai toh while true mai ddal diya

                try {
                    while (true) {
                        String message = reader.readLine();
                        if (message.equals("exit")) {
                            System.out.println("Server exited the chat");
                            JOptionPane.showMessageDialog(this,"Server exited the chat");
                            messageInput.setEnabled(false);
                            socket.close();
                            break;
                        }
//                        System.out.println("Server :" + message);
                        messageArea.append(serverName+": "+message+"\n");
                    }
                }
                catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("connection closed");
                }

        };

        new Thread(r1).start();
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

    public void startWriting(){
        Runnable r2=()->{

                try{
                    while (true && !socket.isClosed()){
                    //to read from keyboard
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));  //this is inputc

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

        Scanner sc=new Scanner(System.in);
        System.out.println("What is your name?");
        String myName=sc.next();
        Identity myidentity=new Identity(myName);

        System.out.println("Enter IP address of server");
        String IpAddressOfServer=sc.next();

        System.out.println("Enter port number of server");
        int portNumberOfServer=sc.nextInt();

        //send your identity and port number,ip of server
        new Client(IpAddressOfServer,portNumberOfServer,myidentity);

    }
}
