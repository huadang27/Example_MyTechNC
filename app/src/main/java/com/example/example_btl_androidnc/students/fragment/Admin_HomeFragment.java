package com.example.example_btl_androidnc.students.fragment;

import static com.example.example_btl_androidnc.students.api.RetrofitClient.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.example_btl_androidnc.students.addItem.AllCoursesActivity;
import com.example.example_btl_androidnc.students.addItem.AllCoursesActivity;
import com.example.example_btl_androidnc.students.addItem.BlogActivity;
import com.example.example_btl_androidnc.students.addItem.Edit_Profile;
import com.example.example_btl_androidnc.students.addItem.SetAdmin_Activity;
import com.example.example_btl_androidnc.students.api.GetAPI_Service;
import com.example.example_btl_androidnc.students.api.RetrofitClient;
import com.example.example_btl_androidnc.students.adapter.CourseAdapter;
import com.example.example_btl_androidnc.students.authentication.LoginActivity;
import com.example.example_btl_androidnc.students.authentication.SignUpActivity;
import com.example.example_btl_androidnc.students.database.MySharedPreferences;
import com.example.example_btl_androidnc.students.model.Course;
import com.example.example_btl_androidnc.R;
import com.example.example_btl_androidnc.students.model.UserCourse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_HomeFragment extends Fragment {

    RecyclerView recyclerView;
    List<Course> CourseList;
    Button Bt_dn, Bt_TinTuc,bt_all_course;
    TextView textView;
    private MySharedPreferences mySharedPreferences;
    SearchView searchView;
    RelativeLayout header_demo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        connectWebSocket();
        recyclerView = view.findViewById(R.id.recyclerview);
        Bt_dn = view.findViewById(R.id.bt_dn);
        Bt_TinTuc = view.findViewById(R.id.bt_tintuc);
        bt_all_course = view.findViewById(R.id.bt_all_course);
        //Mở tìm kiếm
        View headerTitle = view.findViewById(R.id.header_title);
        headerTitle.setVisibility(View.GONE);
        searchView =view.findViewById(R.id.searchView);
        textView = view.findViewById(R.id.textView);
        header_demo = view.findViewById(R.id.header_demo);
        header_demo.setVisibility(View.VISIBLE);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) searchView.getLayoutParams();
                if (hasFocus) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                }
//                searchView.setLayoutParams(layoutParams);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Ẩn bàn phím khi người dùng ấn Enter trên bàn phím
                Toast.makeText(getContext() , query, Toast.LENGTH_SHORT).show();
//                searchView.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCoursesAdmin(newText);
                return false;
            }
        });



        CourseList = new ArrayList<>();
        mySharedPreferences= new MySharedPreferences(getContext());
        Bt_TinTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BlogActivity.class);
                startActivity(intent);
            }
        });
        Bt_dn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SignUpActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        bt_all_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AllCoursesActivity.class);
                startActivity(intent);
            }
        });

        getData();

//
        return view;


    }

    public void getData(){
        GetAPI_Service getAPI_service = RetrofitClient.getClient().create(GetAPI_Service.class);

        Call<List<Course>> call = getAPI_service.getCourse();
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
//                Log.d("test",response.body().toString());
                if (response.code() != 200) {
                    Log.d("test", "Response code: " + response.code());
                    Log.d("test", "Response message: " + response.message());


                }

                // hiện theo điều kiện sinh viên đã đki khóa học
                List<Course> courses = response.body();

                HashMap<String, Integer> courseData = mySharedPreferences.getCourseDataFromSharedPreferences();

                for (Course course : courses) {
                    // Kiểm tra nếu courseId không trùng với courseId đã lưu trong SharedPreferences
                    if (!courseData.containsKey(course.getId())) {
                        CourseList.add(course);
                    }
                }

// hiện toàn bộ

             /*   List<Course> courses = response.body();
                for (Course movie : courses) CourseList.add(movie);
                Log.d("test", "thêm dữ liệu thành công");*/

                Log.d("testdemo",CourseList.toString());

                PutDataIntoRecyclerView(CourseList);


            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Log.d("test", t.toString() + " ______onfailue_____");
            }

        });
    }

    private void PutDataIntoRecyclerView(List<Course> courses) {
        CourseAdapter adapter = new CourseAdapter(getContext(), courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void filterCoursesAdmin(String query) {
        List<Course> filteredCourseList = new ArrayList<>();
        for (Course course : CourseList) {
            if (course.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredCourseList.add(course);
            }
        }
        PutDataIntoRecyclerView(filteredCourseList);
    }


    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + "my-websocket-endpoint")
                // ws://192.168.80.149:8082/my-websocket-endpoint

                .build();
        client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d("hihi: ", reason + "onClosed.1");
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d("hihi: ", reason + "onClosing.2");
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
//                Log.d("hihi: ", response.toString() + "onFailure.3");

            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                super.onMessage(webSocket, text);
                Log.d("hihi", text.toString() + "   ____onMessage.4______");
                List<Course> list = new Gson().fromJson(text, new TypeToken<List<Course>>() {
                }.getType());


                HashMap<String, Integer> courseData = mySharedPreferences.getCourseDataFromSharedPreferences();
                List<Course> CourseRealtime = new ArrayList<>();
                for (Course course : list) {
                    // Kiểm tra nếu courseId không trùng với courseId đã lưu trong SharedPreferences
                    if (!courseData.containsKey(course.getId())) {
                        CourseRealtime.add(course);
                    }
                }




                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Hiển thị danh sách Category lên giao diện
                        PutDataIntoRecyclerView(CourseRealtime);
                        System.out.println(CourseRealtime.toString());

                    }
                });
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Hiển thị danh sách Category lên giao diện
//                        PutDataIntoRecyclerView(courses);
//                        System.out.println(courses.toString());
//
//                    }
//                });

            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d("hihi: ", bytes.toString() + "onMessage.5");


            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull okhttp3.Response response) {
                super.onOpen(webSocket, response);
                Log.d("hihi: ", response + "onOpen.5 ket noi thanh cong");
                webSocket.send("test");
                webSocket.send("subscribe:my-websocket-endpoint");
                webSocket.request();

            }
        });
    }

}