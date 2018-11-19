package com.jone.lanchat.utils;

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

/**
 * Created by xanarry on 2016/5/24.
 */
public class Utils {

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
}
