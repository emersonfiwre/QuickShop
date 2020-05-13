package com.emersonlima.carrinhointeligente.connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.emersonlima.carrinhointeligente.domain.RequestData;
import com.emersonlima.carrinhointeligente.interfaces.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emerson on 01/03/2018.
 */

public class VolleyConn {
    private Transaction transaction;
    private RequestQueue requestQueue;
    //NÃO PRECISA DO CONTEXT DEPOIS ARRUMAR ISTO

    public VolleyConn(Context c, Transaction t, RequestQueue request) {
        transaction = t;requestQueue = request;
    }


    public void execute() {
        transaction.doBefore();
        callByStringRequest(transaction.getRequestData());

    }


    private void callByStringRequest(final RequestData requestData) {
        StringRequest request = new StringRequest(Request.Method.POST,
                requestData.getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LOG", "onResponse(): " + response.toString());
                        transaction.doAfter(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("LOG", "onErrorResponse(): " + error.getMessage());
                        transaction.doAfter(null);
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", requestData.getMethod());

                //Uma verificação para ver se precisa-se enviar mais parametros
                if (requestData.getParams() != null) {
                    params.put("lastParams", requestData.getParams());
                    if (requestData.getSecondParams() != null) {
                        params.put("lastSecondParams", requestData.getSecondParams());
                        if(requestData.getThirdParams() != null){
                            params.put("lastThrirdParams", requestData.getThirdParams());
                        }
                    }
                }

                return (params);
            }
        };

        request.setTag("conn");
        requestQueue.add(request);
    }
}
