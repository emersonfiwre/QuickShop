package com.emersonlima.carrinhointeligente;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;

import java.util.Random;

public class ActPaymentMoney extends AppCompatActivity implements Transaction {
    private RelativeLayout layoutMoney;
    private Transaction transaction;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private TextView tvCodigo, tvCaixa, tvAgradece;
    private Button btnVoltar;
    private LinearLayout llCodgio;
    private int codigoCaixa, codigoProduto, qtdeItem;
    private boolean hadError;
    private int contt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_payment_money);
        transaction = this;
        requestQueue = Volley.newRequestQueue(this);

        layoutMoney = findViewById(R.id.layout_money);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        tvCodigo = (TextView) findViewById(R.id.tv_codigo);

        btnVoltar = findViewById(R.id.btn_volta_compras);
        llCodgio = findViewById(R.id.ll_codigo);
        tvCaixa = findViewById(R.id.textView);
        tvAgradece = findViewById(R.id.textView2);

        gerarCodigo();
        enviarItemPedido();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActPaymentMoney.this, ActLogin.class));
                finish();
            }
        });

        Utilitarios.landscape(this);

    }

    private void gerarCodigo() {
        //instância um objeto da classe Random usando o construtor padrão
        Random gerador = new Random();
        //imprime sequência de 5 números inteiros aleatórios
        codigoCaixa = gerador.nextInt(99999);
    }

    private void enviarItemPedido() {

        for (int i = 0; i <= PedidosBean.getListProduto().size() - 1; i++) {
            if (!hadError) {
                codigoProduto = PedidosBean.getListProduto().get(i).getCodigo();
                qtdeItem = PedidosBean.getListProduto().get(i).getQntd();

                Log.i("LOG", "enviarItemPedido(): " + String.valueOf(PedidosBean.getListProduto().get(i).getCodigo()));
                (new VolleyConn(this, transaction, requestQueue)).execute();
            } else {
                break;
            }
        }
        loading(true);
        tvCodigo.setText(String.valueOf(codigoCaixa));
    }

    @Override
    public void doBefore() {
        this.loading(false);
    }

    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            Log.i("LOG", "doAfter: " + answer);
            try {
                if (answer.equals("error") || answer.isEmpty()) {
                    hadError = true;
                    Utilitarios.setDialogError("Desculpe houve um erro!", "Erro inesperado, ouve retorno de erro", this);
                } else if (answer.equals("success")) {
                    Log.i("LOG", "doAfterSuccess: ");
                }
            } catch (Exception e) {
                Log.i("LOG", "Exception(): " + e.getMessage());
                hadError = true;
                Utilitarios.setDialogError("Desculpe houve uma exceção ! ", "Erro inesperado, ouve uma exceção", this);
            }
        } else {
            hadError = true;
            Utilitarios.setDialogError("Desculpe houve um erro!", "Erro inesperado, ouve retorno de nullo", this);
        }

    }

    @Override
    public RequestData getRequestData() {
        this.loading(false);
        RequestData rd = new RequestData(PedidosBean.PEDIDO_URL, "an-set-is_money", String.valueOf(qtdeItem));
        rd.setSecondParams(String.valueOf(codigoCaixa));
        rd.setThirdParams(String.valueOf(codigoProduto));
        return rd;
    }

    private void loading(boolean finish) {
        if (finish) {
            progressBar.setVisibility(View.GONE);
            llCodgio.setVisibility(View.VISIBLE);
            tvCaixa.setVisibility(View.VISIBLE);
            tvAgradece.setVisibility(View.VISIBLE);
            btnVoltar.setVisibility(View.VISIBLE);
            tvCodigo.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            llCodgio.setVisibility(View.GONE);
            tvCaixa.setVisibility(View.GONE);
            tvAgradece.setVisibility(View.GONE);
            btnVoltar.setVisibility(View.GONE);
            tvCodigo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        contt++;
        if (contt > 1) {
            super.onBackPressed();
        } else {
            Snackbar.make(layoutMoney, R.string.message_onBack, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.lbl_ok, null).show();
        }
    }
}
