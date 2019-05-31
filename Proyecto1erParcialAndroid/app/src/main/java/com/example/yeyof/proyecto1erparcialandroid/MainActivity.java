package com.example.yeyof.proyecto1erparcialandroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;

    TextView mVerifBluetooth;
    ImageView mBluetoothIMG;
    ListView listaDispositivos;
    Button mOnBtn, mOffBtn, mPairedBtn;

    BluetoothAdapter mBlueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVerifBluetooth = findViewById(R.id.statusBluetoothTv);
        mBluetoothIMG = findViewById(R.id.bluetoothIMG);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);
        listaDispositivos = findViewById(R.id.IDdispBT);

        //adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bluetooth is available or not
        if (mBlueAdapter == null) {
            mVerifBluetooth.setText("No existe Bluetooth en el dispositivo...");
        } else {
            mVerifBluetooth.setText("El dispositivo tiene Bluetooth");
        }
        //set image according to bluetooth status(on/off)
        if (mBlueAdapter.isEnabled()) {
            mBluetoothIMG.setImageResource(R.drawable.ic_action_on);
        } else {
            mBluetoothIMG.setImageResource(R.drawable.ic_action_off);
        }

        //on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isEnabled()) {
                    showToast("Encendiendo Bluetooth...");
                    //intent para encender bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    showToast("Bluetooth ya está activado");
                }
            }
        });

        //off btn click
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()) {
                    mBlueAdapter.disable();
                    showToast("Apagando Bluetooth");
                    mBluetoothIMG.setImageResource(R.drawable.ic_action_off);
                    ArrayList listavacia = new ArrayList();
                    final ArrayAdapter adaptervacio = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, listavacia);
                    listaDispositivos.setAdapter(adaptervacio);
                } else {
                    showToast("Bluetooth ya está apagado");
                }
            }
        });
        //get paired devices btn click
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter.isEnabled()) {
                    listaDispositivosvinculados();
                } else {
                    showToast("Enciende Bluetooth para obtener los dispositivos.");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    mBluetoothIMG.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth encendido");
                } else {
                    showToast("No se pudo encender el Bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void listaDispositivosvinculados() {
        Set<BluetoothDevice> dispVinculados;
        dispVinculados = mBlueAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (dispVinculados.size() > 0) {
            for (BluetoothDevice bt : dispVinculados) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Obtenemos los nombres y direcciones MAC de los disp. vinculados
            }
        } else {
            Toast.makeText(getApplicationContext(), "No se han encontrado dispositivos vinculados", Toast.LENGTH_LONG).show();
        }
        showToast("Elija el dispositivo al que se conectará");
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listaDispositivos.setAdapter(adapter);
        listaDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dir = (String) listaDispositivos.getItemAtPosition(position);
                String address = dir.substring(dir.length() - 17);
                showToast(address);
                Intent i = new Intent(MainActivity.this, Sensores.class);
                i.putExtra("ADDRESS", address); //this will be received at ledControl (class) Activity
                startActivity(i);
            }
        });

    }

    //toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}