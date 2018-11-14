package com.xanarry.lantrans.network;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import static com.xanarry.lantrans.utils.Configuration.BROADCAST_DATA;
import static com.xanarry.lantrans.utils.Configuration.RESPONSE_DATA;

/**
 * Created by xanarry on 2016/5/22.
 */
public class UdpServer {
    public static final String TAG = UdpServer.class.getName();;

    private int mPort;
    private DatagramSocket socket;
    private boolean mLive = true;

    public UdpServer(int port) {
        this.mPort = port;
    }

    public void waitClient() throws IOException {
            socket = new DatagramSocket(mPort);//设置服务器端口, 监听广播信息

            byte[] buf = new byte[BROADCAST_DATA.getBytes().length];
            DatagramPacket message = new DatagramPacket(buf, buf.length);
            while (mLive) {
                message.setData(buf);
                try {
                    socket.receive(message);
                } catch (SocketTimeoutException e) {

                }

                String msg = new String(message.getData());

                Log.e(TAG, "waitClient: " + msg);
                if (BROADCAST_DATA.equals(msg)) {
                    message.setData(RESPONSE_DATA.getBytes());
                    socket.send(message);
                    Log.e(TAG, "waitClient: send out");
                }
            }
        socket.close();
    }

    public void kill() {
        mLive = false;
    }

    public void close() {
        socket.close();
    }
}
