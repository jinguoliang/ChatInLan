package com.xanarry.lantrans.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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

    public static InetAddress getLocalHostLanIP() {
        //获取本机在局域网中的IP
        Enumeration<?> allNetInterfaces;
        InetAddress IP = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                //System.out.println(netInterface.getName());
                if (netInterface.isLoopback() || netInterface.isPointToPoint()) {
                    continue;
                }

                Enumeration<?> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress tempIP = (InetAddress) addresses.nextElement();
                    if (tempIP != null && tempIP instanceof Inet4Address) {
                        //System.out.println("本机的IP=" + tempIP.getHostAddress());
                        IP = tempIP;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return IP;
    }

    public static InetAddress getBroadcastAddr(InetAddress address) {
        //获取本机在局域网中的广播地址
        if (address == null) {
            return null;
        }
        InetAddress broadcastAddr = null;
        NetworkInterface networkInterface;
        try {
            networkInterface = NetworkInterface.getByInetAddress(address);
            for (InterfaceAddress taddr : networkInterface.getInterfaceAddresses()) {
                //获取指定ip的广播地址
                if (taddr.getAddress().getHostAddress().equals(address.getHostAddress())) {
                    broadcastAddr = taddr.getBroadcast();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return broadcastAddr;
    }

    public static InetAddress getBroadcastAddr() {
        return getBroadcastAddr(getLocalHostLanIP());
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
