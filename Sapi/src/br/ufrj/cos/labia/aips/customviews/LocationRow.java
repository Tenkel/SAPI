package br.ufrj.cos.labia.aips.customviews;

import com.tenkel.sapi.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

public class LocationRow extends TableRow {
	TextView andar;
    TextView posicao;
    TextView probabilidade;
    TextView confianca;
    
    static final String tenkel_xmls = "http://schemas.android.com/apk/res/com.tenkel.sapi";

	public LocationRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutParams rowParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    LayoutParams innerParamsw1 = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
	    LayoutParams innerParamsw2 = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT, 2);
	    
	    String string_temporary;
	    
	    
	    this.setLayoutParams(rowParams);
	    
	    andar = new TextView(context);
	    posicao = new TextView(context);
	    probabilidade = new TextView(context);
	    confianca = new TextView(context);
	    
	    andar.setLayoutParams(innerParamsw2);
	    andar.setGravity(Gravity.CENTER);
	    if((string_temporary = attrs.getAttributeValue(tenkel_xmls, "andar"))!= null)
	    	andar.setText(string_temporary);
	    else
	    	andar.setText("Andar Atual");
	    
	    posicao.setLayoutParams(innerParamsw1);
	    posicao.setGravity(Gravity.CENTER);
	    posicao.setTextColor(getResources().getColor(R.color.Red));
    	posicao.setText(String.valueOf(attrs.getAttributeUnsignedIntValue(tenkel_xmls, "posicao", 0)));
	    
	    probabilidade.setLayoutParams(innerParamsw1);
	    probabilidade.setGravity(Gravity.CENTER);
	    probabilidade.setTextColor(getResources().getColor(R.color.black));
	    probabilidade.setText(String.format("%.1f",attrs.getAttributeFloatValue(tenkel_xmls, "probabilidade", 0))+"%");
	    
	    confianca.setLayoutParams(innerParamsw1);
	    confianca.setGravity(Gravity.CENTER);
	    confianca.setTextColor(getResources().getColor(R.color.green));
	    confianca.setText(String.format("%.2f",attrs.getAttributeFloatValue(tenkel_xmls, "confianca", 0)));

        //add views to row
        this.addView(andar);
        this.addView(posicao);
        this.addView(probabilidade);
        this.addView(confianca);
	}



}
