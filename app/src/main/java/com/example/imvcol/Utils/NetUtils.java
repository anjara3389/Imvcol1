package com.example.imvcol.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class NetUtils {

    public static Boolean isOnlineNet(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info_wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo info_datos = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return String.valueOf(info_wifi.getDetailedState()).equals("CONNECTED") || String.valueOf(info_datos.getDetailedState()).equals("CONNECTED");
    }

    public static String getIP() throws SocketException {
        List<InetAddress> addrs;
        String address = "";

        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface intf : interfaces) {
            addrs = Collections.list(intf.getInetAddresses());
            for (InetAddress addr : addrs) {
                if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                    address = addr.getHostAddress().toUpperCase(new Locale("es", "CO"));
                }
            }
        }
        return address;
    }

}
