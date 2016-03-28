package com.graffixnyc.rootchecker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.stericson.RootTools.RootTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    InterstitialAd interstitial;
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    String rootStatus;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Storage Permissions
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        AdView adView;
        adView = (AdView)findViewById(R.id.adView);

        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-9686536348398632/4182050505");

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice("574FDECC732294731FB9CEE4F5F23C6E")
                .addTestDevice("5FF1038AD320B1CC54DFCACC32761729")
                .build();
        // Start loading the ad in the background.
        adView.loadAd(adRequest2);

        Button btnCheckRoot=(Button)findViewById(R.id.button);

        final TextView tvDevice=(TextView)findViewById((R.id.textView8));
        String makeModel = "<b>Manufacturer:</b> " + Build.MANUFACTURER + " <br><b>Model:</b> " + Build.MODEL + "<br><b>Android Version:</b> " +  Build.VERSION.RELEASE ;
        tvDevice.setText(Html.fromHtml(makeModel));

        btnCheckRoot.setOnClickListener(new View.OnClickListener() {


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Spannable text = new SpannableString("Simple Root Checker");


                getSupportActionBar().setTitle(text);
                new PostTask().execute();


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i=new Intent(this,About.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_share) {
            saveSS();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();
        //displayInterstitial();
    }

    @Override
    public void onStop() {
        super.onStop();
        displayInterstitial();
        //File file = new File(Environment.getExternalStorageDirectory()
        //      + File.separator + "Pictures/screenshot.png");
        //boolean deleted = file.delete();
    }

    // Invoke displayInterstitial() when you are ready to display an interstitial.
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }




    public void saveSS() {

        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap myBitmap  = v1.getDrawingCache();

        String filePath = Environment.getExternalStorageDirectory()
                + File.separator + "Pictures/root_checker_screenshot.png";
        File imagePath = new File(filePath);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            //sendMail(filePath);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    createShareIntent(MainActivity.this,rootStatus,"Get Simple Root Checker here: https://play.google.com/store/apps/details?id=com.graffixnyc.rootchecker\n\n#SimpleRootChecker");
                }
            }, 150);

        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void createShareIntent(Context context, String title, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);

        String extraText = title != null ? title + "\n\n" + url : url;

        //share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ss));
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures/root_checker_screenshot.png");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        share.putExtra(android.content.Intent.EXTRA_TEXT, extraText);
        share.setType("image/*");
        context.startActivity(Intent.createChooser(share, "Share using"));

    }
    private class PostTask extends AsyncTask<String, Integer, String>
        {
            int RootAccess=0;
            int BB=0;

        @Override
        protected String doInBackground(String... params) {
            if (RootTools.isAccessGiven()) {
                RootAccess=1;


            } else {



            }
            if (RootTools.isBusyboxAvailable()) {
                BB=1;


            } else {
                BB=0;

            }

            return "All Done!";
        }


            @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            final TextView tvRoot=(TextView)findViewById(R.id.tvRoot);
            final TextView tvBB=(TextView)findViewById(R.id.tvBB);
            ImageView imgView = (ImageView) findViewById(R.id.imageView);
            //int currentapiVersion = android.os.Build.VERSION.SDK_INT;

                Drawable colorDrawable1 = new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.green));
                Drawable colorDrawable2 = new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.red));
                Drawable colorDrawable3 = new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.yellow));

            if (RootAccess==1)
                    {
                        tvRoot.setText("Congrats! You are Rooted!!");
                        imgView.setImageResource(R.drawable.logogreen);
                        rootStatus = "My " + Build.MANUFACTURER + " " + Build.MODEL + " running Android " + Build.VERSION.RELEASE + " is owned!";

                        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.darkgreen));
                                getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.darkgreen));
                            }
                        getSupportActionBar().setBackgroundDrawable(colorDrawable1);
                    }
                else
                    {
                        tvRoot.setText(Html.fromHtml("Sorry you are <b><font color='red'>NOT</font></b> rooted!!"));
                        rootStatus=   "No root for me! :(  Damn it, I needz rootz for the " + Build.MANUFACTURER + " " + Build.MODEL + " running Android version " + Build.VERSION.RELEASE + "!";
                        imgView.setImageResource(R.drawable.logored);
                        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.darkred));
                            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.darkred));
                        }
                        getSupportActionBar().setBackgroundDrawable(colorDrawable2);
                    }
                if (BB==1){
                    String BBver=RootTools.getBusyBoxVersion();
                    tvBB.setText("Congrats! You have BusyBox installed!!  \nBusyBox Version: " + BBver);
                    //Toast.makeText(MainActivity, BB, Toast.LENGTH_SHORT).show();
                }
                else if (BB==0)
                    {
                        tvBB.setText(Html.fromHtml("BusyBox is <b><font color='red'>NOT</font></b> installed!!"));
                        //Toast.makeText(MainActivity.this, "Else BB", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

