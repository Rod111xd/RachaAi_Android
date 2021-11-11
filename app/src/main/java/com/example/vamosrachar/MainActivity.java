package com.example.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, TextToSpeech.OnInitListener {

    EditText valueMoney, valuePeople;
    TextView resultText;
    FloatingActionButton actionShare, actionSpeak;
    TextToSpeech ttsPlayer;
    boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valueMoney = (EditText) findViewById(R.id.valueMoney);
        valuePeople = (EditText) findViewById(R.id.valuePeople);
        resultText = (TextView) findViewById(R.id.resultText);
        actionShare = (FloatingActionButton) findViewById(R.id.actionShare);
        actionSpeak = (FloatingActionButton) findViewById(R.id.actionSpeak);

        valueMoney.addTextChangedListener(this);
        valuePeople.addTextChangedListener(this);
        actionShare.setOnClickListener(this);
        actionSpeak.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        try {
            startActivityForResult(checkTTSIntent, 1122);
        }catch(Exception e) {
            Log.v("A", e.toString());
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1122) {
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ttsPlayer = new TextToSpeech(this, this);
            }else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    protected String paraFala(String valor) {
        String[] partes = valor.split(",");
        String result;

        result = partes[0];

        if(!partes[1].equals("00")) {
            result += " e " + partes[1] + " centavos";
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        if(view==actionShare) {
            if(valid) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Cada pessoa pagará " + resultText.getText().toString() + ".");
                startActivity(intent);
            }
        }
        if(view==actionSpeak) {
            if(valid) {
                if (ttsPlayer != null) {
                    ttsPlayer.speak("Cada pessoa pagará " + paraFala(resultText.getText().toString()), TextToSpeech.QUEUE_FLUSH, null, "ID1");
                }
            }
        }
    }

    protected void calculo() {
        DecimalFormat df = new DecimalFormat("0.00");

        Double valorDinheiro = 0.0;
        int numPessoas = 0;

        try {
            valorDinheiro = Double.parseDouble(valueMoney.getText().toString());
        }catch (Exception e) {
            resultText.setText("Dinheiro inválido");
            resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            valid = false;
            return;
        }

        try {
            numPessoas = Integer.parseInt(valuePeople.getText().toString());
        }catch (Exception e) {
            resultText.setText("Número de pessoas inválido");
            resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            valid = false;
            return;
        }

        if(numPessoas != 0) {
            Double result = valorDinheiro / numPessoas;

            String resFormat = df.format(result);
            resultText.setText("R$" + resFormat.replace(".", ","));
            resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP,48);
            valid = true;
        }else {
            resultText.setText("Alguém precisa pagar a conta");
            resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            valid = false;
        }
    }

    @Override
    public void onInit(int initStatus) {
        if(initStatus == TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS ativado...", Toast.LENGTH_LONG).show();
        }else if(initStatus == TextToSpeech.ERROR){
            Toast.makeText(this, "Sem TTS habilitado...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable edit) {

        calculo();

    }


}