package com.example.frutiapp;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity2_Nivel4 extends AppCompatActivity {
    private TextView tv_nombre,tv_score;
    private ImageView iv_Auno, iv_Ados, iv_vidas,iv_signo;
    private EditText et_respuesta;
    private MediaPlayer mp ,mp_great,mp_bad;

    int score, numAleatorio_uno,numAleatorio_dos,resultado, vidas = 3;
    String nombre_jugador, string_score, string_vidas;
    String numero [] = {"cero","uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_activity2_nivel4);

        Toast.makeText(this, "Nivel 4 - Sumas y Restas", Toast.LENGTH_SHORT).show();

        tv_nombre = findViewById(R.id.textView_nombre);
        tv_score = findViewById(R.id.textView_score);
        iv_vidas = findViewById(R.id.imageView_vidas);
        iv_Auno =  findViewById(R.id.imageView_NumUno);
        iv_Ados = findViewById(R.id.imageView_NumDos);
        iv_signo = findViewById(R.id.imageView_signo);
        et_respuesta = findViewById(R.id.editText_resultado);

        nombre_jugador = getIntent().getStringExtra("jugador");
        tv_nombre.setText("Jugador: " + nombre_jugador);


        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        tv_score.setText("Score: " + score);

        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if(vidas == 3){
            iv_vidas.setImageResource(R.drawable.tresvidas);
        }if(vidas == 2){
            iv_vidas.setImageResource(R.drawable.dosvidas);
        }if(vidas == 1){
            iv_vidas.setImageResource(R.drawable.unavida);
        }


        mp = MediaPlayer.create(this,R.raw.goats);
        mp.start();
        mp.setLooping(true);

        mp_great = MediaPlayer.create(this,R.raw.wonderful);
        mp_bad = MediaPlayer.create(this,R.raw.bad);

        NumAleatorio();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }


    public  void Comparar(View view){

        String respuesta = et_respuesta.getText().toString();
        if (!respuesta.equals("")){
            int respuesta_jugador = Integer.parseInt(respuesta);
            if(respuesta_jugador == resultado){
                mp_great.start();
                score++;
                tv_score.setText("Score: " + score);
                et_respuesta.setText("");
                BaseDeDatos();
            }else{
                mp_bad.start();
                vidas--;
                BaseDeDatos();
                switch (vidas){
                    case 3:
                        iv_vidas.setImageResource(R.drawable.tresvidas);
                        break;
                    case 2:
                        Toast.makeText(this, "Te quedan 2 manzanas", Toast.LENGTH_SHORT).show();
                        iv_vidas.setImageResource(R.drawable.dosvidas);
                        break;
                    case 1:
                        Toast.makeText(this, "Te queda 1 manzana", Toast.LENGTH_SHORT).show();
                        iv_vidas.setImageResource(R.drawable.unavida);
                        break;
                    case 0:
                        Toast.makeText(this, "Has perdido todas tus manzanas", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        mp.release();
                        break;
                }

                et_respuesta.setText("");
            }

            NumAleatorio();

        }else{
            Toast.makeText(this, "Escribe tu respuesta", Toast.LENGTH_SHORT).show();
        }

    }
    //metodo para crear numeros aleatorios

    public void NumAleatorio() {
        if (score <= 39) {

            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);

            if (numAleatorio_uno >= 0 && numAleatorio_uno <=4) {
                resultado = numAleatorio_uno + numAleatorio_dos;
                iv_signo.setImageResource(R.drawable.adicion);
            }else{

                resultado = numAleatorio_uno - numAleatorio_dos;
                iv_signo.setImageResource(R.drawable.resta);
            }

            if (resultado >= 0) {
                for (int i = 0; i < numero.length; i++) {

                    int id = getResources().getIdentifier(numero[i], "drawable", getPackageName());
                    if (numAleatorio_uno == i) {

                        iv_Auno.setImageResource(id);

                    }
                    if (numAleatorio_dos == i) {

                        iv_Ados.setImageResource(id);

                    }
                }
            } else {

                NumAleatorio();

            }





        }else{

            Intent intent = new Intent(this,MainActivity2_Nivel5.class);
            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("jugador",nombre_jugador);
            intent.putExtra("score", string_score);
            intent.putExtra("vidas", string_vidas);

            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
            mp_bad.release();
            mp_great.release();


        }
    }

    public void BaseDeDatos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"BD",null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery("select * from puntaje where score = (select max(score)from puntaje)", null);
        if(consulta.moveToFirst()){

            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);

            int bestScore = Integer.parseInt(temp_score);
            if (score > bestScore){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre",nombre_jugador);
                modificacion.put("score",score);

                BD.update("puntaje", modificacion,"score=" + bestScore , null);
            }
        }else{

            ContentValues insertar = new ContentValues();
            insertar.put("nombre",nombre_jugador);
            insertar.put("score",score);

            BD.insert("puntaje",null, insertar);
            BD.close();
        }

    }
}
