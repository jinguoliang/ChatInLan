package com.xanarry.lantrans.network;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static com.xanarry.lantrans.utils.Configuration.BROADCAST_DATA;
import static com.xanarry.lantrans.utils.Configuration.RESPONSE_DATA;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpServer {
    public static final String TAG = UdpServer.class.getName();;

    private int mPort;
    private DatagramSocket socket;

    public UdpServer(int port) {
        this.mPort = port;
    }

    public void waitClient() {
        try {
            socket = new DatagramSocket(mPort);//设置服务器端口, 监听广播信息

            byte[] buf = new byte[BROADCAST_DATA.getBytes().length];
            DatagramPacket message = new DatagramPacket(buf, buf.length);
            while (true) {
                message.setData(buf);
                socket.receive(message);

                String msg = new String(message.getData());

                Log.e(TAG, "waitClient: " + msg);
                if (BROADCAST_DATA.equals(msg)) {
                    message.setData(RESPONSE_DATA.getBytes());
                    socket.send(message);
                    Log.e(TAG, "waitClient: send out");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket.close();
    }

    public void close() {
        socket.close();
    }
}
