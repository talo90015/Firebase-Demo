package com.talo.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Context context = this;

    Button btnAdd;
    Button btnEdit;
    Button btnDelete;
    TextView txtTotal;
    TextView txtData;
    EditText editTotal;
    EditText editData;
    ListView listView;

    int x_last = 0;
    String x_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btn_add);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        txtTotal = findViewById(R.id.edit_total);
        txtData = findViewById(R.id.edit_data);

//        editTotal = findViewById(R.id.edit_total);
//        editData = findViewById(R.id.edit_data);

        listView = findViewById(R.id.listView);

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //數據連線庫
        DatabaseReference reference = database.getReference("data_text"); //父節點

        final DatabaseReference reference1 = reference.child("data01");//子節點，分支

        firebase_select(reference1);  //讀取資料庫

        //新增
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x_last += 1;
                reference1.child(String.valueOf(x_last)).setValue(
                        new TextString(txtTotal.getText().toString(), txtData.getText().toString()));
                firebase_select(reference1);
            }
        });
        //修改
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference1.child(x_select).setValue(new TextString(txtTotal.getText().toString(), txtData.getText().toString()));
                firebase_select(reference1);
            }
        });
        //刪除
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference1.child(x_select).removeValue();
                firebase_select(reference1);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt_1 = view.findViewById(R.id.txt_1);
                x_select = txt_1.getText().toString();

                TextView txt_2 = view.findViewById(R.id.txt_2);
                txtTotal.setText(txt_2.getText().toString());

                TextView txt_3 = view.findViewById(R.id.txt_3);
                txtData.setText(txt_3.getText().toString());

            }
        });
    }

    private void firebase_select(DatabaseReference databaseReference) {
        final List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

        //為單值事件加上監聽
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x_sum = (int) snapshot.getChildrenCount();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    TextString user_data = ds.getValue(TextString.class);
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("id", ds.getKey());
                    item.put("str1", user_data.getStr1());
                    item.put("str2", user_data.getStr2());
                    items.add(item);
                    x_last = Integer.parseInt(ds.getKey()); //抓取最後一個值
                }
                SimpleAdapter adapter = new SimpleAdapter(context, items, R.layout.text_string, new String[]{"id", "str1", "str2"},
                        new int[]{R.id.txt_1, R.id.txt_2, R.id.txt_3});
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}