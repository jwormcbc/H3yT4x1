package bitcore.objects;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Taisi {
private LatLng posicionActual;
private String id="",nombre="";



public Taisi(String id,LatLng posicion,String nombre){
	this.id=id;
	this.posicionActual=posicion;
	this.nombre=nombre;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public LatLng getPosicionActual() {
	return posicionActual;
}

public void setPosicionActual(LatLng posicionActual) {
	this.posicionActual = posicionActual;
}

public String getNombre() {
	return nombre;
}

public void setNombre(String nombre) {
	this.nombre = nombre;
}

public void actualizaPosicion(String id){
    final String NAMESPACE = "http://servicios.bitcore/";
    final String METHOD_NAME = "PosicionAcualByID";
    final String SOAP_ACTION = "http://servicios.bitcore/PosicionAcualByID";
    final String URL = "http://192.168.1.101:8080/HT/ServicioTaxero?wsdl";

    Thread networkThread = new Thread() {
        @Override
        public void run() {
          try {
             SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);          
             SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
             request.addProperty("id",Taisi.this.id);
             envelope.setOutputSoapObject(request);
              
             HttpTransportSE ht = new HttpTransportSE(URL);
             ht.call(SOAP_ACTION, envelope);
             final  SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
             final String str = response.toString();
            
         
             String diviCadena[]=str.split(",");
             Log.d("Out","--" + diviCadena[0] + "   --  " + diviCadena[1]);
             Taisi.this.setPosicionActual(new LatLng(Double.parseDouble(diviCadena[0]),Double.parseDouble(diviCadena[1])));
         
          } 
         catch (Exception e) {
             e.printStackTrace();
         }
        }
      };
      networkThread.start();
      
      
      }

}
