
// ViewPerformance lets user test link bandwidth vs performance in number of viewers
// Usage: java ViewPerformance <linkName> <minBandwidth> <maxBandwidth> <delta> <file>
//        <viewServer> <viewPort> <playerName> [Auto <length> <interval>]
//   for (i = <minBandwidth>; i <= <maxBandwidth>; i += <delta>) {
//      get the max number Mi of viewers watching a game without delay warning}
//   write all i, Mi to <file>
// Example 1: java ViewPerformance eth1 20 50 10 "viewPerf.txt" "10.10.172.130" 58001 Alex
//  for link bandwidth from 20kbps to 50kbps with increment 10kbps
//    get max number of viewers for each bandwidth to viewPerf.txt
// Example 2: java ViewPerformance eth1 10 100 10 "viewPerf.txt" "10.10.172.130" 58001 Alex Auto 30 5000
//  for link bandwidth from 10kbps to 100kbps with increment 10kbps
//    get max number of viewers for each bandwidth to viewPerf.txt 
//    where each viewer automatically generates a comment of 30 bytes every 5000 ms


import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.channels.FileLock;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class ViewPerformance {

    public static final String WARNING_FILE = "viewerWarning.txt";
    public static final String DELAY_UNACCEPTABLE_WARNING = "????? BROADCAST QUALITY NOT ACCEPTABLE ?????";

    public static void main(String[] args) {
        String linkName = null;
        int minBandwidth = 0;
        int maxBandwidth = 0;
        int delta = 0;
        String perfFileName = null;
        String viewServerHost = null;
        String viewPort = null;
        String playerName = null;
        String autoStr = null;
        String lengthStr = null;
        String intervalStr = null;
        int[][] perfArray;

        if (args.length >= 8) {
            linkName = args[0];
            try {
                minBandwidth = Integer.parseInt(args[1]);
                maxBandwidth = Integer.parseInt(args[2]);
                delta = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println("Arguments " + args[1] + " " + args[2] + " " + args[3] + " must be an unsigned number.");
                System.exit(1);
            }
            perfFileName = args[4];
            viewServerHost = args[5];
            viewPort = args[6];
            playerName = args[7];

            if (args.length >= 11) {
                autoStr = args[8];
                lengthStr = args[9];
                intervalStr = args[10];
            }
        }
        else {
            System.err.println("Usage: java java ViewPerformance <linkName> <minBandwidth> <maxBandwidth> <delta> <file>");
            System.exit(1);
        }

        int n = (maxBandwidth - minBandwidth) / delta + 1;
        perfArray = new int[n][2];
        String cmdChangeBw = null;
        int j = 0;
        for (int i = minBandwidth; i <= maxBandwidth; i += delta) {
            // commands to change link bandwidth 
            // sudo tc qdisc replace dev eth1 root netem rate 10kbit
            cmdChangeBw = "sudo tc qdisc replace dev " + linkName + " root netem rate " + i + "kbit";
            System.out.println("cmdChangeBw: " + cmdChangeBw);
            try {
                Process proc = Runtime.getRuntime().exec(cmdChangeBw);
                proc.waitFor();

                perfArray[j][0] = i;
                int start = 0;
                if (j > 0) {
                    start = perfArray[j - 1][1] + 1;
                }
                perfArray[j][1] = maxViewers(viewServerHost, viewPort, playerName, autoStr, lengthStr, intervalStr, start);
                j++;
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }

        writePerfToFile(perfArray, perfFileName);
    }

    public static int maxViewers(String viewServer, String port, String player, String auto, String len, String interval, int start) {
        int numberOfViewers = start;
        writeWarningToFile(WARNING_FILE, "Clean \n");
        //writeWarningToFileWithLock(WARNING_FILE, "Clean \n", 0);
        if (start > 0) {
            try {
                Thread.sleep(10000); // sleep 10 s
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
        while (!hasDelayWarning()) {
            // Start a ViewClient
            String cmdRunViewClient = "java ViewClient " + viewServer + " " +
                    port + " " + "Viewer-" + (numberOfViewers + 1) + " " + player;
            if (auto != null && auto.equals("Auto")) {
                cmdRunViewClient += " Auto " + len + " " + interval;
            }
            System.out.println("cmdRunViewClient: " + cmdRunViewClient);

            try {
                Process proc = Runtime.getRuntime().exec(cmdRunViewClient);
                // proc.waitFor();
                int ran = (int) Math.floor(Math.random() * 5000 + 1);
                Thread.sleep(5000 + ran); // sleep 10 s
                numberOfViewers++;
            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        } // while 

        return numberOfViewers - 1;
    } // maxViewers

    public static boolean hasDelayWarning() {
        try {
            StringBuilder builder = new StringBuilder(WARNING_FILE);
            BufferedReader buffer = new BufferedReader(new FileReader(WARNING_FILE));
            if (buffer != null) {
                String str = buffer.readLine();
                System.out.println("hasDelayWarning(): str = " + str);
                if (str.startsWith(DELAY_UNACCEPTABLE_WARNING)) {
                    return true;
                }
                return false;
            }
            System.out.println("hasDelayWarning(): buffer == null");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("hasDelayWarning() after try");

        return true;
    }

    public static boolean hasDelayWarningWithLock() {
        boolean hasDelayWarning = false;
        File warningFile = new File(WARNING_FILE);
        try {
            RandomAccessFile filerw = new RandomAccessFile(warningFile, "rw");
            FileChannel fileChannel = filerw.getChannel();
            FileLock lock = fileChannel.lock();

            ByteBuffer byteBuffer = ByteBuffer.allocate(512);
            Charset charset = Charset.forName("US-ASCII");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();


            while (fileChannel.read(byteBuffer) > 0) {
                byteBuffer.rewind();
                System.out.print(charset.decode(byteBuffer));

                bos.write(byteBuffer.array());
                byte[] bys = bos.toByteArray();

                String str = new String(bys);

                System.out.println("hasDelayWarning(): str = " + str);
                if (str.startsWith(DELAY_UNACCEPTABLE_WARNING)) {
                    hasDelayWarning = true;
                }
                hasDelayWarning = false;
                byteBuffer.flip();
                break;
            }

            fileChannel.close();
            filerw.close();

            return hasDelayWarning;

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("hasDelayWarning() after try");
        return true;
    }

    public static void writeWarningToFile(String fileName, String data) {
        File warningFile = new File(fileName);
        FileWriter outputToFile = null;
        try {
            if (warningFile != null) {
                outputToFile = new FileWriter(warningFile);
                outputToFile.write(data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close resources
            try {
                outputToFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public static void writeWarningToFileWithLock(String fileName, String data, int count) {
        File warningFile = new File(fileName);
        try {
            RandomAccessFile filerw = new RandomAccessFile(warningFile, "rw");
            FileLock lock = filerw.getChannel().lock();

            String warningLine = DELAY_UNACCEPTABLE_WARNING + " " + count + "(times)\n";
            filerw.seek(0);
            filerw.write(data.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writePerfToFile(int[][] perfArray, String fileName) {
        File perfFile = new File(fileName);
        FileWriter outputToFile = null;
        try {
            if (perfFile != null) {
                outputToFile = new FileWriter(perfFile);

                for (int i = 0; i < perfArray.length; i++) {
                    outputToFile.write(perfArray[i][0] + ", " + perfArray[i][1] + ";\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //close resources
            try {
                outputToFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

}
