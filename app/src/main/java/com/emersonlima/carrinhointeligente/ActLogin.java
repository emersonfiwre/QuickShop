package com.emersonlima.carrinhointeligente;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.domain.UsuarioBean;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.emersonlima.carrinhointeligente.utils.ValidaCPF;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import br.com.sapereaude.maskedEditText.MaskedEditText;

public class ActLogin extends AppCompatActivity implements Transaction {

    private MaskedEditText edt_cpf;
    private int contt = 0;
    private RelativeLayout layoutLogin;
    private MaterialFancyButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_login);

        edt_cpf = (MaskedEditText) findViewById(R.id.edt_cpf);
        login = (MaterialFancyButton) findViewById(R.id.btn_login);

        layoutLogin = findViewById(R.id.layout_login);
        LinearLayout form = (LinearLayout) findViewById(R.id.formlogin);
        android.view.animation.Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_bottom);
        form.setAnimation(animation);

        PedidosBean.setValorTotal(0);
        PedidosBean.getListProduto().clear();
        UsuarioBean.setCodigo(0);
        UsuarioBean.setCpf("0");
        edt_cpf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    MessageRegister();
                }
                return false;
            }
        });

        Utilitarios.landscape(this);

    }

    public void Login(View view) {
        MessageRegister();
    }

    public void MessageRegister() {
        Log.i("LOG", "getRawText() " + edt_cpf.getRawText());
        if (ValidaCPF.isCPF((edt_cpf.getRawText()).trim())) {

            UsuarioBean.setCpf(edt_cpf.getRawText().trim());
            (new VolleyConn(ActLogin.this, ActLogin.this, Volley.newRequestQueue(ActLogin.this))).execute();
        } else {
            new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.darkred)
                    .setButtonsColorRes(R.color.darkblue)
                    .setIcon(R.mipmap.error)
                    .setTitle("Por Favor!")
                    .setMessage("Por favor, digite um CPF válido")
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        }
    }


    @Override
    public void doBefore() {
        Utilitarios.setProgressBar(true, this);
    }

    @Override
    public void doAfter(String answer) {

        if (answer != null) {
            Log.i("LOG", "doAfter: " + answer);
            try {
                if (answer.equals("error") || answer.isEmpty()) {
                    startActivity(new Intent(this, ActIntroduction.class));
                    finish();
                } else {
                    UsuarioBean.setCodigo(Integer.parseInt(answer));
                    startActivity(new Intent(this, ActMain.class));
                    finish();
                }
            } catch (Exception e) {
                Log.i("LOG", "Exception(): " + e.getMessage());
                Utilitarios.setProgressBar(false, this);
                Utilitarios.setDialogError("Desculpe houve uma exceção !", "Exceção inesperado!", this);
            }
        } else {
            Utilitarios.setProgressBar(false, this);
            Utilitarios.setDialogError("Desculpe erro inesperado", "Erro inesperado, o Endereço IPV4 não está ativo ou não está respondendo", this);
        }

    }

    @Override
    public RequestData getRequestData() {
        Log.i("LOG: " + "RequestData", edt_cpf.getRawText().trim());
        Utilitarios.setProgressBar(true, this);
        login.setEnabled(false);
        return (new RequestData(UsuarioBean.USER_URL, "an-get-user", edt_cpf.getRawText().trim()));
    }

    @Override
    public void onBackPressed() {
        contt++;
        if (contt > 1) {
            super.onBackPressed();
        } else {
            Snackbar.make(layoutLogin, R.string.message_onBack, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.lbl_ok, null).show();
        }
    }
}
