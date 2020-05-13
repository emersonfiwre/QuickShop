package com.emersonlima.carrinhointeligente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.domain.UsuarioBean;
import com.emersonlima.carrinhointeligente.fragments.CustomSlide;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class ActIntroduction extends MaterialIntroActivity implements Transaction {
    private int contt = 0;
    LovelyStandardDialog lovelyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Adicionando os slides para introdução
        CreateSlides();
        addSlide(new CustomSlide());
        lovelyDialog = new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL);


    }

    private void CreateSlides() {
        addSlide(new SlideFragmentBuilder()
                .title(getString(R.string.Welcome))
                .description(getString(R.string.Welcometxt))
                .buttonsColor(R.color.slide_button)
                .image(R.drawable.hello)
                .backgroundColor(R.color.white)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title(getString(R.string.Security))
                .description(getString(R.string.Securitytxt))
                .buttonsColor(R.color.slide_button)
                .image(R.drawable.secure)
                .backgroundColor(R.color.white)
                .build());


        addSlide(new SlideFragmentBuilder()
                .title(getString(R.string.Enjoy))
                .description(getString(R.string.Enjoytxt))
                .buttonsColor(R.color.slide_button)
                .image(R.drawable.welcome)
                .backgroundColor(R.color.white)
                .build());

        Utilitarios.landscape(this);
    }

    @Override
    public void onBackPressed() {
        contt++;
        if (contt > 1) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.message_onBack, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onFinish() {
        (new VolleyConn(ActIntroduction.this, ActIntroduction.this, Volley.newRequestQueue(ActIntroduction.this))).execute();
    }


    @Override
    public void doBefore() {
        Utilitarios.setProgressBar(true, this);
    }

    //Tratamento da resposta do servidor
    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            Log.i("LOG", "doAfter: " + answer);
            try {
                if (answer.equals("error") || answer.isEmpty()) {
                    Utilitarios.setDialogError("Desculpe houve um erro!", "Erro na persistencia do cadastro do usuario, por favor tente mais tarde!", this);
                } else {
                    UsuarioBean.setCodigo(Integer.parseInt(answer));
                }
            } catch (Exception e) {
                Log.i("LOG", "Exception(): " + e.getMessage());
                Utilitarios.setProgressBar(false, this);
                Utilitarios.setDialogError("Desculpe houve uma exceção !", "Exceção inesperado!", this);
            } finally {
                Utilitarios.setProgressBar(false, this);
                lovelyDialog.setTopColorRes(R.color.green_300)
                        .setButtonsColorRes(R.color.darkblue)
                        .setIcon(R.mipmap.succes)
                        .setCancelable(false)
                        .setTitle("Bem-vindo")
                        .setMessage("Seu cadastro foi concluido com sucesso")
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(ActIntroduction.this, ActMain.class));
                                //finish();
                            }
                        }).show();
            }
        } else {
            Utilitarios.setProgressBar(false, this);
            Utilitarios.setDialogError("Desculpe houve um erro no Endereço IPV4!", "Erro inesperado, o Endereço IPV4 não está ativo ou não está respondendo", this);
        }

    }
    //Mandando verificação para ver se o usuario ja é cadastrado
    @Override
    public RequestData getRequestData() {
        Log.i("LOG: " + "RequestData", String.valueOf(UsuarioBean.getCpf()));
        return (new RequestData(UsuarioBean.USER_URL, "an-set-user", UsuarioBean.getCpf()));
    }

    //sobreescrita, pois a tela insistia em fechar automaticamente
    @Override
    public void finish() {
        if (lovelyDialog == null) {
            super.finish();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        lovelyDialog.dismiss();
        lovelyDialog = null;
        super.startActivity(intent);
        finish();

    }
}


