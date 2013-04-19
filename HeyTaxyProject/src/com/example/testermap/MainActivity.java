package com.example.testermap;

import java.util.HashMap;

import bitcore.objects.Taisi;
import bitcore.util.BTCaller;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class MainActivity extends Activity {

private GoogleMap mMap;
HashMap<String,Taisi> taxis;
HashMap<String,Marker> markers;
BTCaller bt=new BTCaller();
static final int segundos=2; //tiempo de espera de los calls a WS
 
  @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  
//inicia call a web service para tener vores iniciales de posiciones de taxys 
  Thread networkThread = new Thread() {
      @Override
      public void run() {
    	 
    	try{  
    		Log.d("Out","wait info ");
    		generaTaisis();
    		Thread.sleep(1000);
    		Log.d("Out","info ok");
    		
    		runOnUiThread (new Runnable(){ 
    				public void run() {
    					setUpMapIfNeeded();
    				}
        });
    		
    	}catch(InterruptedException e){
    		Log.d("Out","Hilo MainActivity   :   " + e.toString());
    	}
    	 startMainThread();  //Hasta que se haya generado todo el pex de peticiones a WS 
      }
  };
  networkThread.start();
   
  
  
 
  
  }

  private void startMainThread() {
	// TODO Auto-generated method stub
	  
	  Thread networkThread = new Thread() {
	      @Override
	      public void run() {
	    	  
	    	  
	    	  	while(true){
	      		Log.d("Out","Itera While... Start!!");
	    		  
	    	try{  
	    		generaTaisis();
    			Thread.sleep(segundos*1000);
	    		for(int i=1;i<=taxis.size();i++){
	    			taxis.get(String.valueOf(i)).actualizaPosicion(String .valueOf(i));
	    		}
    			
    			
	  runOnUiThread (new Runnable(){ 
		  
	      public void run() {
	    	  for(int i=1;i<=markers.size();i++){
	    	  markers.get(String.valueOf(i)).setPosition(taxis.get(String.valueOf(i)).getPosicionActual());
	    	  markers.get(String.valueOf(i)).setTitle(taxis.get(String.valueOf(i)).getNombre());
	    	  markers.get(String.valueOf(i)).setSnippet("mensaje snipet");
	    	  }        
	      }
	        });
	  
	    	}catch(InterruptedException e){
	    		Log.d("Out","MainHilo MainActivity   :   " + e.toString());
	    	}
	    	  
} 
	    	  
	      }
	  };
	  networkThread.start();
	
}





private void generaTaisis(){
	  
	  
	  taxis=bt.callWSServicioTaxero_TaxByExt();
	  /*El id del hashmap no tiene que ser el mismo de el objeto taisi, si no el for que carga los taisis en markers FALLARA  CUIDADO!!
	  taxis.put("1",new Taisi("1", new LatLng(26.15058, -104.98814)));
	  taxis.put("2",new Taisi("2", new LatLng(24.15058, -106.98814)));
	  taxis.put("3",new Taisi("3", new LatLng(22.15058, -108.98814)));
	  taxis.put("4",new Taisi("4", new LatLng(20.15058, -110.98814)));*/
  }
  
  
  
  
  
  private void setUpMapIfNeeded() {

  if (mMap == null) {
   mMap = ((MapFragment) getFragmentManager().findFragmentById(
     R.id.map)).getMap();
   // Check if we were successful in obtaining the map.
   if (mMap != null) {
    addMarkers();
   }
  }
 }

  
  private void addMarkers() {
	  markers=new HashMap<String, Marker>();
	 
	  for(int i=1;i<=taxis.size();i++){
		Log.d("Out", " marker: " + String.valueOf(i));
		markers.put(String.valueOf(i),    //No se usa el id de el objeto  por que ya va en title del marker. Asi es mas facil la iteracion futura
				mMap.addMarker(new MarkerOptions()
				.position(taxis.get(String.valueOf(i)).getPosicionActual())
				.title(taxis.get(String.valueOf(i)).getNombre())
				.snippet("mensaje snippet")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxito))));
	  }
	
	  }
  


}
