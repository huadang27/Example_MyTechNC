package com.example.example_btl_androidnc.students.adapter;

import static com.example.example_btl_androidnc.students.adapter.CourseAdapter.convertDateFormat;

import com.example.example_btl_androidnc.students.model.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

//public class ScheduleDateComparator implements Comparator<Schedule> {
//    @Override
//    public int compare(Schedule s1, Schedule s2) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        try {
//            Date date1 = sdf.parse(convertDateFormat(s1.getDay()));
//            Date date2 = sdf.parse(convertDateFormat(s2.getDay()));
//            return date1.compareTo(date2);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }
//}
public class ScheduleDateComparator implements Comparator<Schedule> {
    @Override
    public int compare(Schedule s1, Schedule s2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date1 = sdf.parse(convertDateFormat(s1.getDay()));
            Date date2 = sdf.parse(convertDateFormat(s2.getDay()));
            int dateComparison = date1.compareTo(date2);

            if (dateComparison == 0) {
                // Nếu ngày giống nhau, sắp xếp theo 'number' tăng dần
                return Integer.compare(s1.getNumber(), s2.getNumber());
            } else {
                // Nếu ngày khác nhau, sắp xếp theo ngày tăng dần
                return dateComparison;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}