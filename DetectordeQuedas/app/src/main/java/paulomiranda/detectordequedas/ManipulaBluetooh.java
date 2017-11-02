package paulomiranda.detectordequedas;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ManipulaBluetooh {

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;

    private String mac;
    private UUID MEU_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public ManipulaBluetooh(String mac){
        this.mac = mac;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public char getc() {
        char c = 0;

        try {
            inStream = btSocket.getInputStream();
            c = (char) inStream.read();

        } catch (IOException e) {
        }

        return c;
    }

    public boolean putc(char data) {
        try {
            outStream = btSocket.getOutputStream();

            outStream.write(data);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean puts(String data) {
        try {
            outStream = btSocket.getOutputStream();
            byte[] msgBuffer = data.getBytes();

            outStream.write(msgBuffer);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean verificarConexao(){
        if (btSocket != null) {
            return btSocket.isConnected();
        }

        return false;
    }

    public boolean conectar() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MEU_UUID);
            btSocket.connect();

            return btSocket.isConnected();
        } catch (IOException e) {
            return false;
        }

    }

    public boolean desconectar() {

        if (btSocket != null) {
            try {
                btSocket.close();
                btSocket = null;

                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

}
