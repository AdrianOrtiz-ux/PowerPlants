package info.adrian.powerplant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    public static final String TAG = "UsersPostsActivity";

    RecyclerView userPosts;

    private UserPostsAdapter adapter;
    private List<Post> usersPosts;
    private TextView userName;
    private Button editUser;
    private String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        queryUsersPosts();

        userPosts = findViewById(R.id.userPosts);
        editUser = findViewById(R.id.editUserButton);

        userName = findViewById(R.id.userProfileUsername);
        userName.setText(ParseUser.getCurrentUser().getUsername());

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editAccountActivity = new Intent(getApplicationContext(), UserEditAccountActivity.class);
                startActivity(editAccountActivity);
            }
        });

        usersPosts = new ArrayList<>();
        adapter = new UserPostsAdapter(getApplicationContext(), usersPosts);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        userPosts.setLayoutManager(gridLayoutManager);
        userPosts.setAdapter(adapter);



    }

    private void queryUsersPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post : posts){
                    Log.i(TAG, "Post: " + post.getDescription() + ", username:" + post.getUser().getUsername());
                }

                for(int i = posts.size() - 1; i >= 0; i--){ //will add the posts in reverse order which is New first, old bottom.
                    usersPosts.add(posts.get(i));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}