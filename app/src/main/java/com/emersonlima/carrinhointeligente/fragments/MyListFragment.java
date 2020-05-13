package com.emersonlima.carrinhointeligente.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.emersonlima.carrinhointeligente.ActDetails;
import com.emersonlima.carrinhointeligente.ActPaymentCard;
import com.emersonlima.carrinhointeligente.ActPaymentMoney;
import com.emersonlima.carrinhointeligente.R;
import com.emersonlima.carrinhointeligente.adapters.ProdutosAdapter;
import com.emersonlima.carrinhointeligente.domain.PedidosBean;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.interfaces.RecyclerViewOnClickListenerHack;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import cn.carbs.android.library.MDDialog;

/**
 * Created by Emerson Torres on 27/04/2018.
 */

public class MyListFragment extends Fragment implements RecyclerViewOnClickListenerHack {
    private RelativeLayout layoutActMain;
    private Button btn_finalizaCompra;
    //Parte para pegar imagem web
    private ImageLoader mImageLoader;
    //Pegando a classe anemica para virar objeto
    private ProdutosBean produtosBean = null;
    //Lista de de compras
    private RecyclerView mRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DO BANCO REMOTO
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.error)
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
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);


        //DECLARAÇÃO E APONTAMENTO DE VARÍAVEIS QUE ESTÃO NO LAYOUT
        layoutActMain = (RelativeLayout) view.findViewById(R.id.layout_content_act_main);
        btn_finalizaCompra = (Button) view.findViewById(R.id.btn_finaliza_compra);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);

        if (PedidosBean.getListProduto().size() > 0) {
            btn_finalizaCompra.setEnabled(true);
        } else {
            btn_finalizaCompra.setEnabled(false);
        }
        criarRecyclerView();
        btn_finalizaCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPayment();
            }
        });

        return view;
    }


    private void criarRecyclerView() {
        //Criando os parametros necessarios para se popular uma RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        ProdutosAdapter adapter = new ProdutosAdapter(getContext(), PedidosBean.getListProduto(), mImageLoader);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        //ATUALIZANDO O ITEM NO ADAPTER PARA ALIMENTAR A RECYCLERVIEW
        ProdutosAdapter adapter = (ProdutosAdapter) mRecyclerView.getAdapter();
        adapter.notifyDataSetChanged();
        super.onStart();
    }

    @Override
    public void onResume() {
        //ATUALIZANDO O ITEM NO ADAPTER PARA ALIMENTAR A RECYCLERVIEW
        ProdutosAdapter adapter = (ProdutosAdapter) mRecyclerView.getAdapter();
        adapter.notifyDataSetChanged();

        if (PedidosBean.getValorTotal() > 0) {
            btn_finalizaCompra.setEnabled(true);
        } else {
            btn_finalizaCompra.setEnabled(false);
        }
        super.onResume();
    }

    //MÉTODO RESPONSAVÉL PELO CLICK DA VIEW/O ITEM DA LISTA;
    @Override
    public void onClickListener(View view, int position) {
        final int posicao = position;
        int id = view.getId();
        switch (id) {
            case R.id.ib_delete:
                //BOTÃO PARA DELETAR PRODUTO DA LISTA
                new LovelyStandardDialog(getActivity(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        .setTopColorRes(R.color.darkred)
                        .setButtonsColorRes(R.color.darkBlueGrey)
                        .setIcon(R.mipmap.error)
                        .setTitle("Excluir produto da lista")
                        .setMessage("Tem certeza que deseja excluir " + PedidosBean.getListProduto().get(position).getProd() + " da sua lista ?")
                        .setPositiveButton("Cancelar", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setNeutralButton("Sim", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeValueItem(PedidosBean.getListProduto().get(posicao).getValor_unit());
                                ProdutosAdapter adapter = (ProdutosAdapter) mRecyclerView.getAdapter();
                                adapter.removeListItem(posicao);
                            }
                        })
                        .show();
                break;
            case R.id.layout_item:
                Intent it = new Intent(getContext(), ActDetails.class);
                it.putExtra("prod", PedidosBean.getListProduto().get(position));
                startActivity(it);
                break;
        }
    }


    @Override
    public void onLongPressClickListener(View view, int position) {
        Toast.makeText(getActivity(), PedidosBean.getListProduto().get(position).getProd(), Toast.LENGTH_SHORT).show();
    }

    public void removeValueItem(Double valor) {
        if (valor != null) {
            PedidosBean.setValorTotal(PedidosBean.getValorTotal() - valor);
            Utilitarios.atualizaValorTotal(getActivity(), Utilitarios.formatarValor(PedidosBean.getValorTotal()));
            if (PedidosBean.getListProduto().size()  < 0) {
                btn_finalizaCompra.setEnabled(false);
            }
        } else {
            Utilitarios.setDialogError("ERRO!", "Erro ao tentar remover o item!, tente novamente", getActivity());
        }
    }

    //BOTÃO PARA FINALIZAR AS COMPRAS
    public void onClickPayment() {
        Log.i("PedidosBean","getValorTotal(): " + String.valueOf(PedidosBean.getValorTotal()));
        if (PedidosBean.getValorTotal() <= 0){
            btn_finalizaCompra.setEnabled(false);
        }else {
            new LovelyStandardDialog(getActivity(), LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                    .setTopColorRes(R.color.darkGreen)
                    .setButtonsColorRes(R.color.darkblue)
                    .setIcon(R.drawable.ic_money_icon)
                    .setTitle("Forma de pagamento")
                    .setMessage("Qual forma de pagamento você deseja realizar ?")
                    .setPositiveButton("Dinheiro", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getActivity(), ActPaymentMoney.class));
                            getActivity().finish();
                        }
                    })
                    .setNeutralButton("Cartão", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onCreateDialogCard();
                        }
                    })
                    .show();
        }

    }

    //pop-up para inserção de dados do cartão do cliente
    private void onCreateDialogCard() {
        new MDDialog.Builder(getActivity())
                .setTitle("Pagamento")
                .setContentView(R.layout.payment)
                .setNegativeButton("Cancelar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("Finalizar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ActPaymentCard.class));
                        getActivity().finish();

                    }
                })
                .setWidthMaxDp(400)
                .create()
                .show();
    }
}
