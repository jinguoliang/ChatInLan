package com.xanarry.lantrans.network;

import androidx.annotation.Nullable;
import android.util.Log;

import com.xanarry.lantrans.minterfaces.SearchStateListener;
import com.xanarry.lantrans.utils.Configuration;
import com.xanarry.lantrans.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpClient {
    private int timeout;
    private int port;
    private String TAG;

    public UdpClient(int timeout, int times, int port) {
        this.timeout = timeout;
        this.port = port;
        TAG = UdpClient.class.getName();
    }

    public UdpClient(int port) {
        this.timeout = Configuration.SEARCH_TIMOUT;
        this.port = port;
        TAG = UdpClient.class.getName();
    }

    public UdpClient() {
        this.timeout = Configuration.SEARCH_TIMOUT;
        this.port = Configuration.UDP_PORT;
        TAG = UdpClient.class.getName();
    }

    public @Nullable
    List<String> search() {
        byte[] recvBuf = new byte[Configuration.RESPONSE_DATA.getBytes().length];
        byte[] sendBuf = Configuration.BROADCAST_DATA.getBytes();

        InetAddress broadcastAddress = Utils.getBroadcastAddr();//设置广播地址
        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, broadcastAddress, port);
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);

        List<String> addresses = new ArrayList<>();


        try {
            DatagramSocket clientSocket = new DatagramSocket();//创建一个udpClient
            clientSocket.setBroadcast(true);//广播信息
            clientSocket.setSoTimeout(this.timeout * 1000);//如果2秒后没后得到服务器的回应, 抛出超时异常, 以便重新广播

            clientSocket.send(sendPacket);//向服务器发送数据包

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 10000) {
                clientSocket.receive(recvPacket);
                Log.e(TAG, "search: receive " + recvPacket.getAddress());
                String msg = new String(recvPacket.getData());
                Log.e(TAG, "search: receive " + msg);
                if (Configuration.RESPONSE_DATA.equals(msg)) {
                    Log.e(TAG, "search: match");
                    addresses.add(recvPacket.getAddress().getHostAddress());
                }
            }
            clientSocket.close();

        } catch (SocketException e3) {
            e3.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }
}
