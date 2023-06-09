package com.example.example_btl_androidnc.students.addItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;
import android.widget.Toolbar;

import com.example.example_btl_androidnc.R;
import com.example.example_btl_androidnc.students.adapter.ListCourseAdapter;
import com.example.example_btl_androidnc.students.adapter.StudentListAdapter;
import com.example.example_btl_androidnc.students.api.GetAPI_Service;
import com.example.example_btl_androidnc.students.api.RetrofitClient;
import com.example.example_btl_androidnc.students.model.UserCourse;
import com.example.example_btl_androidnc.students.model.Users;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentList extends AppCompatActivity {
    RecyclerView recyclerView;
    StudentListAdapter adapter;
    private List<Users> usersList;
    String courseId;
    SearchView searchView;
    Context context;
    androidx.appcompat.widget.Toolbar tbStudent;
    StudentListAdapter studentListAdapter;

    // private List<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_list);
        Intent intent = getIntent();

        tbStudent = findViewById(R.id.tbStudent);

        setSupportActionBar(tbStudent);

        courseId = intent.getStringExtra("courseId");
        // Thêm đoạn mã này để kiểm tra dữ liệu nhận được
        if (courseId == null) {
            Log.e("StudentList", "courseId is null");
        } else {
            Log.i("StudentList", "courseId: " + courseId);
        }

        // lấy danh sách sinh viên
        getDataStudent(courseId);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
   //     studentListAdapter = new StudentListAdapter(getDataStudent(courseId));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
// không dùng
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                filterStudent(s);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                filterStudent(s);
//                return false;
//            }
//        });
    }


    // Điểm danh
    public void getDataStudent(String idCouse) {
        GetAPI_Service getAPI_service = RetrofitClient.getClient().create(GetAPI_Service.class);
        Call<List<Users>> call = getAPI_service.getUsersWithRoleUserInCourse(idCouse);
        // Log.d("testloi",idCouse);
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {

                if (response.isSuccessful()) {
                    Log.d("hihihi", response.body().toString());
                    usersList = response.body();
                    adapter = new StudentListAdapter(StudentList.this, usersList, courseId);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(StudentList.this, "Lỗi khi lấy danh sách sinh viên từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Toast.makeText(StudentList.this, "Lỗi khi kết nối tới server" + t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("testtst", t.toString());
            }
        });
    }



    //tìm kiếm trong danh sách học sinh của lớp học
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.mnuSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //adapter.getFilter().filter(s);
                filterStudent(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
            //   adapter.getFilter().filter(s);
                filterStudent(s);
                return false;
            }
        });
        return true;
    }


private void PutDataIntoRecyclerView(List<Users> movieList) {
    StudentListAdapter adapter = new StudentListAdapter(StudentList.this, movieList);
    recyclerView.setLayoutManager(new LinearLayoutManager(StudentList.this));
    recyclerView.setAdapter(adapter);
}

    private void filterStudent(String query) {
        List<Users> filteredStudentList = new ArrayList<>();
        for (Users users : usersList) {
            if (users.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredStudentList.add(users);
            }
        }
        PutDataIntoRecyclerView(filteredStudentList);
    }

//    @Override
//    public void onBackPressed() {
//        if(!searchView.isIconified()){
//            searchView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }
}