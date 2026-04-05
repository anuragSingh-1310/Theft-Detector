package com.example.theftdetector;

import android.hardware.usb.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.hoho.android.usbserial.driver.*;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView statusText;
    Button connectBtn;
    UsbManager usbManager;
    UsbSerialPort port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        connectBtn = findViewById(R.id.connectBtn);

        usbManager = (UsbManager) getSystemService(USB_SERVICE);

        connectBtn.setOnClickListener(v -> connectUSB());
    }

    private void connectUSB() {
        List<UsbSerialDriver> drivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

        if (drivers.isEmpty()) {
            statusText.setText("No USB device found");
            return;
        }

        UsbSerialDriver driver = drivers.get(0);
        UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());

        if (connection == null) {
            statusText.setText("Permission denied");
            return;
        }

        port = driver.getPorts().get(0);

        try {
            port.open(connection);
            port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            new Thread(() -> {
                byte[] buffer = new byte[1024];

                while (true) {
                    try {
                        int len = port.read(buffer, 1000);
                        String data = new String(buffer, 0, len).trim();

                        runOnUiThread(() -> {
                            if (data.contains("ALERT")) {
                                statusText.setText("🚨 INTRUSION DETECTED!");
                                statusText.setTextColor(0xFFFF0000);
                            } else if (data.contains("SAFE")) {
                                statusText.setText("✅ SAFE");
                                statusText.setTextColor(0xFF4CAF50);
                            }
                        });

                    } catch (Exception e) {
                        break;
                    }
                }
            }).start();

        } catch (Exception e) {
            statusText.setText("Connection Failed");
        }
    }
}