package com.emersonlima.carrinhointeligente.fragments;


import android.util.Log;
import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;

/**
 * Created by NIDE-MASTER on 26/04/2018.
 */
//Classe filha para pegar um generos especifico
public class AlimentosFragment extends ProductFragment {
    private String genero = "Alimentos";

    //fazendo requisição do genero especifico
    @Override
    public RequestData getRequestData() {
        RequestData rd = new RequestData(ProdutosBean.PRODUCT_URL, "get-genre-product", String.valueOf(parametros));
        rd.setSecondParams(genero);
        Log.i("LOG" ,"RequestData: " + String.valueOf(parametros) );
        return rd;
    }


}
