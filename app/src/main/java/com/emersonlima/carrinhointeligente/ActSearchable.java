package com.emersonlima.carrinhointeligente;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.adapters.CardViewAdapter;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.interfaces.RecyclerViewOnClickListenerHack;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cn.carbs.android.library.MDDialog;


public class ActSearchable extends AppCompatActivity implements RecyclerViewOnClickListenerHack, Transaction {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<ProdutosBean> mList;
    private CardViewAdapter adapter;
    private CoordinatorLayout clContainer;
    private String parametros = null;
    private boolean isThereMore = false;
    private ProdutosBean produtosBean = null;
    private ImageLoader mImageLoader;
    private MDDialog mdDialog;
    private MDDialog.Builder mdBiulder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DO BANCO REMOTO
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.retry_img)
                .showImageOnLoading(R.drawable.loading_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //Parametrizando lib necessaria para carregar imagens remotas;
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(5)
                .writeDebugLogs()
                .build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList("mList");
        } else {
            mList = new ArrayList<>();
        }

        clContainer = (CoordinatorLayout) findViewById(R.id.cl_container);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager llm = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        //llm.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        adapter = new CardViewAdapter(this, mList, mImageLoader);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        hendleSearch(getIntent());
        mdBiulder = new MDDialog.Builder(this);

        Utilitarios.landscape(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        hendleSearch(intent);
    }

    public void hendleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);

            mToolbar.setTitle(q);
            mList.clear();
            parametros = q.trim();
            (new VolleyConn(this, this, Volley.newRequestQueue(this))).execute();

        }
    }


    public void filterProds() {
        mRecyclerView.setVisibility(mList.isEmpty() ? View.GONE : View.VISIBLE);
        if (mList.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Nenhum produto com este nome encontrado.");
            tv.setTextColor(getResources().getColor( R.color.colorPrimarytext));
            tv.setId(1);
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);

            clContainer.addView(tv);
        } else if (clContainer.findViewById(1) != null) {
            clContainer.removeView(clContainer.findViewById(1));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<ProdutosBean>) mList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) item.getActionView();
        } else {
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        return true;
    }

    // LISTENERS
    @Override
    public void onClickListener(View view, int position) {
        Log.i("LOG","onClickListener");
        Intent intent = new Intent(this, ActDetails.class);
        intent.putExtra("prod", mList.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, final int position) {
        Log.i("LOG","onLongPressClickListener");
        mdBiulder.setCancelable(true)
                .setContentView(R.layout.dialog_produto)
                .setShowButtons(true)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        //Refenciando as Views do layout
                        TextView nome = (TextView) contentView.findViewById(R.id.tvNome);
                        TextView valor = (TextView) contentView.findViewById(R.id.tvValor);
                        ImageView image = (ImageView) contentView.findViewById(R.id.imageProd);
                        Button adicionar = (Button) contentView.findViewById(R.id.btn_adicionar);
                        Button cancelar = (Button) contentView.findViewById(R.id.btn_cancelar);
                        Button localizar = (Button) contentView.findViewById(R.id.btn_localizar);

                        //setando o valor nas Views
                        mImageLoader.displayImage(mList.get(position).getPhoto(), image);
                        nome.setText(mList.get(position).getProd());
                        valor.setText(Utilitarios.formatarValor(mList.get(position).getValor_unit()));
                        //SE ELE DESEJAR ADICIONAR O PRODUTO A SUA LISTA, IREMOS DÁ UM TOAST PARA FALAR QUE FOI ADICIONADO COM SUCESSO SE O PROCESSO OCORREU
                        //CORETAMENTE
                        adicionar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //ADICIONANDO O PRODUTO NA LISTA
                                PedidosBean.setProduto(mList.get(position));
                                mdDialog.dismiss();
                                Snackbar.make(clContainer, getString(R.string.sucess_add_prod), Snackbar.LENGTH_SHORT)
                                        .setAction(getString(R.string.lbl_ok), null);
                            }
                        });
                        localizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Utilitarios.showImageDialog(mList.get(position).getLocalizacao(),ActSearchable.this);
                            }
                        });
                        //SE ELE NÃO DESEJAR O PRODUTO (CANCELAR), SÓ IREMOS DA UM RETORN PARA ELE CONTINUAR AS COMPRAS
                        cancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mdDialog.dismiss();
                            }
                        });
                    }
                })
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setShowTitle(false)
                .setWidthMaxDp(320)//560
                .setShowButtons(false);

        mdDialog = mdBiulder.create();
        mdDialog.show();
    }

    @Override
    public void doBefore() {
        Utilitarios.setProgressBar(true, this);
    }

    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            try {
                JSONObject json = new JSONObject(answer);

                if (!json.isNull("products")) {
                    isThereMore = json.getBoolean("isThereMore");
                    JSONArray ja = json.getJSONArray("products");
                    Log.i("LOG", String.valueOf(ja.length()));
                    if (ja.length() < 1) {
                        Utilitarios.setProgressBar(false, this);
                    } else {
                        for (int i = 0, tam = ja.length(); i < tam; i++) {
                            JSONObject jProduct = ja.getJSONObject(i);

                            produtosBean = new ProdutosBean();

                            produtosBean.setCodigo(jProduct.getInt("cod_produto"));
                            produtosBean.setCodigo_busca(jProduct.getInt("cod_busca"));
                            produtosBean.setProd(jProduct.getString("nome_produto"));
                            produtosBean.setDesc(jProduct.getString("descricao_produto"));
                            produtosBean.setGenero(jProduct.getString("genero"));
                            produtosBean.setLocalizacao(jProduct.getString("localizacao"));
                            produtosBean.setValidade_produto(jProduct.getString("validade_produto"));
                            produtosBean.setValor_unit(jProduct.getDouble("valor_venda"));
                            produtosBean.setPhoto(jProduct.getString("photo_prod"));

                            mList.add(produtosBean);
                            adapter.notifyItemInserted(mList.size());



                        }
                    }
                } else {
                    Utilitarios.setDialogError("Erro", "Objeto fornecido é nullo",this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("LOG", "JSONException(): " + e.getMessage());

            } finally {

                Utilitarios.setProgressBar(false, this);

            }
        } else {
            Utilitarios.setProgressBar(false, this);
        }

        filterProds();
    }

    @Override
    public RequestData getRequestData() {
        Log.i("LOG", "RequestData: " + parametros );
        return (new RequestData(ProdutosBean.PRODUCT_URL, "search-product", parametros ));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
