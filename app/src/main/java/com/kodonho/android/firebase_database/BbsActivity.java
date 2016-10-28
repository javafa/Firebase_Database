package com.kodonho.android.firebase_database;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BbsActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference rootRef;
    DatabaseReference postsRef;

    EditText etTitle;
    EditText etName;
    EditText etBody;

    Button post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs);

        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();
        postsRef = database.getReference("posts");

        etTitle = (EditText) findViewById(R.id.etTitle);
        etName = (EditText) findViewById(R.id.etName);
        etBody = (EditText) findViewById(R.id.etBody);

        post = (Button) findViewById(R.id.btnPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String body = etBody.getText().toString().trim();

                writeNewPost("axuidn013547",name,title,body);
            }
        });
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // postsRef 가 가르키는 posts 에 유일한 키값을 생성해서 가져온다
        String key = postsRef.push().getKey();
        // 글을 쓴 내용을 Post 객체로 만들어 주고
        Post post = new Post(userId, username, title, body);
        // 해당객체를 Map 으로 변환한다
        Map<String, Object> postValues = post.toMap();

        // 실제 파이어 베이스에 올릴 맵을 추가로 생성하고
        // posts 노드와 user-posts 노드 두곳에 데이터를 업데이트한다
        // - 이유는 사용자 기준으로 올라온 글을 검색할 수 있게 하기 위함이다
        Map<String, Object> childUpdates = new HashMap<>();

        // /posts 노드에 데이터 추가
        childUpdates.put("/posts/" + key, postValues);
        // /user-posts 노드에 데이터 추가
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        // 추가된 데이터를 root 노드에서 업데이트 해준다
        rootRef.updateChildren(childUpdates);
    }

}
