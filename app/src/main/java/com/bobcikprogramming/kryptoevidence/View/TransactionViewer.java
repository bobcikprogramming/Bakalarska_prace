package com.bobcikprogramming.kryptoevidence.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bobcikprogramming.kryptoevidence.Controller.TransactionViewerController;
import com.bobcikprogramming.kryptoevidence.R;

public class TransactionViewer extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnBack, btnEdit;
    private ViewPager transactionViewer;

    private TransactionViewerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_viewer);
        int position = (int) getIntent().getSerializableExtra("position");

        setupUIViews();

        controller = new TransactionViewerController(this, transactionViewer, position);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                closeActivity();
                break;
            case R.id.btnEdit:
                Intent infoActivity = new Intent(TransactionViewer.this, TransactionEdit.class);
                infoActivity.putExtra("transactionID", controller.getDataFromDatabase().get(transactionViewer.getCurrentItem()).transaction.uidTransaction);
                infoActivityTransactionEditLauncher.launch(infoActivity);
                break;
        }
    }

    private void setupUIViews(){
        transactionViewer = findViewById(R.id.viewPagerTransaction);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);

        btnBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void closeActivity(){
        Intent intent = new Intent();
        intent.putExtra("changed", controller.isChanged());
        setResult(RESULT_OK, intent );
        System.out.println("---------------------volám toto");
        finish();
    }

    ActivityResultLauncher<Intent> infoActivityTransactionEditLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                controller.setChanged(data.getBooleanExtra("changed", false));
                boolean deleted = data.getBooleanExtra("deleted", false);
                boolean photoChange = data.getBooleanExtra("photoChange", false);
                if(controller.isChanged() || photoChange){
                    controller.loadDataFromDb();
                    if(deleted){
                        System.out.println("--------------close: " + String.valueOf(controller.isChanged()));
                        closeActivity();
                    }else {
                        controller.viewPagerAdapterUpdate();
                        if(photoChange){
                            controller.getViewPagerAdapter().showPhotosIfNotEmpty();
                        }
                    }
                }
            }
        });
}