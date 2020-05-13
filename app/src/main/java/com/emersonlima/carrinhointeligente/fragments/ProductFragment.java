package com.emersonlima.carrinhointeligente.fragments;


import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.emersonlima.carrinhointeligente.ActDetails;
import com.emersonlima.carrinhointeligente.R;
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

//Classe pai dos fragmentos de genero
public class ProductFragment extends Fragment implements RecyclerViewOnClickListenerHack, Transaction {
    protected static final String TAG = "LOG";
    protected RecyclerView mRecyclerView;
    protected List<ProdutosBean> mList;
    protected ImageLoader mImageLoader;
    protected ProdutosBean produtosBean = null;
    protected int parametros = 0;
    protected boolean isThereMore = false;
    protected RequestQueue requestQueue = null;
    protected Transaction transaction;
    protected FrameLayout layoutFragment;
    protected MDDialog mdDialog;
    protected MDDialog.Builder mdBiulder;
    protected CardViewAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transaction = this;
        requestQueue = Volley.newRequestQueue(getActivity());

        if (savedInstanceState != null) {
            mList = savedInstanceState.getParcelableArrayList("mList");
        } else {
            mList = new ArrayList<>();
            (new VolleyConn(getActivity(), transaction, requestQueue)).execute();
        }
        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DO BANCO REMOTO
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.retry_img)
                .showImageOnLoading(R.drawable.loading_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //Parametrizando lib necessaria para carregar imagens remotas;
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(mDisplayImageOptions)
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(5)
                .writeDebugLogs()
                .build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        layoutFragment = (FrameLayout) view.findViewById(R.id.frame_product);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mdBiulder = new MDDialog.Builder(getActivity());


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            //Metodo responsável por pegar interação do usuario com scroll
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager llm = (GridLayoutManager) mRecyclerView.getLayoutManager();
                //Fazendo uma nova requisiçãoao servidor para pegar mais dados para serem exibidos
                if (llm.findLastCompletelyVisibleItemPosition() + 1 == mList.size() && isThereMore) {
                    parametros = mList.get(mList.size() - 1).getCodigo();
                    isThereMore = false;
                    Log.i("LOG", "onScrolled:" + String.valueOf(parametros));
                    (new VolleyConn(getActivity(), transaction, requestQueue)).execute();

                }


            }
        });
        GridLayoutManager llm = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);

        adapter = new CardViewAdapter(getActivity(), mList, mImageLoader);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        Utilitarios.landscape(getActivity());

        return view;
    }

    @Override
    public void doBefore() {
        Utilitarios.setProgressBar(true, getActivity());

    }

    //Após receber a resposta do servidor
    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            try {
                JSONObject json = new JSONObject(answer);
                if (!json.isNull("products")) {
                    CardViewAdapter adapter = (CardViewAdapter) mRecyclerView.getAdapter();
                    isThereMore = json.getBoolean("isThereMore");
                    JSONArray ja = json.getJSONArray("products");
                    Log.i("LOG", "JSONArray.length()" + String.valueOf(ja.length()));
                    if (ja.length() < 1) {
                        Utilitarios.setProgressBar(false, getActivity());
                        //Utilitarios.setDialogError("Desculpe!", "Esse produto não está cadastrado no sistema", getActivity());
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
                    Utilitarios.setDialogError("Erro", "Objeto fornecido é nullo", getActivity());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("LOG", "JSONException(): " + e.getMessage());
                Utilitarios.setDialogError("Erro", "JSONException " + e.getMessage(), getActivity());
            } finally {
                Utilitarios.setProgressBar(false, getActivity());
                filterProds();
            }
        } else {
            Utilitarios.setProgressBar(false, getActivity());
            Utilitarios.setDialogError("ERROR 404 NOT FOUND", "O servidor requisitado pode estar inativo ou a url requisitada não existe!", getActivity());
        }
    }
    //Fazendo a requisição no servidor remoto
    @Override
    public RequestData getRequestData() {
        Log.i("LOG", "RequestData: " + String.valueOf(parametros));
        return (new RequestData(ProdutosBean.PRODUCT_URL, "get-all-product", String.valueOf(parametros)));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mList", (ArrayList<ProdutosBean>) mList);
    }

    @Override
    public void onClickListener(View view, int position) {
        Log.i("LOG", "onClickListener");
        Intent intent = new Intent(getActivity(), ActDetails.class);
        intent.putExtra("prod", mList.get(position));
        getActivity().startActivity(intent);
    }

    @Override
    public void onLongPressClickListener(View view, final int position) {
        Log.i("LOG", "onLongPressClickListener");
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
                                Utilitarios.atualizaValorTotal(getActivity(), Utilitarios.formatarValor(PedidosBean.getValorTotal()));
                                mdDialog.dismiss();
                                Snackbar.make(layoutFragment, getString(R.string.sucess_add_prod), Snackbar.LENGTH_SHORT)
                                        .setAction(getString(R.string.lbl_ok), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Fragment frag = new MyListFragment();
                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
                                                ft.commit();
                                            }
                                        }).show();
                            }
                        });
                        localizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Utilitarios.showImageDialog(mList.get(position).getLocalizacao(), getContext());
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
    //Um filtro para analisar se tem alguma coisa exibindo na tela
    //se não tiver setar que nenhum produto foi encontrado
    protected void filterProds() {
        mRecyclerView.setVisibility(mList.isEmpty() ? View.GONE : View.VISIBLE);
        if (mList.isEmpty()) {
            TextView tv = new TextView(getActivity());
            tv.setText("Nenhum produto encontrado.");
            tv.setTextColor(getResources().getColor(R.color.colorPrimarytext));
            tv.setId(1);
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);

            layoutFragment.addView(tv);
        } else if (layoutFragment.findViewById(1) != null) {
            layoutFragment.removeView(layoutFragment.findViewById(1));
        }

        adapter.notifyDataSetChanged();
    }


}
