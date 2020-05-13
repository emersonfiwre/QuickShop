package com.emersonlima.carrinhointeligente.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.emersonlima.carrinhointeligente.R;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Emerson Torres on 10/05/2018.
 */

public class DiscountAdapter extends CardViewAdapter {


    public DiscountAdapter(Context c, List<ProdutosBean> l, ImageLoader imageLoader) {
        super(c, l, imageLoader);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_produtos_card_desconto, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        Log.i("LOG", "onBindViewHolder()");
        mImageLoader.displayImage(mList.get(position).getPhoto(), myViewHolder.ivProd);

        myViewHolder.tvProd.setText(mList.get(position).getProd());
        myViewHolder.tvValorAtual.setText("DE: " + Utilitarios.formatarValor(mList.get(position).getValor_before()));
        myViewHolder.tvValorDesconto.setText("POR: " + Utilitarios.formatarValor(mList.get(position).getValor_unit()));
        myViewHolder.tvValorPorcentagem.setText((mList.get(position).getDiscount_porcent()) + "% OFF");

    }

}
