package com.emersonlima.carrinhointeligente;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.fragments.AlimentosFragment;
import com.emersonlima.carrinhointeligente.fragments.BebidasFragment;
import com.emersonlima.carrinhointeligente.fragments.CarnesFragment;
import com.emersonlima.carrinhointeligente.fragments.DiscountFragment;
import com.emersonlima.carrinhointeligente.fragments.FrutasFragment;
import com.emersonlima.carrinhointeligente.fragments.HigieneFragmment;
import com.emersonlima.carrinhointeligente.fragments.LimpezaFragment;
import com.emersonlima.carrinhointeligente.fragments.MyListFragment;
import com.emersonlima.carrinhointeligente.fragments.PadariaFragment;
import com.emersonlima.carrinhointeligente.fragments.ProductFragment;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.carbs.android.library.MDDialog;

public class ActMain extends AppCompatActivity implements Transaction {
    private Drawer navigationDrawerLeft = null;
    private AccountHeader headerNavigationLeft;
    private Toolbar toolbar, footer;
    private RelativeLayout layoutActAllProducts;
    private int contt = 0;
    public TextView valorTotal;
    private FloatingActionButton fab;
    private String parametros;
    private Transaction transaction;
    private RequestQueue requestQueue;
    private ProdutosBean produtosBean = null;
    private ImageLoader mImageLoader;
    private MDDialog mdDialog;
    private MDDialog.Builder mdBuilder;
    private Fragment frag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        footer = (Toolbar) findViewById(R.id.include_footer);
        valorTotal = (TextView) findViewById(R.id.valor_total_footer);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.drawer_item_discount);
        toolbar.setLogo(R.mipmap.logo_sem_fundo);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(ActMain.this, (R.string.lbl_scanning_qr), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        transaction = this;
        requestQueue = Volley.newRequestQueue(this);
        mdBuilder = new MDDialog.Builder(this);

        valorTotal.setText(getString(R.string.valor_total) + " " + Utilitarios.formatarValor(PedidosBean.getValorTotal()));


        layoutActAllProducts = (RelativeLayout) findViewById(R.id.layout_act_all_products);
        headerNavigationLeft = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(false)
                .withHeaderBackground(R.drawable.back_navigation)
                .build();


        //Navigation Drawer
        navigationDrawerLeft = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(0)
                .withAccountHeader(headerNavigationLeft)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:
                                Toast.makeText(ActMain.this, position, Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                toolbar.setTitle(R.string.drawer_item_discount);
                                frag = new DiscountFragment();
                                break;
                            case 2:
                                toolbar.setTitle(R.string.drawer_item_all);
                                frag = new ProductFragment();
                                break;
                            case 3:
                                toolbar.setTitle(R.string.toolbar_title);
                                frag = new MyListFragment();
                                break;
                            case 5:
                                toolbar.setTitle(R.string.drawer_item_alimetos);
                                frag = new AlimentosFragment();
                                break;
                            case 6:
                                toolbar.setTitle(R.string.drawer_item_bebidas);
                                frag = new BebidasFragment();
                                break;
                            case 7:
                                toolbar.setTitle(R.string.drawer_item_carnes);
                                frag = new CarnesFragment();
                                break;
                            case 8:
                                toolbar.setTitle(R.string.drawer_item_padaria);
                                frag = new PadariaFragment();
                                break;
                            case 9:
                                toolbar.setTitle(R.string.drawer_item_frutas);
                                frag = new FrutasFragment();
                                break;
                            case 10:
                                toolbar.setTitle(R.string.drawer_item_higiene);
                                frag = new HigieneFragmment();
                                break;
                            case 11:
                                toolbar.setTitle(R.string.drawer_item_limpeza);
                                frag = new LimpezaFragment();
                                break;
                            default:
                                Toast.makeText(ActMain.this, position, Toast.LENGTH_LONG).show();
                                break;
                        }
                        onFragmentTransaction(frag);
                        return false;
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_discount), Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_all), Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_my_list), Toast.LENGTH_SHORT).show();
                                break;
                            case 5:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_alimetos), Toast.LENGTH_SHORT).show();
                                break;
                            case 6:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_bebidas), Toast.LENGTH_SHORT).show();
                                break;
                            case 7:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_carnes), Toast.LENGTH_SHORT).show();
                                break;
                            case 8:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_padaria), Toast.LENGTH_SHORT).show();
                                break;
                            case 9:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_frutas), Toast.LENGTH_SHORT).show();
                                break;
                            case 10:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_higiene), Toast.LENGTH_SHORT).show();
                                break;
                            case 11:
                                Toast.makeText(ActMain.this, (R.string.drawer_item_limpeza), Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                })
                .build();

        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_discount).withIcon(getResources().getDrawable(R.mipmap.ic_desconto_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_all).withIcon(getResources().getDrawable(R.mipmap.ic_store_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_my_list).withIcon(getResources().getDrawable(R.mipmap.ic_cesta_icon)));
        navigationDrawerLeft.addItem(new SectionDrawerItem().withName(R.string.drawer_item_selection));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_alimetos).withIcon(getResources().getDrawable(R.mipmap.ic_alimentos_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_bebidas).withIcon(getResources().getDrawable(R.mipmap.ic_bebidas_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_carnes).withIcon(getResources().getDrawable(R.mipmap.ic_carnes_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_padaria).withIcon(getResources().getDrawable(R.mipmap.ic_padaria_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_frutas).withIcon(getResources().getDrawable(R.mipmap.ic_frutav_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_higiene).withIcon(getResources().getDrawable(R.mipmap.ic_higiene_icon)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(R.string.drawer_item_limpeza).withIcon(getResources().getDrawable(R.mipmap.ic_limpeza_icon)));


        // FRAGMENT
        Fragment frag = (Fragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
        if (frag == null) {
            frag = new DiscountFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
            ft.commit();
        }

        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DO BANCO REMOTO
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.error)
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

        Utilitarios.landscape(this);

    }

    private void onFragmentTransaction(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
        ft.commit();
    }

    @Override
    protected void onResume() {
        valorTotal.setText(getString(R.string.valor_total) + " " + Utilitarios.formatarValor(PedidosBean.getValorTotal()));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        contt++;
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (navigationDrawerLeft != null && navigationDrawerLeft.isDrawerOpen()) {
                navigationDrawerLeft.closeDrawer();
            } else if (contt > 1) {
                super.onBackPressed();
            } else {
                Snackbar.make(layoutActAllProducts, R.string.message_onBack, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.lbl_ok, null).show();

            }
        } else {
            getFragmentManager().popBackStack();
        }


    }

    //BOTÃO PARA ADICIONAR PRODUTO E LER O QRCODE DO PRODUTO
    public void onClickScann(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Aponte para o QR-Code do produto.");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState = navigationDrawerLeft.saveInstanceState(outState);
        outState = headerNavigationLeft.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //VARÍAVEL QUE CONTÉM O RESULTADO DO QRCODE
        final IntentResult qrresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //TESTANDO SE O VALOR DO QRCODE FOI DIFERENTE DE VAZIO/NULL;
        if (qrresult != null) {
            //TESTANDO SE O CONTENSTS/ CONTÉUDO SEM SER OS "PARAMETROS" DO QR CODE.
            if (qrresult.getContents() == null) {
                //EXIBINDO UMA MENSAGEM NA TELA;
                Toast.makeText(this, "Você cancelou o Scan", Toast.LENGTH_LONG).show();

            } else {
                parametros = qrresult.getContents();
                (new VolleyConn(this, this, requestQueue)).execute();
            }
        } else {
            Utilitarios.setDialogError("Erro inesperado!", "Erro em tentar acessar  camêra do dispositivo", this);
        }


    }


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
    public void doBefore() {
        Utilitarios.setProgressBar(true, this);
    }

    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            try {
                JSONObject json = new JSONObject(answer);

                if (!json.isNull("products")) {
                    //isThereMore = json.getBoolean("isThereMore");
                    JSONArray ja = json.getJSONArray("products");
                    produtosBean = new ProdutosBean();

                    if (ja.length() < 1) {
                        Utilitarios.setDialogError("Desculpe!", "Esse produto não está cadastrado no sistema", this);
                    } else {
                        for (int i = 0, tam = ja.length(); i < tam; i++) {
                            JSONObject jProduct = ja.getJSONObject(i);

                            produtosBean.setCodigo(jProduct.getInt("cod_produto"));
                            produtosBean.setCodigo_busca(jProduct.getInt("cod_busca"));
                            produtosBean.setProd(jProduct.getString("nome_produto"));
                            produtosBean.setDesc(jProduct.getString("descricao_produto"));
                            produtosBean.setGenero(jProduct.getString("genero"));
                            produtosBean.setLocalizacao(jProduct.getString("localizacao"));
                            produtosBean.setValidade_produto(jProduct.getString("validade_produto"));
                            produtosBean.setValor_unit(jProduct.getDouble("valor_venda"));
                            produtosBean.setPhoto(jProduct.getString("photo_prod"));


                        }
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
                                        adcionarItem(produtosBean);
                                        toolbar.setTitle(R.string.toolbar_title);
                                        frag = new MyListFragment();
                                        onFragmentTransaction(frag);
                                    }
                                });
                                localizar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Utilitarios.showImageDialog(produtosBean.getLocalizacao(), ActMain.this);
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
                } else {
                    Utilitarios.setDialogError("Erro", "Objeto fornecido é nullo", this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("LOG", "JSONException(): " + e.getMessage());
                Utilitarios.setDialogError("Erro", "JSONException " + e.getMessage(), this);

            } finally {
                Utilitarios.setProgressBar(false, this);
            }
        } else {
            Utilitarios.setProgressBar(false, this);
            Utilitarios.setDialogError("ERROR 404 NOT FOUND", "O servidor requisitado pode estar inativo ou a url requisitada não existe!", this);
        }

    }

    @Override
    public RequestData getRequestData() {
        return (new RequestData(ProdutosBean.PRODUCT_URL, "an-get-product", parametros));
    }

    //MÉTODO RESPONSAVÉL POR ADICIONAR ITENS NA LISTA;
    private void adcionarItem(ProdutosBean pb) {
        //ADICIONANDO O ITEM NO ADAPTER PARA ALIMENTAR A RECYCLERVIEW
        PedidosBean.setProduto(produtosBean);
        Utilitarios.atualizaValorTotal(this, Utilitarios.formatarValor(PedidosBean.getValorTotal()));
        mdDialog.dismiss();
        Snackbar.make(layoutActAllProducts, getString(R.string.sucess_add_prod), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.lbl_ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        frag = new MyListFragment();
                        onFragmentTransaction(frag);
                    }
                }).show();

    }

}