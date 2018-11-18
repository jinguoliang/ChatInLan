package com.xanarry.lantrans.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by xanarry on 2016/5/24.
 */
public class Utils {
    public static String getMessage(byte[] buffer) {
        try {
            String msg = new String(buffer, "utf-8");
            int eof = msg.indexOf(Configuration.EOF);
            if (eof > 0) {
                return msg.substring(0, eof);
            } else {
                return msg;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHumanReadableSize(final long size) {
        String[] units = {"Byte", "KB", "MB", "GB", "TB", "PB"};
        int pos = 0;
        double dsize = size;
        while (dsize > 1024) {
            dsize /= 1024;
            pos++;
        }
        return (int) (dsize * 100) / 100.0 + units[pos];
    }

    /**
     * 获取本机在局域网中的IP
     *
     * @return
     */
    public static InetAddress getIPInLan() {
        InetAddress result = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();

                if (netInterface.isLoopback() || netInterface.isPointToPoint()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    if (address instanceof Inet4Address) {
                        result = address;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static InetAddress getBroadcastAddress(InetAddress address) {
        if (address == null) {
            return null;
        }

        InetAddress broadcastAddress = null;
        try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            for (InterfaceAddress a : networkInterface.getInterfaceAddresses()) {
                if (a.getAddress().getHostAddress().equals(address.getHostAddress())) {
                    broadcastAddress = a.getBroadcast();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return broadcastAddress;
    }

    public static InetAddress getBroadcastAddress() {
        return getBroadcastAddress(getIPInLan());
    }

    public static void showDialog(Activity activity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);// 定义弹出框
        builder.setTitle(title);// 设置标题
        builder.setMessage(message);// 设置信息主体
        builder.setPositiveButton("知道了",// 设置确定键显示的内容及点击后的操作
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();// 直接关闭对话框
                    }
                });
        builder.create().show();
    }
}
