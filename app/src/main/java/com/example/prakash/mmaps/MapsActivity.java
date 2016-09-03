package com.example.prakash.mmaps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class MapsActivity extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    static final String MARKER = "marker";
    static final String url = "http://mymap.site11.com/getmap1.php";//site url
    //static final String url  = "localhost/maps/getmap.php";
    //private ArrayList<HashMap<String, String>> mMarkers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //ArrayList<HashMap<String, String>> mMarkers = new ArrayList<HashMap<String, String>>();
        /*
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("No Network")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }*/
        LocationManager locationManager;
         LocationListener locationListener;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        new GetXMLAsyncTask().execute(url);
        //setUpMapIfNeeded();
    }
    private class GetXMLAsyncTask extends AsyncTask< String, MarkerOptions ,String> {


        private XMLParser parser = null;
        boolean i=true;
        String name;
        String lat,lng;
        String time;
        public GetXMLAsyncTask() {
            super();
            //mMarkers = new ArrayList<>();
            parser = new XMLParser();
        }
        void Sleep(int ms)
        {
            try
            {
                Thread.sleep(ms);
            }
            catch (Exception e)
            {

            }
        }

        @Override
        protected void onPreExecute() {
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                setUpMap();
            }
        }
        @Override
        protected String doInBackground (String...urls){
                //boolean enuf = false;
                boolean RT=true;
                boolean MT=false;
                MarkerOptions options = new MarkerOptions();
                String xml = parser.getXmlFromUrl(urls[0]); // getting XML
            if (mMap != null) {
                while(RT) {
                    if(MT) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mMap.clear();
                            }
                        });
                    }
                    MT=true;
                    xml = parser.getXmlFromUrl(urls[0]);
                    if (xml != null) {
                        Document doc = parser.getDomElement(xml);
                        NodeList nl = doc.getElementsByTagName(MARKER);
                        //int s = nl.getLength();
                        for (int x = 0, size = nl.getLength(); x < size; x++) {
                            name = nl.item(x).getAttributes().getNamedItem("name").getNodeValue();
                            lat = nl.item(x).getAttributes().getNamedItem("lat").getNodeValue();
                            lng = nl.item(x).getAttributes().getNamedItem("lng").getNodeValue();
                            time = nl.item(x).getAttributes().getNamedItem("time").getNodeValue();
                        /*HashMap<String, String> map = new HashMap<>();
                        map.put("Name", nl.item(x).getAttributes().getNamedItem("name").getNodeValue());
                        map.put("Lat", nl.item(x).getAttributes().getNamedItem("lat").getNodeValue());
                        map.put("Lng", nl.item(x).getAttributes().getNamedItem("lng").getNodeValue());
                        mMarkers.add(map);*/
                        /*if(!enuf){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat),Double.parseDouble(lng)), 15));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
                            enuf=true;
                        }*/
                            options.position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name+ " - "+ time);
                            publishProgress(options);
                            Sleep(1000);

                        }
                    /*MarkerOptions options = new MarkerOptions();
               options.position(new LatLng(13.009657, 80.004224)).title(25 + "\n 0 Km/h");
               mMap.addMarker(options);
               int duration = Toast.LENGTH_SHORT;
               Toast toast = Toast.makeText(context," Connected ", duration);
               toast.show();
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.009657,80.004224), 15));
               mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);*/

                    }
                    else{
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context," Error Connecting Server ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }

            return xml;
        }

            @Override
            protected void onProgressUpdate (MarkerOptions...options) {
                  if (i) {
                        mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 11)));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 3000, null);
                        i = false;
                    }
                    mMap.addMarker(options[0]);
                    //Toast toast = Toast.makeText(context,"size is "+size[0],Toast.LENGTH_SHORT);
                    //toast.show();


            }

            @Override
            protected void onPostExecute (String result){

                super.onPostExecute(result);
                //setUpMap();
                //setUpMapIfNeeded(xml);
            }


       /* @Override
        protected void onPostExecute(String xml) {

            //super.onPostExecute(xml);
           setUpMapIfNeeded(xml);
        }

        @Override
        protected String doInBackground(String... urls) {
            return parser.getXmlFromUrl(urls[0]); // getting XML
        }*/
        //}
    }
    @Override
    protected void onResume() {
        super.onResume();
        new GetXMLAsyncTask().execute(url);
    }
       private class GPSLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);                            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

   protected void createLocationRequest() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

      /*private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            XMLParser parser = new XMLParser();
            if (mMap != null) {
                if (xml!=null) {
                    Document doc = parser.getDomElement(xml);
                    NodeList nl = doc.getElementsByTagName(MARKER);
                    for(int x=0,size= nl.getLength(); x<size; x++) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Name",nl.item(x).getAttributes().getNamedItem("name").getNodeValue());
                        map.put("Lat",nl.item(x).getAttributes().getNamedItem("lat").getNodeValue());
                        map.put("Lng",nl.item(x).getAttributes().getNamedItem("lng").getNodeValue());
                        mMarkers.add(map);
                    }
                    /*int size = mMarkers.size();
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context,Integer.toString(size),Toast.LENGTH_SHORT);
                    toast.show();// comment here
                }
                setUpMap();
            }
        }
    }*/

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        createLocationRequest();

                /*MarkerOptions options = new MarkerOptions();
               options.position(new LatLng(13.009657, 80.004224)).title(25 + "\n 0 Km/h");
               mMap.addMarker(options);
               int duration = Toast.LENGTH_SHORT;
               Toast toast = Toast.makeText(context," Connected ", duration);
               toast.show();
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.009657,80.004224), 15));
               mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);*/


    }
}

