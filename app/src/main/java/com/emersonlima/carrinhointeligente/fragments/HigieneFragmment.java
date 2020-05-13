package com.emersonlima.carrinhointeligente.fragments;

import android.util.Log;

import com.emersonlima.carrinhointeligente.domain.ProdutosBean;
import com.emersonlima.carrinhointeligente.domain.RequestData;

/**
 * Created by Emerson Torres on 26/04/2018.
 */

public class HigieneFragmment extends ProductFragment {
    private String genero = "Higiene";
    @Override
    public RequestData getRequestData() {
        RequestData rd = new RequestData(ProdutosBean.PRODUCT_URL, "get-genre-product", String.valueOf(parametros));
        rd.setSecondParams(genero);
        Log.i("LOG: " +"RequestData" , String.valueOf(parametros) );
        return rd;
    }
}
