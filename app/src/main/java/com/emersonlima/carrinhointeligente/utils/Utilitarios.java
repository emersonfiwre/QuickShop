package com.emersonlima.carrinhointeligente.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emersonlima.carrinhointeligente.ActPaymentCard;
import com.emersonlima.carrinhointeligente.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.DecimalFormat;

import cn.carbs.android.library.MDDialog;

/**
 * Created by Emerson Torres on 26/04/2018.
 */

public class Utilitarios {
    public static String BASE_URL = "http://192.168.0.10";
    //public static String BASE_URL = "http://192.168.15.11";
    private static ImageLoader mImageLoader;
    public static boolean islandScape;

    private static void createUimageDialog(Context context) {
        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DO BANCO REMOTO
        DisplayImageOptions mDisplayImageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.error)
                .showImageOnFail(R.drawable.retry_img)
                .showImageOnLoading(R.drawable.loading_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //DECLARAÇÃO DE PARAMETROS PARA CARREGAR IMAGENS DA ACTIVITY ANTERIOR
        ImageLoaderConfiguration conf = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .threadPoolSize(5)
                .writeDebugLogs()
                .build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(conf);

    }

    //MÉTODO "HERDADO" DO JAVA PARA FORMATA O DOUBLE EM FORMATO DECIMAL
    public static String formatarValor(Double valor) {
        DecimalFormat df = new DecimalFormat("###,###,#00.00");
        String valorFormatado = "R$" + df.format(valor);

        return valorFormatado;
    }

    //MÉTODO PARA COLOCAR UMA JANELA DE DIALOGO
    public static void setDialogError(String title, String message, Context context) {
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.darkred)
                .setButtonsColorRes(R.color.darkblue)
                .setIcon(R.mipmap.error)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    //MÉTODO PARA MOSTRAR UM PROGRESSO PARA USUARIO
    public static void setProgressBar(boolean status, Context context) {
        status = false;
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Aguarde um momento");
        progress.setMessage("Buscando...");
        if (status) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    //Método responsável por atualizar a footer localizada no ActMain
    public static void atualizaValorTotal(final Activity activity, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) activity.findViewById(R.id.valor_total_footer)).setText(activity.getString(R.string.valor_total) + " " + text);

            }
        });
    }

    //Método para exibir imagens em um pop-up rapido
    public static void showImageDialog(final String image, final Context context) {
        new MDDialog.Builder(context)
                .setContentView(R.layout.dialog_show_image)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_show_image);
                        createUimageDialog(context);
                        mImageLoader.displayImage(image, imageView);
                    }
                })
                .setNegativeButton("Fechar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setShowTitle(false)
                .setWidthMaxDp(350)
                .setShowPositiveButton(false)
                .create()
                .show();
    }

    /*
        public static void showImageDialog(String image,Context context){
            Dialog builder = new Dialog(context);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //nothing;
                }
            });

            ImageView imageView = new ImageView(context);
            createUimageDialog(context);
            imageView.setMaxHeight(300);
            imageView.setMaxWidth(370);
            mImageLoader.displayImage(image, imageView);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            builder.show();
        }
    */

    //Método responsável por deixar a tela na horizontal fixa
    public static void landscape(Activity activity) {
        if (islandScape) {
            View decorView = activity.getWindow().getDecorView();

            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

}
