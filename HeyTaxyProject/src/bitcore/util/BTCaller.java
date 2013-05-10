package bitcore.util;

import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;
import bitcore.objects.Taisi;;

public class BTCaller {
	//private final static String URL = "http://54.244.124.64:8080/HT/ServicioTaxero?wsdl"; // amazon 
	private final static String URL = "http://192.168.1.76:8084/HT/ServicioTaxero?wsdl";
	private HashMap<String, Taisi> hmT=new HashMap<String, Taisi>();
	
	
	public HashMap<String, Taisi> callWSServicioTaxero_TaxByExt(){
        final String NAMESPACE = "http://servicios.bitcore/";
        final String METHOD_NAME = "TaxByExt";
        final String SOAP_ACTION = "http://servicios.bitcore/TaxByExt";

        Thread networkThread = new Thread() {
            @Override
            public void run() {
              try {
                 SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);          
                 SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                 request.addProperty("miny", "10");
                 request.addProperty("maxy", "45");
                 request.addProperty("minx", "-120");
                 request.addProperty("maxx", "-80");
                 envelope.setOutputSoapObject(request);
                  
                 HttpTransportSE ht = new HttpTransportSE(URL);
                 ht.call(SOAP_ACTION, envelope);
                 final  SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                 final String str = response.toString();
                 //Log.d("call",str);
             
                 String diviCadena[]=str.split(",");
                 String divSubCadena[];
                 int t=1;
                 for(String s:diviCadena){
             	 	divSubCadena=s.split(":");
             	 	hmT.put(String.valueOf(t),//id del hash map
             	 			new Taisi(  //contruimos el obj taisi
             	 		    divSubCadena[0],  //primer valor es el id
             	 			new LatLng(Double.parseDouble(divSubCadena[2].toString()), Double.parseDouble(divSubCadena[3].toString())),  //elementos 2 y 3 son lat lon
             	 			divSubCadena[1]  // nombre, es el arreglo 1 pero en el constructor se pone al final
             	 					)
             	 			);
             	 	
                 	t++;
                 }
             
              } 
             catch (Exception e) {
                 e.printStackTrace();
             }
            }
          };
          networkThread.start();
          
          return  hmT;
          }
	
	
	
	
	public HashMap<String, Taisi> callWSServicioTaxero_TaxByExt_WithThread(){
        final String NAMESPACE = "http://servicios.bitcore/";
        final String METHOD_NAME = "TaxByExt";
        final String SOAP_ACTION = "http://servicios.bitcore/TaxByExt";

        Thread networkThread = new Thread() {
            @Override
            public void run() {
              try {
                 SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);          
                 SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                 request.addProperty("miny", "20");
                 request.addProperty("maxy", "25");
                 request.addProperty("minx", "-105");
                 request.addProperty("maxx", "-100");
                 envelope.setOutputSoapObject(request);
                  
                 HttpTransportSE ht = new HttpTransportSE(URL);
                 ht.call(SOAP_ACTION, envelope);
                 final  SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                 final String str = response.toString();
                
             
                 String diviCadena[]=str.split(",");
                 String divSubCadena[];
                 int t=1;
                 for(String s:diviCadena){
             	 	divSubCadena=s.split(":");
             	 	//Log.d("Out"," workingg--->>> " + divSubCadena[0] +divSubCadena[1]+divSubCadena[2]+divSubCadena[3] );
             	 	hmT.put(String.valueOf(t),//id del hash map
             	 			new Taisi(  //contruimos el obj taisi
             	 		    divSubCadena[0],  //primer valor es el id
             	 			new LatLng(Double.parseDouble(divSubCadena[2].toString()), Double.parseDouble(divSubCadena[3].toString())),  //elementos 2 y 3 son lat lon
             	 			divSubCadena[1]  // nombre, es el arreglo 1 pero en el constructor se pone al final
             	 					)
             	 					
             	 			);
             	 	
                 	t++;
                 }
                 //Log.d("Out", " Taisis HM ok!: " + hmT.get("1").getId()   +  "  **   " + hmT.get("1").getPosicionActual().latitude);
             
              } 
             catch (Exception e) {
                 e.printStackTrace();
             }
            }
          };
          networkThread.start();
          
          return  hmT;
          }

	
	
}
