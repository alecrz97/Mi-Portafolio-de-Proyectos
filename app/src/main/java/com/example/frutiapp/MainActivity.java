package com.example.frutiapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity extends AppCompatActivity {

    private EditText et_nombre;
    private ImageView iv_personaje;
    private TextView tv_bestScore;
    private MediaPlayer mp;

    int num_aleatorio = (int) (Math.random()* 10);


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        et_nombre = findViewById(R.id.txt_nombre);
        iv_personaje = findViewById(R.id.imageView_personaje);
        tv_bestScore = findViewById(R.id.txt_BestScore);





        if(num_aleatorio == 0 || num_aleatorio == 10) {
            iv_personaje.setImageResource(R.drawable.mango);
        } else if(num_aleatorio == 1 || num_aleatorio == 9){
            iv_personaje.setImageResource(R.drawable.fresa);

        }else if(num_aleatorio == 2 || num_aleatorio == 8) {
            iv_personaje.setImageResource(R.drawable.manzana);

        }else if(num_aleatorio == 3 || num_aleatorio == 7) {
            iv_personaje.setImageResource(R.drawable.sandia);

        }else if(num_aleatorio == 4 ||  num_aleatorio == 5 || num_aleatorio == 6) {
            iv_personaje.setImageResource(R.drawable.uva);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"BD",null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();

         @SuppressLint("Recycle") Cursor consulta = BD.rawQuery(
               "select * from puntaje where score = (select max(score) from puntaje) ", null);
         if(consulta.moveToFirst()){
             String temp_nombre = consulta.getString(0);
             String temp_score = consulta.getString(1);

             tv_bestScore.setText("Record: " + temp_score + " de " + temp_nombre);
             BD.close();
         }else{
             BD.close();
         }
        mp = MediaPlayer.create(this,R.raw.alphabet_song);
        mp.start();
        mp.setLooping(true);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // No hacer nada
            }
        });
    }

    public void Jugar(View view){
        String nombre = et_nombre.getText().toString();
        if(!nombre.isEmpty()){
            mp.stop();
            mp.release();

            Intent intent = new Intent(this, MainActivity2_nivel1.class);
            intent.putExtra("jugador", nombre);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Primero debes escribir tu nombre", Toast.LENGTH_SHORT).show();
            et_nombre.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_nombre, InputMethodManager.SHOW_IMPLICIT);
        }
    }


}