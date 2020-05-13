package com.emersonlima.carrinhointeligente;


import android.app.SearchManager;
import android.content.Context;

import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.carbs.android.library.MDDialog;

public class ActDetails extends AppCompatActivity {
    Toolbar mToolbar;
    ImageView ivImg;
    TextView tvDescription, tvValue, tvName;
    private ImageLoader mImageLoader;
    private ProdutosBean produtosBean;
    private Drawer navigationDrawerLeft;
    Button btnLocation;
    private CoordinatorLayout layoutActDetails;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ViewGroup mRoot;
    private MDDialog mdDialog;
    private MDDialog.Builder mdBuilder;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_details);

        if (savedInstanceState != null) {
            produtosBean = savedInstanceState.getParcelable("prod");
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("prod") != null) {
                produtosBean = getIntent().getExtras().getParcelable("prod");
            } else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DO BANCO REMOTO
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.retry_img)
                .showImageOnLoading(R.drawable.loading_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DA ACTIVITY ANTERIOR
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(5)
                .writeDebugLogs()
                .build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(produtosBean.getProd());


        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(produtosBean.getProd());
        setSupportActionBar(mToolbar);

        mdBuilder = new MDDialog.Builder(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);


        mRoot = (ViewGroup) findViewById(R.id.ll_tv_description);
        layoutActDetails = (CoordinatorLayout) findViewById(R.id.layout_act_details);
        ivImg = (ImageView) findViewById(R.id.ivImg);
        tvName = (TextView) findViewById(R.id.tvName);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvValue = (TextView) findViewById(R.id.tvValue);
        btnLocation = (Button) findViewById(R.id.btn_location);
        mImageLoader.displayImage(produtosBean.getPhoto(), ivImg);
        tvName.setText(produtosBean.getProd());
        tvValue.setText(Utilitarios.formatarValor(produtosBean.getValor_unit()));
        tvDescription.setText(produtosBean.getDesc());

        navigationDrawerLeft = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(false)
                .withCloseOnClick(true)
                .withActionBarDrawerToggleAnimated(false)
                .withActionBarDrawerToggle(new ActionBarDrawerToggle(this, new DrawerLayout(this), R.string.drawer_open, R.string.drawer_close) {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        navigationDrawerLeft.closeDrawer();
                        finish();
                    }
                })
                .build();

        Utilitarios.landscape(this);
    }

    public void addProdDialog(View view) {
        mdBuilder.setCancelable(true);
        mdBuilder.setContentView(R.layout.dialog_produto);
        mdBuilder.setShowButtons(true);
        mdBuilder.setContentViewOperator(new MDDialog.ContentViewOperator() {
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
                mImageLoader.displayImage(produtosBean.getPhoto(), image);
                nome.setText(produtosBean.getProd());
                valor.setText(Utilitarios.formatarValor(produtosBean.getValor_unit()));
                //SE ELE DESEJAR ADICIONAR O PRODUTO A SUA LISTA, IREMOS DÁ UM TOAST PARA FALAR QUE FOI ADICIONADO COM SUCESSO SE O PROCESSO OCORREU
                //CORETAMENTE

                adicionar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ADICIONANDO O PRODUTO NA LISTA
                        PedidosBean.setProduto(produtosBean);
                        mdDialog.dismiss();
                        Snackbar.make(layoutActDetails, getString(R.string.sucess_add_prod), Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.lbl_ok), null).show();
                    }
                });
                localizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utilitarios.showImageDialog(produtosBean.getLocalizacao(), ActDetails.this);
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
        });
        mdBuilder.setShowTitle(false);
        mdBuilder.setWidthMaxDp(320);//560
        mdBuilder.setShowButtons(false);

        mdDialog = mdBuilder.create();
        mdDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    //Criação para realizar busca dos produtos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_main, menu);

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


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("prod", produtosBean);
    }

    public void btnLocation(View view) {
        Utilitarios.showImageDialog(produtosBean.getLocalizacao(), ActDetails.this);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(mRoot, new Slide());
            tvDescription.setVisibility(View.INVISIBLE);
        }

        super.onBackPressed();
    }

    public void enlargeImage(View view) {
        Utilitarios.showImageDialog(produtosBean.getPhoto(), ActDetails.this);

    }


}
