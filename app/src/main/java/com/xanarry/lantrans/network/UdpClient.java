package com.xanarry.lantrans.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpClient {
    private SearchStateListener updateState;
    private int times;
    private int timeout;
    private int port;
    private String TAG;

    public UdpClient(int timeout, int times, int port) {
        this.timeout = timeout;
        this.times = times;
        this.port = port;
        TAG = UdpClient.class.getName();
    }

    public UdpClient(int port) {
        this.timeout = Configuration.SEARCH_TIMOUT;
        this.times = Configuration.SEARCH_TIMES;
        this.port = port;
        TAG = UdpClient.class.getName();
    }

    public UdpClient() {
        this.timeout = Configuration.SEARCH_TIMOUT;
        this.times = Configuration.SEARCH_TIMES;
        this.port = Configuration.UDP_PORT;
        TAG = UdpClient.class.getName();
    }

    public @Nullable
    InetAddress search() {
        byte[] recvBuf = new byte[Configuration.RESPONSE_DATA.getBytes().length];
        byte[] sendBuf = Configuration.BROADCAST_DATA.getBytes();

        InetAddress address = Utils.getBroadcastAddr();//设置广播地址
        DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
        DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);


        try {
            DatagramSocket clientSocket = new DatagramSocket();//创建一个udpClient
            clientSocket.setBroadcast(true);//广播信息
            clientSocket.setSoTimeout(this.timeout * 1000);//如果2秒后没后得到服务器的回应, 抛出超时异常, 以便重新广播

            Log.e(TAG, "本机ip:" + Utils.getLocalHostLanIP() + " 广播地址:" + address);

            clientSocket.send(sendPacket);//向服务器发送数据包
            clientSocket.receive(recvPacket);
            clientSocket.close();

            Log.e(TAG, "search: receive " + recvPacket.getAddress());
            String msg = new String(recvPacket.getData());
            Log.e(TAG, "search: receive " + msg);
            if (Configuration.RESPONSE_DATA.equals(msg)) {
                Log.e(TAG, "search: match");
                return recvPacket.getAddress();
            }
        } catch (SocketException e3) {
            e3.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
