/*
    Created By Ye Yuan 10/2/2017
    CMPE 206 Project Part1
*/
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;


class SlaveData implements Runnable {

    private MasterBot m;
    private Socket sock;

    private String sHostName;
    private String sIP;
    private int sPort;
    private String regDate;
    private PrintWriter pw;

    public SlaveData(MasterBot m, Socket sock) {
        this.m = m;
        this.sock = sock;

        this.sPort = this.sock.getPort();
        this.sHostName = this.sock.getInetAddress().getHostName();
        this.sIP = this.sock.getInetAddress().getHostAddress();


        try {
            this.pw = new PrintWriter(sock.getOutputStream(), true);
        } catch (IOException ex) {
            System.exit(-1);
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date dData = new Date();
        this.regDate = df.format(dData);
    }

    public void connect(String[] cmd) {
        if (!cmd[0].equalsIgnoreCase("connect")) {
            return;
        }

        String send = cmd[0];

        for (int i = 2; i < cmd.length; ++i) {
            send += " " + cmd[i];
        }

        pw.println(send);
    }

    public void disconnect(String[] cmd) {
        if (!cmd[0].equalsIgnoreCase("disconnect")) {
            return;
        }

        String send = cmd[0];

        for (int i = 2; i < cmd.length; ++i) {
            send += " " + cmd[i];
        }

        pw.println(send);
    }


    public String getsHostName() {
        return sHostName;
    }

    public String getsIP() {
        return sIP;
    }


    @Override
    public void run() {
        m.register(this);
    }

    @Override
    public String toString() {
        String outPut = sHostName + "\t" + sIP + "\t" + sPort + "\t" + regDate;
        return outPut;
    }

}

public class MasterBot {
    public static void main(String[] args) {
        if (!args[0].equals("-p") ||args.length != 2) {
            System.out.println("Usage: java MasterBot -p port#");
            System.exit(-1);
        }

        MasterBot m = new MasterBot();

        m.port = Integer.parseInt(args[1]);
        m.sInfo = new LinkedList<>();

        (new Thread(new cmdLI(m))).start();

        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(m.port);
        } catch (IOException ex) {
            System.exit(-1);
        }

        try {
            while (true) {
                new Thread(new SlaveData(m, ssock.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private int port;

    private LinkedList<SlaveData> sInfo;

    public void list() {
        synchronized (sInfo) {

            int size = sInfo.size();

            if (size == 0) {
                System.out.println();
                return;
            }

            for (int i = 0; i < size; ++i) {
                System.out.println(sInfo.get(i).toString());
            }
            System.out.flush();
        }
    }

    public void connect(String[] cmd) {
        if (cmd.length < 2)
            return;

        synchronized (sInfo) {
            int size = sInfo.size();

            if (cmd[1].equalsIgnoreCase("all")) {
                for (int i = 0; i < size; ++i) {
                    sInfo.get(i).connect(cmd);
                }

            } else {
                for (int i = 0; i < size; ++i) {
                    if (cmd[1].equals(sInfo.get(i).getsHostName()) || cmd[1].equals(sInfo.get(i).getsIP()) ) {
                        sInfo.get(i).connect(cmd);
                    }
                }
            }
        }
    }

    public void disconnect(String[] cmd) {
        if (cmd.length < 2)
            return;

        synchronized (sInfo) {
            int size = sInfo.size();

            if (cmd[1].equalsIgnoreCase("all")) {
                for (int i = 0; i < size; ++i) {
                    sInfo.get(i).disconnect(cmd);
                }

            } else {
                for (int i = 0; i < size; ++i) {
                    if (cmd[1].equals(sInfo.get(i).getsHostName()) || cmd[1].equals(sInfo.get(i).getsIP())) {
                        sInfo.get(i).disconnect(cmd);
                    }
                }
            }
        }
    }

    public void register(SlaveData slave) {
        synchronized (sInfo) {
            sInfo.add(slave);
        }
    }

}
//Finish a command line interpreter to excute cmd
class cmdLI implements Runnable {

    MasterBot m;

    public cmdLI(MasterBot m) {
        this.m = m;
    }

    private void parseCmd(String s) {
        if (s == null)
            return;

        String[] cmd = s.split("[ \t]+");

        switch (cmd[0]) {
            case "list":
                m.list();
                break;
            case "connect":
                m.connect(cmd);
                break;
            case "disconnect":
                m.disconnect(cmd);
                break;
            default:
                break;

        }
    }

    @Override
    public void run() {
        while (true) {

            System.out.print("> ");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String s = "";

            try {
                s = br.readLine();
            } catch (IOException ex) {
                System.exit(-1);
            }
            if (s != null && s.length() > 2) {
                s.trim();
                parseCmd(s);
            }
        }
    }
}