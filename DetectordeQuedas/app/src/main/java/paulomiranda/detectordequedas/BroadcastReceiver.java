package paulomiranda.detectordequedas;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;

public class BroadcastReceiver extends android.content.BroadcastReceiver  implements SensorEventListener{

    private ManipulaBluetooh manipulaBluetooh;
    private static String MAC = "98:D3:31:FC:3C:ED";

    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private SensorManager mSensorManager;

    private Context ctx;

    private DecimalFormat df;
    private long inicio_queda = 0;
    private long impacto_queda = 0;
    private long inicio_giro = 0;
    private boolean fase1 = false;
    private boolean fase2 = false;
    private boolean fase3 = false;
    private double media_modulo_a = 0;
    private double media_modulo_g = 0;

    private double max_queda = 0;
    private double min_queda = 0;

    int cont1 = 0;
    int cont2 = 0;

    long old_time;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,mGyroscope,SensorManager.SENSOR_DELAY_FASTEST);

        old_time = new java.util.Date().getTime();
        ctx = context;

        df = new DecimalFormat("#0.0000");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));

        resetar();

        manipulaBluetooh = new ManipulaBluetooh(MAC);
        boolean res = manipulaBluetooh.conectar();

        if(res)
            manipulaBluetooh.putc('l');

    }

    private void resetar(){
        inicio_queda = 0;
        inicio_giro = 0;
        impacto_queda = 0;

        min_queda = 50;
        max_queda = 0;

        fase1 = false;
        fase2 = false;
        fase3 = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        long timestamp = new Date().getTime();

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                cont1++;

                double moduloA = Math.sqrt(x * x + y * y + z * z) / 9.8;
                media_modulo_a += moduloA;

                if(cont1 == 4) {
                    cont1 = 0;

                    media_modulo_a /= 4;

                    if( (min_queda > media_modulo_a) && (media_modulo_a < 0.6) ){
                        inicio_queda = timestamp;
                        min_queda = media_modulo_a;
                        fase1 = true;
                    }


                    if ( (media_modulo_a > 2.5) && (media_modulo_a > max_queda) && fase1 && ( (timestamp - inicio_queda) < 400 ) && !fase2) {
                        max_queda = media_modulo_a;
                        impacto_queda = timestamp;
                    }


                    if (fase1 && (timestamp - inicio_queda >= 400) && !fase2) {
                        if (max_queda > 2.5) {
                            fase2 = true;
                        } else {
                            resetar();
                        }
                    }

                    if ( (timestamp - inicio_queda >= 2000) && (timestamp - inicio_queda < 4000) && fase2 && !fase3) {
                        if ( (media_modulo_a > 0.8) && (media_modulo_a < 1.2) ) {
                            fase3 = true;
                        } else {
                            resetar();
                        }
                    }

                    if ( (timestamp - inicio_queda >= 4000) && (timestamp - inicio_giro < 5000) && fase3) {
                        Toast.makeText(ctx, "Caiu", Toast.LENGTH_LONG).show();
                        manipulaBluetooh.putc('A');
                        resetar();
                    }

                    media_modulo_a = 0;
                }

                break;

            case Sensor.TYPE_GYROSCOPE:
                double moduloG = (Math.sqrt(y * y + z * z) * 180) / Math.PI;
                media_modulo_g += moduloG;

                cont2++;

                if(cont2 == 4) {
                    cont2 = 0;

                    media_modulo_g /= 4;

                    if ((media_modulo_g > 200) && fase1) {
                        inicio_giro = timestamp;
                    }

                    media_modulo_g = 0;
                }

                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
