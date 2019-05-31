package com.example.yeyof.proyecto1erparcialandroid;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class Sensores extends AppCompatActivity {

    TextView muestra, valTemp, valVolt, valHall, valRPM;
    Button desconectar;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        Intent newintent = getIntent();
        address = newintent.getStringExtra("ADDRESS");

        desconectar = (Button) findViewById(R.id.btnDesconectar);
        muestra = (TextView) findViewById(R.id.idMuestra);
        valTemp = (TextView) findViewById(R.id.idTemperatura);
        valVolt = (TextView) findViewById(R.id.idVolt);
        valHall = (TextView) findViewById(R.id.idHall);
        valRPM = (TextView) findViewById(R.id.idTacom);

        muestra.append(" a: " + address);

        new ConnectBT().execute(); //Call the class to connect
        new MiAsincronia().execute();

        desconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                showToast("Error");
            }
        }
        finish();
    }

    class MiAsincronia extends AsyncTask<Void, String, Void> {
        protected Void doInBackground(Void... x) {
            byte[] data = new byte[1024];
            int lenght;
            String text;
            String[] valores = null;
            while (true) {
                try {
                    Thread.sleep(999); //
                    lenght = btSocket.getInputStream().read(data);
                    text = new String(data, 0, lenght);
                    valores = text.split("\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishProgress(valores);
            }
        }

        protected void onProgressUpdate(String... texto) {
            valTemp.setText(texto[0]);
            valVolt.setText(texto[1]);
            valHall.setText(texto[2]);
            valRPM.setText(texto[3]);
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Sensores.this, "Conectando...", "Espera, por favor...");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//conectamos al dispositivo y chequeamos si esta disponible
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                showToast("Conexión Fallida");
                finish();
            } else {
                showToast("Conectado");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}