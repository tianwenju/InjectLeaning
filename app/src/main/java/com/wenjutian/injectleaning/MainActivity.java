package com.wenjutian.injectleaning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.bt_1)
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectUtls.inject(this);
      //  textView = ((TextView) findViewById(R.id.text));
        textView.setText("sdfsdfsfdsdf");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @OnClick({R.id.bt_1,R.id.bt_2})
    public void showOnclick(View view) {

        switch (view.getId()) {
            case R.id.bt_1:
                Log.e("自定义标签", "onClick: bt1");
                break;
            case R.id.bt_2:
                Log.e("自定义标签", "onClick: bt2 ");
                break;
        }

    }
}
