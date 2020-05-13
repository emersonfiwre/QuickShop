package com.emersonlima.carrinhointeligente;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.domain.UsuarioBean;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;


public class ActPaymentCard extends AppCompatActivity implements Transaction {
    private Button btnDevolta;
    private ProgressBar progressBar;
    private ImageView image;
    private int codigoProduto;
    private int qtdeItem;
    private RequestQueue requestQueue = null;
    private Transaction transaction;
    private Drawer navigationDrawerLeft;
    private boolean isThereMore;
    private TextView tvGoodBye;
    private int contt = 0;
    private RelativeLayout layoutFinal;
    private boolean hadError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_payment_card);
        transaction = this;
        requestQueue = Volley.newRequestQueue(this);
        layoutFinal = findViewById(R.id.layout_card);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        image = (ImageView) findViewById(R.id.iv_finalizado);
        btnDevolta = (Button) findViewById(R.id.btn_volta_compras);
        tvGoodBye = (TextView) findViewById(R.id.tv_good_bye);


        btnDevolta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActPaymentCard.this, ActLogin.class);
                startActivity(intent);
                finish();
            }
        });

        navigationDrawerLeft = new DrawerBuilder()
                .withActivity(this)
                .build();
        isThereMore = false;
        (new VolleyConn(this, transaction, requestQueue)).execute();

        Utilitarios.landscape(this);
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
    }

    private void loading(boolean finish) {
        if (finish) {
            progressBar.setVisibility(View.GONE);
            tvGoodBye.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            btnDevolta.setVisibility(View.VISIBLE);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            tvGoodBye.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            btnDevolta.setVisibility(View.GONE);
        }
    }

    @Override
    public void doBefore() {
        loading(false);
    }

    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            loading(true);
            Log.i("LOG", "doAfter: " + answer);
            try {
                if (answer.equals("error") || answer.isEmpty()) {
                    hadError = true;
                    Utilitarios.setDialogError("Desculpe houve um erro!", "Erro inesperado, ouve retorno de erro", this);
                } else if (answer.equals("success")) {
                    Log.i("LOG", "doAfterSuccess: ");
                } else {
                    PedidosBean.setCodigo(Integer.parseInt(answer));
                    enviarItemPedido();
                }
            } catch (Exception e) {
                Log.i("LOG", "Exception(): " + e.getMessage());
                hadError = true;
                Utilitarios.setDialogError("Desculpe houve uma exceção ! ", e.getMessage(), this);
            } finally {
                loading(true);
            }
        }
    }

    @Override
    public RequestData getRequestData() {
        if (isThereMore) {
            RequestData rd = new RequestData(PedidosBean.PEDIDO_URL, "an-set-item_pedido", String.valueOf(qtdeItem));
            rd.setSecondParams(String.valueOf(String.valueOf(PedidosBean.getCodigo())));
            rd.setThirdParams(String.valueOf(codigoProduto));

            Log.i("LOG", "RequestQtdProd: " + String.valueOf(qtdeItem));
            Log.i("LOG", "RequestCodgiPedido: " + String.valueOf(PedidosBean.getCodigo()));
            Log.i("LOG", "RequestCodgiProd: " + String.valueOf(codigoProduto));

            return rd;

        } else {
            RequestData rd = new RequestData(PedidosBean.PEDIDO_URL, "an-set-pedido", String.valueOf(UsuarioBean.getCodigo()));
            Log.i("LOG", "RequestCodgiUSer:  " + String.valueOf(UsuarioBean.getCodigo()));

            isThereMore = true;
            Log.i("LOG", "isTheMore:  " + String.valueOf(isThereMore));
            return rd;
        }

    }

    @Override
    public void onBackPressed() {
        contt++;
        if (contt > 1) {
            super.onBackPressed();
        } else {
            Snackbar.make(layoutFinal, R.string.message_onBack, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.lbl_ok, null).show();
        }
    }
}
