package com.emersonlima.carrinhointeligente.interfaces;

import com.emersonlima.carrinhointeligente.domain.RequestData;

/**
 * Created by Emerson on 01/03/2018.
 */

public interface Transaction {
    public void doBefore();
    public void doAfter(String answer);
    public RequestData getRequestData();
}
