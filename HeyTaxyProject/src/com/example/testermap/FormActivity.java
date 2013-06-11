package com.example.testermap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FormActivity extends Activity {
	TextView tv;
	Button pedirBtn,regresarBtn;
	String idElegido="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		pedirBtn=(Button)findViewById(R.id.btnPedir);
		regresarBtn=(Button)findViewById(R.id.btnCancelarRegresar);
		
		
		 Intent sender=getIntent();
         idElegido=sender.getExtras().getString("taxyID");
         tv=(TextView)findViewById(R.id.textView1);
         tv.setText("Id. Taxista:"+ idElegido);
         
         pedirBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
				/*
				 * 
				 * ANTES DE TODO ESTO EL TAXY DEBE ACEPTAR.. PARTE PENDIENTE APP TAXISTA AUN NO LISTA PARA RESPONDER
				 * 
				 * 
				 * 
				 */
				
				   Intent intent=new Intent();
				    intent.putExtra("elegido",idElegido);
				    setResult(RESULT_OK, intent);
				    finish(); //  callbackeddd  onActivityResult del activity anterior ajua!
				
			}
		});
         
         regresarBtn.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View arg0) {
 				// TODO Auto-generated method stub
 				
 				
 				/*
 				 * 
 				 * ANTES DE TODO ESTO EL TAXY DEBE ACEPTAR.. PARTE PENDIENTE APP TAXISTA AUN NO LISTA PARA RESPONDER
 				 * 
 				 * 
 				 * 
 				 */
 				
 				   Intent intent=new Intent();
 				    intent.putExtra("elegido","ninguno");
 				    setResult(RESULT_OK, intent);
 				    finish(); //  callbackeddd  onActivityResult del activity anterior ajua!
 				
 			}
 		});
         
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form, menu);
		return true;
	}

}
