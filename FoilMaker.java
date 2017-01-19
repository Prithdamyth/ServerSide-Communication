import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by krishna, and Prithvi on 11/24/16.
 */
public class FoilMaker {
    static int portNumber =43000;
    static ArrayList<String>  uname=new ArrayList<>(5);
    // static ArrayList<String>  password=ne
    static HashMap<String,User> hmap=new HashMap<String,User>();
    static ArrayList<ClientThread> threads=new ArrayList<>(5);
    static HashMap<String,String> questionsAndAnswers=new HashMap<>(5);
    static ArrayList<String> questions=new ArrayList<>(5);
    static int threadCounter=0;


    FoilMaker() {
        //  this.portNumber=portNumber;
      /* ObjectInputStream ois;
       ObjectOutputStream oos;
       try {
           ServerSocket serverSocket = new ServerSocket(portNumber);
           System.out.println("Server Waiting for connection");
           Socket socket = serverSocket.accept();
           System.out.println("Connection is succesful and waiting for commands");
           oos = new ObjectOutputStream(socket.getOutputStream());
           ois = new ObjectInputStream(socket.getInputStream());
           //   s=(String)ois.readObject();


       } catch (IOException e) {
           e.printStackTrace();
       }*/
    }
    public static void main(String[] args) {
        try
        {
            int i=0;
            String password="";
            ServerSocket serverSocket=new ServerSocket(portNumber);
            System.out.println("p= "+portNumber);
            if (args.length != 1) {
                System.err.println("Usage: java FoilMaker [portNumber]");
                return;
            }
            String temp = "";
            File f = new File("/Users/neelu/IdeaProjects/Project 4/src/UserDatabase");
            FileReader fr = null;
            fr = new FileReader(f);


            BufferedReader in = new BufferedReader(fr);
            while (true) {
                temp = in.readLine();
                if (temp == null)
                    break;
                uname.add(temp.substring(0, temp.indexOf(":")));
                password=temp.substring(temp.indexOf(":"),temp.indexOf(":",temp.indexOf(":")+1));
                System.out.println("pass "+password);
                hmap.put(uname.get(i),new User(uname.get(i),password,0,0,0));
                updateUserFile();
                System.out.println("line =" + temp);
                i++;
            }
            File f2=new File("/Users/neelu/IdeaProjects/Project 4/src/WordleDeck");//to access question database
            FileReader fr2=new FileReader(f2);
            BufferedReader in2=new BufferedReader(fr2);
            while (true)
            {
                temp=in2.readLine();
                if(temp==null)
                    break;
                questions.add(temp.substring(0,temp.indexOf(":")));
                questionsAndAnswers.put(temp.substring(0,temp.indexOf(":")),temp.substring(temp.indexOf(":")+1));
                System.out.println("questions are:"+questions.toString());
            }


            // for (String s:uname)
            //   System.out.println("uname = "+uname);
            for ( i = 0; i < uname.size(); i++)
                System.out.println("uname= " + uname.get(i));


            while (true)
            {
                System.out.println("yo");
                Socket socket=serverSocket.accept();
                //  System.out.println("hey = "+socket.toString());
                threads.add(new ClientThread(socket));
                //   System.out.println("gg"+threads.get(threadCounter).toString());
                threads.get(threadCounter++).start();
            }


            //  FoilMaker try1 = new FoilMaker(Integer.parseInt(args[0]));


        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static void updateUserFile(){
        BufferedWriter bw = null;
        try{
            File f = new File("/Users/neelu/IdeaProjects/Project 4/src/UserDatabase");
            bw = new BufferedWriter(new FileWriter(f));
            for(User u: hmap.values()){
                bw.write(u.toString() + "\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(bw != null){
                try{
                    bw.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    void newClient()
    {


    }
}