package com.example.testermap;

import java.util.HashMap;

import bitcore.objects.Taisi;
import bitcore.util.BTCaller;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends Activity {
private static final String ID="10000";
private GoogleMap mMap;
private Button b1,b2,bTest;
HashMap<String,Taisi> taxis;
HashMap<String,Marker> markers;
BTCaller bt=new BTCaller();
static final int segundos=3; //tiempo de espera de los calls a WS
private boolean firstMapCharge=true,activo=true;
private  Thread  networkThread,networkThread2;
private String elegido="ninguno";
int iiTest=0;
 
  @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  
//inicia call a web service para tener valores iniciales de posiciones de taxys 
   networkThread = new Thread() {
      @Override
      public void run() {
    	 
    	try{  
    		generaTaisis();
    		Thread.sleep(segundos*1000);
    		
    		runOnUiThread (new Runnable(){ 
    				public void run() {
    					if(firstMapCharge)
    					setUpMapIfNeeded();
    					else
    					setUpMapIfNeeded_SinCargarElMapa();
    				}
        });
    		
    	}catch(InterruptedException e){
    		Log.d("Out","Hilo MainActivity   :   " + e.toString());
    	}
    	 startMainThread();  //Hasta que se haya generado todo el pex de peticiones a WS 
      }
  };
  networkThread.start();
  
  ////////////////////////////////   Panel Botonero  /////////////////////7
  
  b1=(Button)findViewById(R.id.clearBtn);
  b1.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
		mMap.clear();
		activo=false;
		networkThread2.join();
		Log.d("Out", "hilo detenido con exito networkthred2 ..  ");
		}catch(InterruptedException e){
			Log.d("Out",e.toString() + " al deter el hilo  aplicando join() .btn reload");
		}
		
		
	}
});
  
  b2=(Button)findViewById(R.id.reloadBtn);
  b2.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(networkThread.getState().toString().equals("TERMINATED") && networkThread2.getState().toString().equals("TERMINATED")){
		Log.d("Out", "estado del hilo networkthread y networkthread2  ..  "  + networkThread.getState().toString() + "|" +networkThread.getState().toString() + "  procede a pasar activo");
		firstMapCharge=false; // para que solo agrege markers
		
		
		////TODO ESTO ES EQUIVALENTE A LO QUE SE HACE AL PRINCIPIO SOLO QUE DE FORMA ANONIMA, y solo se hace si no hay otro hilo corriendo
		if(!activo){
		activo=true;
		networkThread2= new Thread(new Runnable() {
			
			@Override
			public void run() {
		    	 
		    	try{  
		    		generaTaisis();
		    		Thread.sleep(segundos*1000);
		    		
		    		runOnUiThread (new Runnable(){ 
		    				public void run() {
		    					if(firstMapCharge)
		    					setUpMapIfNeeded();
		    					else
		    					setUpMapIfNeeded_SinCargarElMapa();
		    				}
		        });
		    		
		    	}catch(InterruptedException e){
		    		Log.d("Out","Hilo MainActivity   :   " + e.toString());
		    	}
		    	
		    	
		    	 
		    	
		    	new Thread(new Runnable() {
					@Override
					public void run() {

			    	  	while(activo){
			    		  
			    	try{  
			    		generaTaisis();
		    			Thread.sleep(segundos*1000);
			    		for(int i=1;i<=taxis.size();i++){
			    			taxis.get(String.valueOf(i)).actualizaPosicion(String .valueOf(i));
			    		}
		    			
		    			
			  runOnUiThread (new Runnable(){ 
				  
			      public void run() {
			    	  for(int i=1;i<=markers.size();i++){
			  		  Log.d("Out", " marker: " + taxis.get(String.valueOf(i)).getNombre() + "   id.Taisi: " + taxis.get(String.valueOf(i)).getId()+
			  		 " isi: " + taxis.get(String.valueOf(i)).getPosicionActual()  + "activo == " + activo);
			    	  markers.get(taxis.get(String.valueOf(i)).getId()).setPosition(taxis.get(String.valueOf(i)).getPosicionActual());
			    	  markers.get(taxis.get(String.valueOf(i)).getId()).setTitle(taxis.get(String.valueOf(i)).getNombre());
			    	  
			    	  }        
			      }
			        });
			  
			    	}catch(InterruptedException e){
			    		Log.d("Out","MainHilo MainActivity   :   " + e.toString());
			    	}
			    	 } 
						
					}
				}).start();
			
			
			}
		});//
		networkThread2.start();
		}  // Cierre de sentencia if para evitar multiples hilos haciendo lo mismo thread overloaded
		}
	
	}
});
 
  
  bTest=(Button)findViewById(R.id.btnTest);
  bTest.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
			
		if(networkThread.getState().toString().equals("TERMINATED") && networkThread2.getState().toString().equals("TERMINATED") && !activo){
		Log.d("Out", "estado del hilo networkthread y networkthread2  ..  "  + networkThread.getState().toString() + "|" +networkThread.getState().toString() + "  procede a pasar activo");
		firstMapCharge=false; // para que solo agrege markers
		
		
		activo=true;
		networkThread2= new Thread(new Runnable() {
			
			@Override
			public void run() {
		    	boolean pasa=true; 
				
		    	try{  
		    		
		    		if(!elegido.equals("ninguno")){
		    		
		    			
		    			
		    			generaUnicamenteTaisiPedido(elegido,ID);
				    
		    			
		    			Log.d("Out","EYTY unicamente y taxito Yo :"  + elegido + ":"+ID+"| primero" + taxis.size());
						
		    		}else{
		    		generaTaisis();
		    		}
		    		Thread.sleep(segundos*1000);
		    		
		    		
		    					if(firstMapCharge)
		    					setUpMapIfNeeded();
		    					else
		    					setUpMapIfNeeded_SinCargarElMapa();
		    			
		    		
		    	}catch(InterruptedException e){
		    		Log.d("Out","Hilo MainActivity   :   " + e.toString());
		    	}
		    	
		    	
		    	new Thread(new Runnable() {
					@Override
					public void run() {

			    	  	while(activo){
			    	try{  
			    		if(!elegido.equals("ninguno")){
			    			
			    			
			    		generaUnicamenteTaisiPedido(elegido,ID);
			    		Log.d("Out","EYTY unicamente y taxito Yo :"  + elegido + ":"+ID+"| segundo  111" + taxis.size());
			    		Thread.sleep(segundos*2000);
			    		
			    		
			    		}else{
			    		generaTaisis();
			    		Log.d("Out","EYTY taxinormal  :"  + elegido + ":"+ID+"|");
			    		
			    		}
		    			 Log.d("Out", "EYTY taxi size mmm: " +taxis.size()+"  22222");// taxis.get(String.valueOf(i)).getNombre());
			    		for(int i=1;i<=taxis.size();i++){
			    			taxis.get(String.valueOf(i)).actualizaPosicion(String .valueOf(i));
			    		}
		    			
			    		 
			    		 
			    		
			  runOnUiThread (new Runnable(){ 
			      public void run() {
			    	  
			    	  
			    	  Log.d("Out", "EYTY marker size: " +markers.size() + "  33333");// taxis.get(String.valueOf(i)).getNombre());
			    	  for(int i=1;i<=markers.size();i++){
			  		  Log.d("Out", "EYTY marker: " + taxis.get(String.valueOf(i)).getNombre() + "   id.Taisi: " + taxis.get(String.valueOf(i)).getId()+
			  		 " isi: " + taxis.get(String.valueOf(i)).getPosicionActual()  + "activo == " + activo   + "   4444");
			    	  markers.get(taxis.get(String.valueOf(i)).getId()).setPosition(taxis.get(String.valueOf(i)).getPosicionActual());
			    	  markers.get(taxis.get(String.valueOf(i)).getId()).setTitle(taxis.get(String.valueOf(i)).getNombre());
			    	  
			    	  }   
			    	  
			    	  iiTest++;
			      }
			        });
			  Thread.sleep(5000);
			    	}catch(InterruptedException e){
			    		Log.d("Out","MainHilo MainActivity   :   " + e.toString());
			    	}
			    	 } 
			    	  		
						
					}
				}).start();
			
			
			}
		});//
		networkThread2.start();
		}
	
		
		
		
	}
});
  
  
  
  }

  private void startMainThread() {
	// TODO Auto-generated method stub
	  
	  networkThread2 = new Thread(){
	      @Override
	      public void run() {
	    	  
	    	  
	    	  	while(activo){
	    		  
	    	try{  
	    		generaTaisis();
    			Thread.sleep(segundos*1000);
	    		for(int i=1;i<=taxis.size();i++){
	    			taxis.get(String.valueOf(i)).actualizaPosicion(String .valueOf(i));
	    		}
    			
    			
	  runOnUiThread (new Runnable(){ 
		  
	      public void run() {
	    	  for(int i=1;i<=markers.size();i++){
	  		  Log.d("Out", " marker: " + taxis.get(String.valueOf(i)).getNombre() + "   id.Taisi: " + taxis.get(String.valueOf(i)).getId()  + "isi: " + taxis.get(String.valueOf(i)).getPosicionActual());
	    	  markers.get(taxis.get(String.valueOf(i)).getId()).setPosition(taxis.get(String.valueOf(i)).getPosicionActual());
	    	  markers.get(taxis.get(String.valueOf(i)).getId()).setTitle(taxis.get(String.valueOf(i)).getNombre());
	    	  
	    	  }        
	      }
	        });
	  
	    	}catch(InterruptedException e){
	    		Log.d("Out","MainHilo MainActivity   :   " + e.toString());
	    	}
	    	  
} 
	    	  
	      }
	  };
	  networkThread2.start();
	
}


