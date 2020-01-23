package com.example.instaedittext;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final EditText remainingWorkEdit=findViewById(R.id.et);
//        remainingWorkEdit.addTextChangedListener(new TextWatcher()
//        {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,int count)
//            {
//                final SpannableString text = new SpannableString(remainingWorkEdit.getText().toString());
//                text.setSpan(new AbsoluteSizeSpan(18), text.length() - "stackOverflow".length(), text.length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                text.setSpan(new ForegroundColorSpan(Color.RED), 3, text.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                remainingWorkEdit.setText(text);
//            }
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after)
//            {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void afterTextChanged(Editable s)
//            {
//                // TODO Auto-generated method stub
//
//            }
//        });
    }
}
