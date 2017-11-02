package paulomiranda.detectordequedas;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;

public class MainActivity extends Activity{

    private BluetoothAdapter meuBluetooth = null;
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private BroadcastReceiver broadcastReceiver = null;

    private TextView txt_status;
    private Button btn_conectar;
    private Button btn_desconectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_status = findViewById(R.id.status);
        btn_conectar = findViewById(R.id.connect);
        btn_desconectar = findViewById(R.id.disconnect);

        meuBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (meuBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Dispositivo não encontrado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!meuBluetooth.isEnabled()) {
            Intent newIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(newIntent, REQUEST_ENABLE_BT);
        }

        updateLayout(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth esta ativado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth nao esta ativado", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    public void conectar(View v) {
        activeBroadcast();
        updateLayout(true);
    }

    public void desconectar(View v) {
        updateLayout(false);
    }

    public void updateLayout(boolean b){
        if(b){
            txt_status.setText("Conectado");
            txt_status.setTextColor(Color.GREEN);
            btn_conectar.setVisibility(View.GONE);
            btn_desconectar.setVisibility(View.VISIBLE);
        }else{
            txt_status.setText("Desconectado");
            txt_status.setTextColor(Color.RED);
            btn_conectar.setVisibility(View.VISIBLE);
            btn_desconectar.setVisibility(View.GONE);
        }
    }


    public void activeBroadcast(){
        IntentFilter it = new IntentFilter();
        it.addAction("BROADCAST_RECEIVER_API");
        it.addCategory(Intent.CATEGORY_DEFAULT);

        broadcastReceiver = new BroadcastReceiver();
        registerReceiver(broadcastReceiver, it);

        Intent intent = new Intent("BROADCAST_RECEIVER_API");
        sendBroadcast(intent);

    }

}