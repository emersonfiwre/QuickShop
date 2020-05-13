package com.emersonlima.carrinhointeligente.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.R;
import com.emersonlima.carrinhointeligente.interfaces.RecyclerViewOnClickListenerHack;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Emerson on 10/12/2017.
 */

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ViewHolderProdutos> {
    private List<ProdutosBean> list;
    private LayoutInflater layoutInflater;
    private ImageLoader mImageLoader;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;


    public ProdutosAdapter(Context c, List<ProdutosBean> l, ImageLoader imageLoader) {
        list = l;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = imageLoader;

    }

    @Override
    public ViewHolderProdutos onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("LOG", "ViewHolderProdutos()");
        View v = layoutInflater.inflate(R.layout.item_produtos, parent, false);
        ViewHolderProdutos vhp = new ViewHolderProdutos(v);
        return vhp;
    }

    @Override
    public void onBindViewHolder(ViewHolderProdutos holder, final int position) {
        Log.i("LOG", "onBindViewHolder()");
        mImageLoader.displayImage(list.get(position).getPhoto(), holder.ivProd);
        //holder.ivProd.setImageResource(list.get(position).getPhoto() );
        holder.tvProd.setText(list.get(position).getProd());
        holder.tvDesc.setText(list.get(position).getDesc());

        int qntd = list.get(position).getQntd();//pegando o int para transforma ele em string
        final String qntdString = "Quantidade: " + String.valueOf(qntd);//aqui é feita a transformação em string
        holder.tvQntd.setText(qntdString);//Setando a string no campo

        holder.tvValor.setText(Utilitarios.formatarValor(list.get(position).getValor_unit()));

        holder.ibDelete.setBackgroundResource(R.drawable.delete_button);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }

    public void addListItem(ProdutosBean p, int position) {
        list.add(p);
        notifyItemInserted(position);
    }

    public void removeListItem(int position) {
        if (list.get(position).getQntd() > 1) {
            ProdutosBean pb = list.get(position);
            pb.decrementQntd();
            notifyItemChanged(position);
        } else {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        }
    }

    public class ViewHolderProdutos extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ImageView ivProd;
        public TextView tvProd;
        public TextView tvDesc;
        public TextView tvQntd;
        public TextView tvValor;
        public ImageButton ibDelete;


        public ViewHolderProdutos(View itemView) {
            super(itemView);

            ivProd = (ImageView) itemView.findViewById(R.id.iv_prod);
            tvProd = (TextView) itemView.findViewById(R.id.tv_prod);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvQntd = (TextView) itemView.findViewById(R.id.tv_qntd);
            tvValor = (TextView) itemView.findViewById(R.id.tv_valor_unit);
            ibDelete = (ImageButton) itemView.findViewById(R.id.ib_delete);

            ibDelete.setOnClickListener(this);
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
