package com.emersonlima.carrinhointeligente.domain;

/**
 * Created by Emerson on 01/03/2018.
 */

public class RequestData {
    private String url;
    private String method;
    private String params;
    private String secondParams = null;
    private String thirdParams = null;

    public RequestData(String u, String m, String p) {
        this.url = u;
        this.method = m;
        this.params = p;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSecondParams() {
        return secondParams;
    }

    public void setSecondParams(String secondParams) {
        this.secondParams = secondParams;
    }

    public String getThirdParams() {
        return thirdParams;
    }

    public void setThirdParams(String thirdParams) {
        this.thirdParams = thirdParams;
    }





   /* public Object getObj() {
        return obj;
    }
    public void setObj(Object obj) {
        this.obj = obj;
    }*/
}

