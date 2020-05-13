package com.emersonlima.carrinhointeligente.domain;

import com.emersonlima.carrinhointeligente.utils.Utilitarios;

/**
 * Created by Emerson Torres on 27/04/2018.
 */

public class UsuarioBean {
    public static final String USER_URL = Utilitarios.BASE_URL+ "/projetoQuickShop/package/ctrl/CtrlUsuario.php ";

    private static int codigo = 1;
    private static String cpf;

    public static int getCodigo() {
        return codigo;
    }

    public static void setCodigo(int codigo) {
        UsuarioBean.codigo = codigo;
    }

    public static String getCpf() {
        return cpf;
    }

    public static void setCpf(String cpf) {
        UsuarioBean.cpf = cpf;
    }

}
