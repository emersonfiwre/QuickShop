package com.emersonlima.carrinhointeligente.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emersonlima.carrinhointeligente.R;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.interfaces.RecyclerViewOnClickListenerHack;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


/**
 * Created by Emerson Torres on 10/3/18.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MyViewHolder> {

    protected Context mContext;
    protected List<ProdutosBean> mList;
    protected LayoutInflater mLayoutInflater;
    protected RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    protected float scale;
    protected int width;
    protected int height;
    protected ImageLoader mImageLoader;

    public CardViewAdapter(Context c, List<ProdutosBean> l, ImageLoader imageLoader) {
        mContext = c;
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = imageLoader;

        scale = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels - (int) (14 * scale + 0.5f);
        height = (width / 16) * 9;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_produtos_card, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {

        Log.i("LOG", "onBindViewHolder()");
        mImageLoader.displayImage(mList.get(position).getPhoto(), myViewHolder.ivProd);

        myViewHolder.tvProd.setText(mList.get(position).getProd());
        myViewHolder.tvValor.setText(Utilitarios.formatarValor(mList.get(position).getValor_unit()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView ivProd;
        public TextView tvProd;
        public TextView tvValor;

        public TextView tvValorAtual;
        public TextView tvValorDesconto;
        public TextView tvValorPorcentagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivProd = (ImageView) itemView.findViewById(R.id.iv_produto);
            tvProd = (TextView) itemView.findViewById(R.id.tv_produto);
            tvValor = (TextView) itemView.findViewById(R.id.tv_valor);

            tvValorAtual = (TextView) itemView.findViewById(R.id.tv_valor_atual);
            tvValorDesconto = (TextView) itemView.findViewById(R.id.tv_valor_desconto);
            tvValorPorcentagem = (TextView) itemView.findViewById(R.id.discount_porcent);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.onClickListener(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.onLongPressClickListener(v, getAdapterPosition());
                return true;
            }
            return false;
        }
    }
}

