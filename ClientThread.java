import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Created by krishna and Prithvi on 11/24/16.
 */
public class ClientThread extends Thread
{
    private  String SEPERATOR="--";
    private Socket clientSocket;
    private String  sessionToken="";
    private String gameToken="";
    private String uname="";
    String password="";
    boolean isLeader=false;
    static int numOfsuggestions=0;
    static ArrayList<String> suggestions=new ArrayList<>(5);
    int numOfQuestions=new FoilMaker().questions.size();
    int k=0;//counter for questions
    ClientThread(Socket clientSocket)  {
        this.clientSocket=clientSocket;
    }
    public void run()
    {
        //  System.out.println("i reached this point");
        //  System.out.println("t= "+this.getThreadGroup());
        InputStreamReader isr= null;
        while (true)
        {
            String clientMessage="";
            try {
                isr = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader in=new BufferedReader(isr);
                clientMessage=in.readLine();
                System.out.println("cm= "+clientMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (clientMessage.matches("(.*)CREATENEWUSER(.*)"))
                registeraNewUser(clientMessage);
            if (clientMessage.matches("(.*)LOGIN(.*)"))
                loginNewUser(clientMessage);
            if (clientMessage.matches("(.*)STARTNEWGAME(.*)"))
                startNewGame(clientMessage);
            if (clientMessage.matches("(.*)JOINGAME(.*)"))
                joinGame(clientMessage);
            if(clientMessage.matches("(.*)ALLPARTICIPANTSHAVEJOINED(.*)"))
                participantsJoin(clientMessage);
            if(clientMessage.matches("(.*)PLAYERSUGGESTION(.*)"))
                collectPlayerSuggestions(clientMessage);
            if(clientMessage.matches("(.*)PLAYERCHOICE(.*)"));
            playerChoice(clientMessage);
     /*  if(clientMessage.indexOf("LOGOUT")!=-1)
           registeraNewUser(clientMessage);*/
        }
    }




    public void registeraNewUser(String clientMessage) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);


            uname = clientMessage.substring(clientMessage.indexOf("-") + 2, clientMessage.lastIndexOf("-") - 1);
            password = clientMessage.substring(clientMessage.lastIndexOf("-") + 2);
            if (isUsername(uname) == false)
                out.println("RESPONSE--CREATENEWUSER--INVALIDUSERNAME--");
            else if (isPassword(password) == false)
                out.println("RESPONSE--CREATENEWUSER--INVALIDPASSWORD--");
            else if (userAlreadyExists(uname) == false) {
                FoilMaker f=new FoilMaker();
                for(String name: f.uname)
                {
                    System.out.println("uname is="+uname);
                    System.out.println("name is ="+name);
                    //   if(name.equals(this.uname))
                    out.println("RESPONSE--CREATENEWUSER--USERALREADYEXISTS--");
                }


            }
            // else if(isInvalidRegisterformat(clientMessage)==false)
            //   out.println("RESPONSE--CREATENEWUSER--INVALIDMESSAGEFORMAT--");
            else {
                out.println("RESPONSE--CREATENEWUSER--SUCCESS--");
                FoilMaker.uname.add(uname);
                FoilMaker.hmap.put(uname,new User(uname,password,0,0,0));
            }
            //     System.out.println("hi");
            //isInvalidformat(clientMessage);




        }catch (Exception e)
        {
            System.out.println(e.getMessage());
        }




    }




    public void startNewGame(String clientMessage)
    {
        String recievedToken="";
        try {
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
            recievedToken=clientMessage.substring(clientMessage.lastIndexOf("-")+1);
            if(recievedToken.equals(sessionToken)==false)
                out.println("RESPONSE--STARTNEWGAME--USERNOTLOGGEDIN");
            else {
                gameTokenGenerator();
                out.println("RESPONSE--STARTNEWGAME--SUCCESS--"+gameToken);
                System.out.println("game token ="+gameToken);
                isLeader=true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    void loginNewUser(String clientMessage)
    {
        try {
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
            sessionTokenGenerator();
            uname = clientMessage.substring(clientMessage.indexOf("-") + 2, clientMessage.lastIndexOf("-") - 1);
            password = clientMessage.substring(clientMessage.lastIndexOf("-") + 2);
            System.out.println("uname is="+uname);
            System.out.println("sessiontoken="+sessionToken);
            out.println("RESPONSE--LOGIN--SUCCESS--"+sessionToken);
            FoilMaker.uname.add(uname);
            FoilMaker.hmap.put(uname,new User(uname,password,0,0,0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void joinGame(String clientMessage)
    {
        try {
            PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
            String[] msg = clientMessage.split(SEPERATOR);
            //   System.out.println("msg[2]= "+msg[2]);
            System.out.println("gametoken in joingame = "+gameToken);
            System.out.println("sessiontoken in joingame= "+sessionToken);
            if (msg[1].equals(sessionToken) == false)
                out.println("RESPONSE--JOINGAME--USERNOTLOGGEDIN--"+msg[2]);
                //     else if (msg[2].equals(gameToken) == false)
                //      out.println("RESPONSE--JOINGAME--GAMEKEYNOTFOUND--"+gameToken);
            else
            {
                out.println("RESPONSE--JOINGAME--SUCCESS--"+msg[2]);
              /* for(Thread t:FoilMaker.threads)
               {
                   System.out.println("joingame threads  "+t.toString());
               }*/
                for(int i=0;i<FoilMaker.threads.size();i++)
                {
                    ClientThread t=FoilMaker.threads.get(i);
                    System.out.println("joingametoken= "+t.gameToken);
                    System.out.println("sessiontoken= "+t.sessionToken);
                    System.out.println("user2= "+this.uname);
                    if(t.isLeaderMethod()) {
                        out=new PrintWriter(t.clientSocket.getOutputStream(),true);
                        System.out.println("thread is for "+t.uname+"leader= "+t.isLeader);
                        out.println("NEWPARTICIPANT" + SEPERATOR + this.uname + SEPERATOR + 0);
                    }


                }
            }
        }catch (Exception e)
        {


        }






    }
    private void participantsJoin(String clientMessage) {


        String[] msg = clientMessage.split(SEPERATOR);
        ClientThread t=null;
        FoilMaker f=new FoilMaker();
        for(int i=0;i<FoilMaker.threads.size();i++)
        {
            t=FoilMaker.threads.get(i);
            try {
                PrintWriter out=new PrintWriter(t.clientSocket.getOutputStream(),true);
                out.println("NEWGAMEWORD"+SEPERATOR+f.questions.get(k)+SEPERATOR+f.questionsAndAnswers.get(f.questions.get(k)));
                if(k==numOfQuestions+1)
                    out.println("GAMEOVER--");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        k++;


    }
    public void collectPlayerSuggestions(String clientMessage)
    {
        String[] msg = clientMessage.split(SEPERATOR);
        if(true) {
            numOfsuggestions++;
            suggestions.add(msg[3]);
        }
        if(numOfsuggestions==2)
            sendRoundOptions();
    /* numOfsuggestions++;
       if(numOfsuggestions==2)
           sendRoundOptions();*/
    }


    public void sendRoundOptions() {
        ClientThread t = null;
        String temp = "";//to store all suggestions in format and single string
    /*   for(int i=0;i<FoilMaker.threads.size();i++)
       {
           t=FoilMaker.threads.get(i);
           try {
               PrintWriter out=new PrintWriter(t.clientSocket.getOutputStream(),true);
               for(int j=0;j<suggestions.size();j++)
                   temp+=suggestions+SEPERATOR;
               System.out.println("temp= "+temp);
               out.println("ROUNDOPTIONS"+SEPERATOR+)
           } catch (IOException e) {
               e.printStackTrace();
           }
       }*/
        System.out.println("answer= "+FoilMaker.questionsAndAnswers.get(FoilMaker.questions.get(k-1)));
        suggestions.add(FoilMaker.questionsAndAnswers.get(FoilMaker.questions.get(k-1)));
        Collections.shuffle(suggestions);
        for (int i = 0; i < FoilMaker.threads.size(); i++) {
            t = FoilMaker.threads.get(i);
            try {
                PrintWriter out = new PrintWriter(t.clientSocket.getOutputStream(), true);
                out.println("ROUNDOPTIONS" + SEPERATOR +suggestions.get(0)+SEPERATOR+suggestions.get(1)+SEPERATOR+suggestions.get(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void playerChoice(String clientMessage)
    {
        ClientThread t = null;
        for(int i=0;i<FoilMaker.threads.size();i++) {
            t = FoilMaker.threads.get(i);
        }
    }


    public  void sessionTokenGenerator()
    {
        String listOfCharacters="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY1234567890/*!";
        int length=listOfCharacters.length();
        Random r=new Random();
        char[] text=new char[length];
        for(int i=0;i<10;i++)
        {
            text[i]=listOfCharacters.charAt(r.nextInt(length));
        }
        for(int i=0;i<length;i++)
            sessionToken+=text[i];
        // System.out.println("token= "+sessionToken);
        // System.out.println(text[0]);
    }
    public  void gameTokenGenerator()
    {
        String listOfCharacters="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY1234567890/*!";
        int length=listOfCharacters.length();
        Random r=new Random();
        char[] text=new char[length];
        for(int i=0;i<3;i++)
        {
            text[i]=listOfCharacters.charAt(r.nextInt(length));
        }
        for(int i=0;i<length;i++)
            gameToken+=text[i];
        // System.out.println("token= "+sessionToken);
        // System.out.println(text[0]);


    }
    boolean isUsername(String uname)
    {
        return true;
    }
    boolean isPassword(String password)
    {
        return true;
    }
    boolean isInvalidRegisterformat(String clientMessage) {
        String[] msg=clientMessage.split(SEPERATOR);
        for (int i=0;i<msg.length;i++)
            System.out.println("m= "+ msg[i]);
        if(msg[0].equals("CREATENEWUSER")==false)
            return false;
        if(clientMessage.substring(clientMessage.indexOf(msg[0]),clientMessage.indexOf(msg[1])).equals(SEPERATOR)==false)
            return false;
        if(clientMessage.substring(clientMessage.indexOf(uname),clientMessage.indexOf(password)).equals(SEPERATOR)==false)
            return false;


        return true;
    }

    public boolean isValidUsername(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c) && c != '_')
                return false;
        }
        return true;
    }

    public boolean isValidPassword(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c) && c != '#' && c != '&' && c != '$' && c != '*')
                return false;
        }
        return true;
    }


    boolean userAlreadyExists(String username) {
        for(int i=0;i<FoilMaker.uname.size();i++)
        {
            if(FoilMaker.uname.get(i).equals(username)) {
                //   System.out.println("f uname: "+FoilMaker.uname);
                //   System.out.println("uname: "+uname);
                return false;
            }
        }
        return true;
    }
    public boolean isLeaderMethod()
    {
        if(isLeader==true)
            return true;
        return false;
    }
}
