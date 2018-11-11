package com.xanarry.lantrans.network;

import android.util.Log;

import com.xanarry.lantrans.minterfaces.ProgressListener;
import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpServer {
    private ProgressListener progressListener;
    private int port;
    private String TAG;
    DatagramSocket serverSockent;

    public UdpServer(int port, ProgressListener progressListener) {
        this.port = port;
        this.TAG = UdpServer.class.getName();
        this.progressListener = progressListener;
    }

    public DatagramPacket waitClient() {
        //server wait and receive message broadcasted by client
        DatagramPacket packet = null;
        String broadcastData = "I'm sender, where is the receiver";
        byte[] recvBuf = new byte[broadcastData.getBytes().length];
        try {
            serverSockent = new DatagramSocket(port);//设置服务器端口, 监听广播信息
            serverSockent.setSoTimeout(Configuration.WAITING_TIME * 1000);//等待10秒

            DatagramPacket message = new DatagramPacket(recvBuf, recvBuf.length);
            serverSockent.receive(message);//接收client的广播信息

            String strmsg = new String(message.getData());
            Log.e(TAG, "waitClient: " + strmsg);
            if (broadcastData.equals(strmsg)) {
                message.setData("I'm receiver and I'm here".getBytes());//将服务器的主机名发送给client
                serverSockent.send(message);//回复信息tcp要使用的Tcp端口给client
                Log.e(TAG, "waitClient: send out");
                message.setData(strmsg.getBytes());
                packet = message;
            }
        } catch (SocketTimeoutException e) {
            progressListener.updateProgress(-3, 100, 100, 999);
            Log.e(TAG, "udp server 等待超时");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "编码解释错误" + e.getMessage());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverSockent.close();
        return packet;//将client发送的数据包返回给调用者, 里面包含client的地址, 端口, 主机名
    }

    public void close() {
        serverSockent.close();
    }
}