private void generaTaisis(){
	  
	  
	  taxis=bt.callWSServicioTaxero_TaxByExt();
	  /*El id del hashmap no tiene que ser el mismo de el objeto taisi, si no el for que carga los taisis en markers FALLARA  CUIDADO!!
	  taxis.put("1",new Taisi("1", new LatLng(26.15058, -104.98814)));
	  taxis.put("2",new Taisi("2", new LatLng(24.15058, -106.98814)));
	  taxis.put("3",new Taisi("3", new LatLng(22.15058, -108.98814)));
	  taxis.put("4",new Taisi("4", new LatLng(20.15058, -110.98814)));*/
  }

/*
 * HACE LO MISMO QUE EL GENERATAISIS PERO SE PLANEA SOLO NOS REGRESE INFO DEL TAXITOYO Y TAXITO ELEGIDO :)
 * 
 */
private void generaUnicamenteTaisiPedido(String t_Pedido,String pasajero){
	  
	  taxis=bt.callWSServicioTaxero_TaxByExt(t_Pedido,pasajero);
	  /*El id del hashmap no tiene que ser el mismo de el objeto taisi, si no el for que carga los taisis en markers FALLARA  CUIDADO!!
	  taxis.put("1",new Taisi("1", new LatLng(26.15058, -104.98814)));
	  taxis.put("2",new Taisi("2", new LatLng(24.15058, -106.98814)));
	  taxis.put("3",new Taisi("3", new LatLng(22.15058, -108.98814)));
	  taxis.put("4",new Taisi("4", new LatLng(20.15058, -110.98814)));*/
}
  
  
  private void setUpMapIfNeeded() {

  if (mMap == null) {
   mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
   mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
	
		String idMark="0";
		 for(int i=1;i<=taxis.size();i++){
			 
				if(taxis.get(String.valueOf(i)).getNombre().equals(arg0.getTitle())){
					idMark=taxis.get(String.valueOf(i)).getId();
				}
				}
		
			Intent intent = new Intent(MainActivity.this,FormActivity.class);
			//startActivity(intent);
			   intent.putExtra("taxyID",idMark);
			   final int result=1;
			   startActivityForResult(intent, result);
		
		return false;
	}
});
   
   
   // Check if we were successful in obtaining the map.
   if (mMap != null) {
    addMarkers();
   }
  }
 }
  
  private void setUpMapIfNeeded_SinCargarElMapa() {
   // Check if we were successful in obtaining the map.
   if (mMap != null) 
    addMarkers();
 }

  
  
  private void addMarkers() {
	  markers=new HashMap<String, Marker>();
	  int yo=R.drawable.taxito;
	  
	  for(int i=1;i<=taxis.size();i++){
		  
		if(taxis.get(String.valueOf(i)).getId().equals(String.valueOf(this.ID)))
			yo=R.drawable.taxitoyo;
		else
			yo=R.drawable.taxito;
		
		  
		  markers.put(taxis.get(String.valueOf(i)).getId(),    //No se usa el id de el objeto  por que ya va en title del marker. Asi es mas facil la iteracion futura
				mMap.addMarker(new MarkerOptions()
				.position(taxis.get(String.valueOf(i)).getPosicionActual())
				.title(taxis.get(String.valueOf(i)).getNombre())
				.snippet("mensaje snippet")
				.icon(BitmapDescriptorFactory.fromResource(yo))));
		  
		  
	  }
	
	  }
  

  
  @Override
  public void onActivityResult(int requestCode,int resultCode,Intent data)
  {
   super.onActivityResult(requestCode, resultCode, data);
   this.elegido=data.getStringExtra("elegido");
   
   
   Toast.makeText(getApplicationContext(),elegido + " es el elegido :) ", Toast.LENGTH_LONG).show();
   
  
  }
  

}
