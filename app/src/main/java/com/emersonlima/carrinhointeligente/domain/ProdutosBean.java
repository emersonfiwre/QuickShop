package com.emersonlima.carrinhointeligente.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Double2;

import com.emersonlima.carrinhointeligente.utils.Utilitarios;

/**
 * Created by Emerson on 06/12/2017.
 */

public class ProdutosBean implements Parcelable {
    public static final String PRODUCT_URL = Utilitarios.BASE_URL + "/projetoQuickShop/package/ctrl/CtrlProduct.php";

    private int codigo = 0;
    private int codigo_busca = 0;
    private String prod = "";
    private String desc = "";
    private String genero = "";
    private String localizacao = "";
    private int qntd = 1;
    private String validade_produto = "";
    private Double valor_unit;
    private String photo = "";
    private Double valor_before;
    private Double discount_porcent;



    public ProdutosBean(){}
    /*public ProdutosBean(String t, String d, int q, Double v, String p, Double tot) {
        this.setProd(t);
        this.setDesc(d);
        this.setQntd(q);
        this.setValor_unit(v);
        this.setPhoto(p);
        this.setValorTotal(tot);
    }*/


    public ProdutosBean(Parcel parcel) {
        setCodigo(parcel.readInt());
        setProd(parcel.readString());
        setDesc(parcel.readString());
        setGenero(parcel.readString());
        setLocalizacao(parcel.readString());
        setQntd(parcel.readInt());
        setValidade_produto(parcel.readString());
        setValor_unit(parcel.readDouble());
        setPhoto(parcel.readString());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getCodigo());
        dest.writeString(getProd());
        dest.writeString(getDesc());
        dest.writeString(getGenero());
        dest.writeString(getLocalizacao());
        dest.writeInt(getQntd());
        dest.writeString(getValidade_produto());
        dest.writeDouble(getValor_unit());
        dest.writeString(getPhoto());

    }

    public static final Creator<ProdutosBean> CREATOR = new Creator<ProdutosBean>() {
        @Override
        public ProdutosBean createFromParcel(Parcel source) {
            return new ProdutosBean(source);
        }

        @Override
        public ProdutosBean[] newArray(int size) {
            return new ProdutosBean[size];
        }
    };

    public void incrementQntd() {
        setQntd(getQntd() + 1);

        /*Double valorQntd = Double.parseDouble(valor_unit);
        String valorUnitXqntd = String.valueOf(valorQntd * 2);

        valor_unit = valorUnitXqntd;*/
    }
    public void decrementQntd() {
        setQntd(getQntd() - 1);
    }



    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo_busca() {
        return codigo_busca;
    }

    public void setCodigo_busca(int codigo_busca) {
        this.codigo_busca = codigo_busca;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public int getQntd() {
        return qntd;
    }

    public void setQntd(int qntd) {
        this.qntd = qntd;
    }

    public String getValidade_produto() {
        return validade_produto;
    }

    public void setValidade_produto(String validade_produto) {
        this.validade_produto = validade_produto;
    }

    public Double getValor_unit() {
        return valor_unit;
    }

    public void setValor_unit(Double valor_unit) {
        this.valor_unit = valor_unit;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public Double getValor_before() {
        return valor_before;
    }

    public void setValor_before(Double valor_before) {
        this.valor_before = valor_before;
    }

    public Double getDiscount_porcent() {
        return discount_porcent;
    }

    public void setDiscount_porcent(Double discount_porcent) {
        this.discount_porcent = discount_porcent;
    }
}