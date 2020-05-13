package com.emersonlima.carrinhointeligente.domain;

import com.emersonlima.carrinhointeligente.utils.Utilitarios;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emerson Torres on 04/03/2018.
 */

public class PedidosBean {
    public static  final String PEDIDO_URL = Utilitarios.BASE_URL + "/projetoQuickShop/package/ctrl/CtrlPedido.php";


    private static int codigo;
    private static List<ProdutosBean> mListProdutos = new ArrayList<>();
    private static double valorTotal = 0;

    public PedidosBean(){
        //mListProdutos = new ArrayList<>();
    }

    public static List<ProdutosBean> getListProduto() {
        return mListProdutos;
    }

    public static void setProduto(ProdutosBean produto) {
        if(!search(produto)){
            mListProdutos.add(produto);
        }
        valorTotal += produto.getValor_unit();
        //gerarValorTotal();
    }
    /*
    public static void gerarValorTotal() {
        for(int i = 0; i < mListProdutos.size(); i++) {
            valorTotal += mListProdutos.get(i).getValor_unit() * mListProdutos.get(i).getQntd();
        }
    }*/

    public static void setValorTotal(double valorTotal) {
        PedidosBean.valorTotal = valorTotal;
    }

    public static double getValorTotal() {
        if (valorTotal < 0 || valorTotal == -0.00){
            return 0;
        }
        return valorTotal;
    }

    //MÉTODO RESPONSAVÉL DE FAZER A BUSCA PARA VER SE O PRODUTO JA FOI ADICIONADO
    public static boolean search(ProdutosBean pb) {
        boolean achei = false;
        int position = 0;

        for (int i = 0; i < mListProdutos.size(); i++) {
            String s = mListProdutos.get(i).getProd();
            if (s.equals(pb.getProd() )) {
                //IGUAL
                achei= true;
                position = i;
                updateItens(position, pb);
            }
        }
        return achei;
    }

    public static void updateItens(int position, ProdutosBean produtoUpdate) {
        produtoUpdate = mListProdutos.get(position);
        produtoUpdate.incrementQntd();
    }

    public static int getCodigo() {
        return codigo;
    }

    public static void setCodigo(int codigo) {
        PedidosBean.codigo = codigo;
    }
}
