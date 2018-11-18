package com.xanarry.lantrans.network;

/**
 * Created by xanarry on 2016/5/22.
 */

import android.util.Log;

import com.xanarry.lantrans.minterfaces.ProgressListener;
import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;


public class TcpClient {
    private static final String TAG = "TcpClient";
    private InetAddress serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private ProgressListener progressListener;

    public TcpClient(HostAddress address, ProgressListener progressListener) {
        this.serverAddress = address.getAddress();
        this.serverPort = address.getPort();
        this.progressListener = progressListener;
    }

    public TcpClient(InetAddress address, int port, ProgressListener progressListener) {
        this.serverAddress = address;
        this.serverPort = port;
        this.progressListener = progressListener;
    }

    public void close() {
        try {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectReceiver() {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            Log.e(TAG, "connectReceiver: socket created");
            bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
            Log.e(TAG, "connectReceiver: socket getInputStream");

            bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            Log.e(TAG, "connectReceiver: socket getOutputStream");

        } catch (IOException e) {
            Log.e(TAG, "connectReceiver: ", e);
        }
    }

    public int sendFile(ArrayList<File> files) {
        int filePosition = 0;
        for (; filePosition < files.size(); filePosition++) {
            long hasSend = 0;
            long lastimeSend = 0;
            long startTime = 0;
            long endTime = 0;
            double speed = 0L;
            int actualRead = 0;

            File file = files.get(filePosition);

            byte[] fileBuf = new byte[Configuration.FILE_IO_BUF_LEN];
            byte[] ackBuf = new byte[Configuration.STRING_BUF_LEN];

            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);//打开文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                String curFileInfo = file.getName() + Configuration.FILE_LEN_SPT + file.length() + Configuration.DELIMITER;

                //发送当前文件描述
                Log.e(TAG, "start send:" + curFileInfo);
                bufferedOutputStream.write(curFileInfo.getBytes());//>>>>>>>>>>>>>>>>>>>>>>>>>
                bufferedOutputStream.flush();

                ackBuf = new byte[Configuration.STRING_BUF_LEN];
                //接收确认
                bufferedInputStream.read(ackBuf);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                Log.e(TAG, "reciver ack" + Utils.getMessage(ackBuf));

                startTime = System.nanoTime();
                if (file.length() == 0) {
                    progressListener.updateProgress(filePosition, 100, 100, 888);
                    continue;
                }

                //读取文件并发送文件
                while ((actualRead = fileInputStream.read(fileBuf, 0, Configuration.FILE_IO_BUF_LEN)) > 0) {//读本地文件
                    // send to server
                    bufferedOutputStream.write(fileBuf, 0, actualRead);//写入到网络
                    endTime = System.nanoTime();

                    // accumulate total size
                    hasSend += actualRead;
                    long diffTime = endTime - startTime;
                    //计算传输速度
                    if (diffTime >= 500000000) {//0.5秒更新一次
                        long diffSize = hasSend - lastimeSend;
                        speed = ((double) diffSize / (double) diffTime) * (1000000000.0 / 1024.0);
                        lastimeSend = hasSend;
                        startTime = endTime;
                    }
                    progressListener.updateProgress(filePosition, hasSend, file.length(), new Double(speed).intValue());

                    if (hasSend == file.length()) {//传输完毕
                        bufferedOutputStream.flush();
                        break;
                    }
                }

                ackBuf = new byte[Configuration.STRING_BUF_LEN];
                //接收接收文件大小确认
                bufferedInputStream.read(ackBuf);
                String ackSize = Utils.getMessage(ackBuf);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return filePosition;
            }
        }
        return filePosition;
    }

    public void send(@NotNull String msg) {
        if (bufferedOutputStream == null) {
            return;
        }

        try {
            bufferedOutputStream.write(msg.getBytes());
            bufferedOutputStream.flush();
            Log.e(TAG, "send: writed");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void sendFile(String path) {
        if (bufferedOutputStream == null) {
            return;
        }

        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
            BufferedOutputStream out = bufferedOutputStream;

            byte[] buf = new byte[1024];
            int n = in.read(buf);
            while (n > 0) {
                Log.e(TAG, "sendFile: " + n);
                out.write(buf, 0, n);
                out.flush();
                n = in.read(buf);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
