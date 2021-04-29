package xp.pricekeyborad.com.pricekeyborad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tvPrice;
    LinearLayout llPrice;
    TextView etPrice;
    TextView etOrginalPrice;
    EditText etFreight;
    PriceKeyBoardView keyboardView;
    LinearLayout llPriceSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvPrice = findViewById(R.id.tv_price);
        llPrice = (LinearLayout) findViewById(R.id.ll_price);
        etPrice = (TextView) findViewById(R.id.et_price);
        etOrginalPrice = (TextView) findViewById(R.id.et_orginal_price);
        etFreight = (EditText) findViewById(R.id.et_freight);
        keyboardView = (PriceKeyBoardView) findViewById(R.id.keyboard_view);
        llPriceSelect = (LinearLayout) findViewById(R.id.ll_price_select);

        final KeyboardUtil keyboardUtil = new KeyboardUtil(this);
        keyboardUtil.setOnOkClick(new KeyboardUtil.OnOkClick() {
            @Override
            public void onOkClick() {
                if (validate()) {
                    llPriceSelect.setVisibility(View.GONE);
                    tvPrice.setText("最终工资" + etFreight.getText());
                }
            }
        });

        keyboardUtil.setOnCancelClick(new KeyboardUtil.onCancelClick() {
            @Override
            public void onCancellClick() {
                llPriceSelect.setVisibility(View.GONE);
            }
        });

        llPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardUtil.attachTo(etFreight);
                etPrice.setFocusable(true);
                etPrice.setFocusableInTouchMode(true);
                etPrice.requestFocus();
                llPriceSelect.setVisibility(View.VISIBLE);
            }
        });

        etFreight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyboardUtil.attachTo(etFreight);
                return false;
            }
        });
    }

    public boolean validate() {
        //判断是否在调整范围
        if (etFreight.getText().toString().equals("")) {
            Toast.makeText(getApplication(), "调整值不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        //最终工资等于基本工资+调整值
//        int etFreight.getText().toString().startsWith("");
//        etOrginalPrice.setText();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (llPriceSelect.getVisibility() == View.VISIBLE) {
            llPriceSelect.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}