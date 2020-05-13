package com.emersonlima.carrinhointeligente.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.emersonlima.carrinhointeligente.R;
import com.emersonlima.carrinhointeligente.adapters.DiscountAdapter;
import com.emersonlima.carrinhointeligente.connection.VolleyConn;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.carbs.android.library.MDDialog;

/**
 * Created by Emerson Torres on 17/04/2018.
 */

public class DiscountFragment extends ProductFragment {
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

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager llm = (GridLayoutManager) mRecyclerView.getLayoutManager();

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

        DiscountAdapter adapter = new DiscountAdapter(getActivity(), mList, mImageLoader);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void doBefore() {
        Utilitarios.setProgressBar(true, getActivity());

    }

    @Override
    public void doAfter(String answer) {
        if (answer != null) {
            try {
                JSONObject json = new JSONObject(answer);

                if (!json.isNull("products")) {

                    DiscountAdapter adapter = (DiscountAdapter) mRecyclerView.getAdapter();
                    isThereMore = json.getBoolean("isThereMore");
                    JSONArray ja = json.getJSONArray("products");
                    Log.i("LOG", "JSONArray.length()" + String.valueOf(ja.length()));
                    if (ja.length() < 1) {
                        Utilitarios.setProgressBar(false, getActivity());
                        //Utilitarios.setDialogError("Desculpe!", "Esse produto não está cadastrado no sistema",getActivity());
                    } else {
                        for (int i = 0, tam = ja.length(); i < tam; i++) {
                            JSONObject jProduct = ja.getJSONObject(i);

                            produtosBean = new ProdutosBean();

                            produtosBean.setCodigo(jProduct.getInt("cod_produto"));
                            produtosBean.setCodigo_busca(jProduct.getInt("cod_busca"));
                            produtosBean.setValor_before(jProduct.getDouble("valor_atual"));
                            produtosBean.setValor_unit(jProduct.getDouble("valor_desconto"));
                            produtosBean.setDiscount_porcent(jProduct.getDouble("desconto_porcentagem"));
                            produtosBean.setProd(jProduct.getString("nome_produto"));
                            produtosBean.setDesc(jProduct.getString("descricao"));
                            produtosBean.setGenero(jProduct.getString("genero"));
                            produtosBean.setLocalizacao(jProduct.getString("localizacao"));
                            produtosBean.setValidade_produto(jProduct.getString("validade_produto"));
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
                //filterProds();
            }
        } else {
            Utilitarios.setProgressBar(false, getActivity());
            Utilitarios.setDialogError("ERROR 404 NOT FOUND", "O servidor requisitado pode estar inativo ou a url requisitada não existe!", getActivity());
        }
    }

    @Override
    public RequestData getRequestData() {
        Log.i("LOG", "RequestData: " + String.valueOf(parametros));
        return (new RequestData(ProdutosBean.PRODUCT_URL, "get-promotions-product", String.valueOf(parametros)));

    }

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
