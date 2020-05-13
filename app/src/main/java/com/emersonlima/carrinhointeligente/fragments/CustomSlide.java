package com.emersonlima.carrinhointeligente.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.emersonlima.carrinhointeligente.R;
import com.emersonlima.carrinhointeligente.utils.Utilitarios;
import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by Emerson Torres on 16/05/2018.
 */
//Slide customizado para tela de introdução
public class CustomSlide extends SlideFragment {
    private CheckBox checkBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_introduction, container, false);
        checkBox = (CheckBox) view.findViewById(R.id.cb_concordo);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.white;
    }

    @Override
    public int buttonsColor() {
        return R.color.slide_button;
    }

    @Override
    public boolean canMoveFurther() {
        return checkBox.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        Utilitarios.setDialogError("Atenção",getString(R.string.check_error),getActivity());
        return null;
    }
}
