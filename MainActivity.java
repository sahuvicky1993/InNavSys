package com.example.joelwasserman.androidbletutorial;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/* MY SQL  */
import com.example.joelwasserman.androidbletutorial.MySQLDataBasehelper;
import com.example.joelwasserman.androidbletutorial.Book;

public class MainActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    Button startScanningButton;
    Button stopScanningButton;
    TextView peripheralTextView;
    TextView mYlocationShowValue;// vivek
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    int Grssi;
   //ySQLDataBasehelper obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    /*
    We are going to create SQL dataBase which stores BDAddr, RSSI value, location for Beacon
    */  callToDataBase();

        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
        mYlocationShowValue = (TextView) findViewById(R.id.mYlocationShowValue);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());

        startScanningButton = (Button) findViewById(R.id.StartScanButton);
        startScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startScanning();
            }
        });

        stopScanningButton = (Button) findViewById(R.id.StopScanButton);
        stopScanningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopScanning();
            }
        });
        stopScanningButton.setVisibility(View.INVISIBLE);

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();


        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }



    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("vivek","Device Addr:"+result.getDevice().getAddress()+" "+Grssi);
            peripheralTextView.append("Device Addr: "+result.getDevice().getAddress()+"Device Name: " + result.getDevice().getName() + " rssi--: " + result.getRssi()+"\n");

            /* Here we check our location */
            if(result.getDevice().getAddress().equals("C6:E2:61:DD:3D:E7") )
            checkLocation(result.getRssi());
            // auto scroll for text view
            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
            // if there is no need to scroll, scrollAmount will be <=0
            if (scrollAmount > 0)
                peripheralTextView.scrollTo(0, scrollAmount);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void startScanning() {
        System.out.println("start scanning");
        peripheralTextView.setText("");
        mYlocationShowValue.setText("");
        startScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        peripheralTextView.append("Stopped Scanning");
        mYlocationShowValue.setText("");
        startScanningButton.setVisibility(View.VISIBLE);
        stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }
    public void callToDataBase(){
    MySQLDataBasehelper db = new MySQLDataBasehelper(this);

        db.addBook(new Book("C6:E2:61:DD:3D:E7",-55,1010));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-60,1515));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-65,2020));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-70,3030));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-75,3535));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-80,4040));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-85,4545));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-90,5050));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-95,5555));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-100,6060));
        db.addBook(new Book("C6:E2:61:DD:3D:E7",-105,6565));
    }
    public void checkLocation(int rssi){
        int mapRssi=0;
        Boolean isNeg=false;
         /* Range in our database is [-55,-105], check where we lies based on RSSI */
        mYlocationShowValue.setText(Integer.toString(mapRssi));
        if(!((rssi<-55) && (rssi>-105)))  // out of range check
           return;

        int digi1,digi2;
        if(rssi<0) {
            isNeg=true; rssi = -rssi;
        }
        if(rssi%10<5){
            digi1=(rssi/10) *10; //digi2=digi1+5;
            if((rssi%10) >= 3)
                mapRssi=digi1+5;
            else
                mapRssi=digi1;
        }
        else{
            digi1=(rssi/10)*10 +5;
            //digi2=digi1+5;
            if((rssi%10)>=7)
                mapRssi=digi1+5;
            else
                mapRssi=digi1;
        }
        mapRssi=isNeg?(-mapRssi):mapRssi;
        //mYlocationShowValue.setText(Integer.toString(mapRssi));

       MySQLDataBasehelper db = new MySQLDataBasehelper(this);
       int i=db.getBook(mapRssi);
       //Log.i("vivekk ","value"+ Integer.toString(i));
        mYlocationShowValue.setText(Integer.toString(i));


    }
}
