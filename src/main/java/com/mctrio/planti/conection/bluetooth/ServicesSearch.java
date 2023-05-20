/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mctrio.planti.conection.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 *
 * Minimal Services Search example.
 */
public class ServicesSearch {

    private boolean BT_CONNECTED = false;
    String RX_STRING = "";
    private boolean BT_SCAN_FINISHED = false;
    private String BT_DEVICE_ADDRESS = "";
    private String BT_URL = "btspp://" + BT_DEVICE_ADDRESS + ":1;authenticate=false;encrypt=false;master=false";
    private RemoteDevice BT_DEVICE;
    private String[] BT_SPLIT_STRING;

    private StreamConnection BT_STREAM_CONNECTION;
    private OutputStream BT_OUT_STREAM;
    private InputStream BT_IN_STREAM;
    private boolean BT_OUT_STREAM_OPEN = false;
    private boolean BT_IN_STREAM_OPEN = false;

    private Thread BT_SCAN_THREAD;
    private Thread BT_CONNECT_THREAD;
    private Thread BT_RECEIVE_THREAD;

    private final ArrayList<String> BT_DEVICE_LIST = new ArrayList<>();

    public void scan() {
        //BT_DEVICE_LIST.removeAllItems();

        BT_SCAN_THREAD = new Thread(() -> {
            try {
                BT_SCAN_FINISHED = false;
                LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
                    @Override
                    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                        try {
                            String name = btDevice.getFriendlyName(false);
                            BT_DEVICE = btDevice;
                            BT_DEVICE_LIST.add(name.trim() + "::" + BT_DEVICE.getBluetoothAddress().trim());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void inquiryCompleted(int discType) {
                        BT_SCAN_FINISHED = true;
                    }

                    @Override
                    public void serviceSearchCompleted(int transID, int respCode) {
                    }

                    @Override
                    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                    }
                });
                while (!BT_SCAN_FINISHED) {
                    System.out.println("Scanning...");
                    Thread.sleep(500);
                }
                UUID uuid = new UUID(0x1101);
                UUID[] searchUuidSet = new UUID[]{uuid};
                int[] attrIDs = new int[]{
                    0x0100
                };
                BT_SCAN_FINISHED = false;
                System.out.println("Allmost there...");

                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, BT_DEVICE, new DiscoveryListener() {
                    @Override
                    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    }

                    @Override
                    public void inquiryCompleted(int discType) {
                    }

                    @Override
                    public void serviceSearchCompleted(int transID, int respCode) {
                        System.out.println("Scanning finished");
                        BT_SCAN_FINISHED = true;
                        //BT_DEVICE_LIST.setSelectedIndex(0);
                        BT_DEVICE_LIST.forEach(System.out::println);
                    }

                    @Override
                    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                        for (ServiceRecord servRecord1 : servRecord) {
                            BT_URL = servRecord1.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                            if (BT_URL != null) {
                                break;
                            }
                        }
                    }
                });
            } catch (InterruptedException | BluetoothStateException e) {
                System.out.println(e);
            }
        });

        BT_SCAN_THREAD.start();
    }

    public void connect() {
        BT_CONNECT_THREAD = new Thread(() -> {
            BT_DEVICE_LIST.add("Planti::00210734D041");
            String dd = BT_DEVICE_LIST.get(0);
            BT_SPLIT_STRING = dd.split("::");
            BT_DEVICE_ADDRESS = BT_SPLIT_STRING[1];

            BT_URL = "btspp://" + BT_DEVICE_ADDRESS + ":1;authenticate=false;encrypt=false;master=false";

            if (BT_CONNECTED) {
                try {
                    BT_OUT_STREAM.close();
                    BT_IN_STREAM.close();
                    BT_STREAM_CONNECTION.close();
                    BT_OUT_STREAM_OPEN = false;
                    BT_IN_STREAM_OPEN = false;
                    BT_CONNECTED = false;
                    //BT_CONNECT_BTN.setText("Connect");
                    //logg("disconnected");

                } catch (IOException e) {
                }
            } else if (!BT_CONNECTED) {
                try {
                    BT_STREAM_CONNECTION = (StreamConnection) Connector.open(BT_URL);
                    BT_OUT_STREAM = BT_STREAM_CONNECTION.openOutputStream();
                    BT_IN_STREAM = BT_STREAM_CONNECTION.openInputStream();
                    BT_OUT_STREAM_OPEN = false;
                    BT_IN_STREAM_OPEN = false;
                } catch (IOException e) {
                    BT_CONNECTED = false;
                    BT_OUT_STREAM_OPEN = false;
                    BT_IN_STREAM_OPEN = false;
                    System.out.println("cannot connect to selected device !");
                    return;
                }
                BT_CONNECTED = true;
                //BT_CONNECT_BTN.setEnabled(true);
                System.out.println("connected to :" + BT_DEVICE_LIST.get(0) + "\n");
            }
            if (BT_CONNECTED) {
                //BT_CONNECT_BTN.setText("Disconnect");
                //BT_DEVICE_LIST.setEnabled(false);
                BT_OUT_STREAM_OPEN = true;
                BT_IN_STREAM_OPEN = true;

                ReceiveData();
            } else {
            }

        });

        BT_CONNECT_THREAD.start();
    }

    public void ReceiveData() {
        BT_RECEIVE_THREAD = new Thread(() -> {
            if (BT_CONNECTED && BT_IN_STREAM_OPEN) {

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(BT_IN_STREAM))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        // Procesar la línea recibida
                        String[] pares = linea.split(";");
                        for (String par : pares) {
                            String[] partes = par.split("=");
                            String variable = partes[0];
                            String valor = partes[1];

                            // Realizar alguna acción con la variable y el valor
                            System.out.println(variable + ": " + valor);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        BT_RECEIVE_THREAD.start();

    }
}
