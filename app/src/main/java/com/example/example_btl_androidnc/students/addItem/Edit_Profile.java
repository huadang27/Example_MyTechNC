package com.example.example_btl_androidnc.students.addItem;

import static com.example.example_btl_androidnc.students.adapter.CourseAdapter.convertDateFormat;
import static com.example.example_btl_androidnc.students.api.RetrofitClient.BASE_IMG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.example.example_btl_androidnc.R;
import com.example.example_btl_androidnc.students.api.GetAPI_Service;
import com.example.example_btl_androidnc.students.api.RetrofitClient;
import com.example.example_btl_androidnc.students.database.ImageDownloader;
import com.example.example_btl_androidnc.students.database.MySharedPreferences;
import com.example.example_btl_androidnc.students.fragment.Admin_HomeFragment;
import com.example.example_btl_androidnc.students.model.ImageHelper;
import com.example.example_btl_androidnc.students.model.UpdateProfileReq;
import com.example.example_btl_androidnc.students.model.UserCourse;
import com.example.example_btl_androidnc.students.model.Users;
import com.example.example_btl_androidnc.teachers.activity.SetTeacher_Activity;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Edit_Profile extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private ImageView logoDefaultImageView;
    EditText edt_name, edt_address, edt_phone, edtNgaysinh;
    private Uri selectedImageUri;
    private String selectedDate;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private MySharedPreferences mySharedPreferences;
    String TAG = "RequestBodyData";
    private Users users;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        edt_name = findViewById(R.id.edt_name);
        edt_address = findViewById(R.id.edt_address);
        edt_phone = findViewById(R.id.edt_phone);
        genderRadioGroup = findViewById(R.id.gender_radiogroup);
        maleRadioButton = findViewById(R.id.male_radiobutton);
        femaleRadioButton = findViewById(R.id.female_radiobutton);
        edtNgaysinh = findViewById(R.id.edtNgaysinh);
        logoDefaultImageView = findViewById(R.id.avatar_imageview);
        progressBar = findViewById(R.id.progressBar);

        mySharedPreferences = new MySharedPreferences(this);
        getDataProfile();


        edtNgaysinh.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(view);
                }
            }
        });


        logoDefaultImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edt_name.getText().toString();
                String address = edt_address.getText().toString();
                String phone = edt_phone.getText().toString();

                if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || genderRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(Edit_Profile.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(Edit_Profile.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar dobCalendar = Calendar.getInstance();
                Calendar nowCalendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date dob = (selectedDate == null) ? sdf.parse(convertDateFormat(users.getDateOfBirth())) : sdf.parse(selectedDate);
                    dobCalendar.setTime(dob);

                    int yearsDiff = nowCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
                    int monthsDiff = nowCalendar.get(Calendar.MONTH) - dobCalendar.get(Calendar.MONTH);
                    int daysDiff = nowCalendar.get(Calendar.DAY_OF_MONTH) - dobCalendar.get(Calendar.DAY_OF_MONTH);

                    if (monthsDiff < 0 || (monthsDiff == 0 && daysDiff < 0)) {
                        yearsDiff--;
                    }

                    if (yearsDiff < 18) {
                        Toast.makeText(Edit_Profile.this, "Bạn phải từ 18 tuổi trở lên", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                String gender;
                if (selectedGenderId == R.id.male_radiobutton) {
                    gender = "Male";
                } else if (selectedGenderId == R.id.female_radiobutton) {
                    gender = "Female";
                } else {
                    // Nếu không có RadioButton nào được chọn, bạn có thể đặt giá trị mặc định hoặc hiển thị thông báo lỗi
                    gender = "Female";
                }

                UpdateProfileReq req = new UpdateProfileReq();
                req.setName(edt_name.getText().toString());
                req.setGender(gender);
                req.setPhone(edt_phone.getText().toString());
                req.setAddress(edt_address.getText().toString());
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    String dateStr = (selectedDate == null) ? convertDateFormat(users.getDateOfBirth()) : selectedDate;
                    Date date = inputFormat.parse(dateStr);
                    String formattedDate = outputFormat.format(date);
                    req.setDateOfBirth(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                RequestBody reqPart = convertUpdateProfileReqToRequestBody(req);

                MultipartBody.Part imagePart = null;
                if (selectedImageUri != null) {
                    imagePart = prepareFilePart("image", selectedImageUri);
                    updateProfile(reqPart, imagePart);
                } else {
                    if (users.getImage() != null) {
                        ImageDownloader imageDownloader = new ImageDownloader(Edit_Profile.this, new ImageDownloader.OnImageDownloadedListener() {
                            @Override
                            public void onImageDownloaded(MultipartBody.Part imagePart) {
                                updateProfile(reqPart, imagePart);
                            }
                        });
                        imageDownloader.execute(BASE_IMG + users.getImage());
                    } else {
                        updateProfile(reqPart, null);
                    }
                }
            }
        });
    }

    private void updateProfile(RequestBody reqPart, MultipartBody.Part imagePart) {
        progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar
        GetAPI_Service getAPI_service = RetrofitClient.getClient().create(GetAPI_Service.class);

        getAPI_service.updateProfile(reqPart, imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Xử lý kết quả thành công
                    progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
                        Toast.makeText(Edit_Profile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                else
                    Toast.makeText(Edit_Profile.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
                Log.d(TAG, "onFailure: " + t.toString());
                Toast.makeText(Edit_Profile.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            logoDefaultImageView.setImageURI(selectedImageUri);
        }
    }



    private RequestBody convertUpdateProfileReqToRequestBody(UpdateProfileReq req) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String jsonString = gson.toJson(req);
        return RequestBody.create(JSON, jsonString);
    }


    public void showDatePickerDialog(View v) {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Xử lý khi chọn ngày
                        selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        edtNgaysinh.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] imageBytes = ImageHelper.getBytesFromInputStream(inputStream);
            File file = ImageHelper.convertBytesToFile(imageBytes, this);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);

            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getDataProfile() {
        users = (Users) getIntent().getSerializableExtra("user");
        Log.d(TAG, users.toString());
        edt_name.setText(checkNull(users.getName(), ""));
        edtNgaysinh.setText(checkNull(convertDateFormat(users.getDateOfBirth()), ""));
        edt_address.setText(checkNull(users.getAddress(), ""));
        edt_phone.setText(checkNull(users.getPhone(), ""));

        if (users.getImage() != null) {
            Glide.with(logoDefaultImageView.getContext())
                    .load(BASE_IMG + users.getImage())
                    .into(logoDefaultImageView);
        } else {
            logoDefaultImageView.setImageResource(R.drawable.logo_default);
        }

        String gender = checkNull(users.getGender(), "");
        if ("Male".equals(gender)) {
            maleRadioButton.setChecked(true);
        } else if ("Female".equals(gender)) {
            femaleRadioButton.setChecked(true);
        } else {
            maleRadioButton.setChecked(false);
            femaleRadioButton.setChecked(false);
        }
    }

    public static <T> T checkNull(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }


    private boolean isValidPhoneNumber(String phoneNumber) {
        // Biểu thức chính quy kiểm tra số điện thoại có đúng 10 chữ số
        String phoneNumberPattern = "^\\d{10}$";
        return phoneNumber.matches(phoneNumberPattern);
    }

}