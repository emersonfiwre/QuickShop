package com.emersonlima.carrinhointeligente;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;


public class Splash extends AppCompatActivity implements Transaction {
    private ImageView lg;
    private TextView title;
    private ProgressBar pv;
    private RelativeLayout lt;
    private String parametros = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        lg = (ImageView) findViewById(R.id.logo);
        Glide.with(this)
                .load(R.drawable.splash) // aqui é teu gif
                .into(lg);
        final Animation fade = AnimationUtils.loadAnimation(this, R.anim.transition);

        if (verificaConexao()) {
            alertGetIp();
        } else {
            (new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Erro dispositivo desconectado!")
                    .setMessage("Ligue o wifi do dispositivo para continuar!")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                              //ACTION_WIRELESS_SETTINGS
                            finish();
                        }
                    })
            ).show();
        }

        Utilitarios.landscape(this);
    }

    private void StartApp() {

        final Intent i = new Intent(this, ActLogin.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }

        };
        timer.start();
    }

    public boolean verificaConexao() {
        boolean conectado = false;
        ConnectivityManager conectivtyManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    //Método responsável de pegar o Endereço IPv4 utilizado no servidor local
    public void alertGetIp() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Endereço IPv4");
        final EditText input = new EditText(this);
        input.setText("http://192.168.43.150");
        alert.setView(input);
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utilitarios.BASE_URL = input.getText().toString().trim();
                Log.i("LOG", "BASE_URL(): " + Utilitarios.BASE_URL);
                isLandScape();
                (new VolleyConn(Splash.this, Splash.this, Volley.newRequestQueue(Splash.this))).execute();
                //StartApp();

            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                isLandScape();
                (new VolleyConn(Splash.this, Splash.this, Volley.newRequestQueue(Splash.this))).execute();
                //StartApp();
            }
        });
        alert.show();
    }

    public void isLandScape(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Deseja que a tela seja fixa ?");
        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utilitarios.islandScape = true;

            }
        });
        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utilitarios.islandScape = false;
            }
        });
        alert.show();
    }

    //Requisição a internet para ver se o servidor está respondendo e está tudo certo;
    @Override
    public void doBefore() {
    }

    @Override
    public void doAfter(String answer) {
        boolean fechaAct = false;
        if (answer != null) {
            Log.i("LOG", "doAfter: " + answer);
            try {
                if (answer.equals("error") || answer.isEmpty()) {
                    Utilitarios.setDialogError("Desculpe houve um erro!", "Erro inesperado, o Endereço IPV4 não respondendo respondendo corretamente!", this);
                    finish();
                    fechaAct = true;
                } else {
                    StartApp();
                }
            } catch (Exception e) {
                Log.i("LOG", "Exception(): " + e.getMessage());
                Utilitarios.setDialogError("Desculpe houve uma exceção !", "Exceção inesperado!", this);
                fechaAct = true;
            }
        } else {
            Utilitarios.setDialogError("Desculpe houve um erro no Endereço IPV4!", "Erro inesperado, o Endereço IPV4 não está ativo ou não está respondendo", this);
            fechaAct = true;
        }

        if (fechaAct) {
            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.i("LOG", "InterruptedException: " + e.getMessage());
                    } finally {
                        finish();
                    }
                }

            };
            timer.start();
        }

    }

    @Override
    public RequestData getRequestData() {
        Log.i("LOG: " + "RequestData", parametros);
        return (new RequestData(Utilitarios.BASE_URL + "/projetoQuickShop/package/util/Request.php", "request-ipv4", parametros));

    }
}
