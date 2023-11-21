package com.guilhermeweber.wasteless.activity.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAlertDialog extends AlertDialog {

    public CustomAlertDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout para armazenar os TextViews
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Criar TextViews e adicionar ao layout
        for (int i = 1; i <= 5; i++) {
            TextView textView = new TextView(getContext());
            textView.setText("TextView " + i);
            linearLayout.addView(textView);
        }

        // Definir o layout no AlertDialog
        setContentView(linearLayout);

        // Botão OK (ou outro botão, se necessário)
        setButton(BUTTON_POSITIVE, "OK", (dialog, which) -> dismiss());
    }
}