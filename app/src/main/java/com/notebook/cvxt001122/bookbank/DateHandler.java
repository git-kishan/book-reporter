package com.notebook.cvxt001122.bookbank;

import java.util.Calendar;

public class DateHandler {
    private Calendar calendar;

    public DateHandler(Calendar calendar){
        this.calendar=calendar;
    }

    public String getFormatedDate(){
        int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        StringBuilder builder = new StringBuilder();
        builder.append(date+" "+getMonthName(month)+", "+year);
        return builder.toString();
    }

    public String getMonthName(int i){
        String [] monthName=new String[]{"jan","feb","march","april","may","june","july"
        ,"aug","sep","oct","nov","dec"};
        return monthName[i];
    }
    public String getReturningDate( Calendar calendar){
        int date=calendar.get(Calendar.DATE);
        int month=calendar.get(Calendar.MONTH);
        int year=calendar.get(Calendar.YEAR);
        int actualNumberOfDaysInMonth=tellNumberOfDays(year, month);
        return tellReturningDate(date,month ,year , actualNumberOfDaysInMonth);

    }

    private String tellReturningDate(int date,int month,int year,int actualNumberOfDayInMonth){

        if(date<=actualNumberOfDayInMonth && date+15<=actualNumberOfDayInMonth){
            date=date+15;
            return "/"+date+"/"+month+"/"+year;
        }
        else if(date+15>actualNumberOfDayInMonth && year<11){
            month =month+1;
            date=date+15-actualNumberOfDayInMonth;
            return "/"+date+"/"+month+"/"+year;
        }
        else if(date+15>actualNumberOfDayInMonth&&year==11){
            month=0;
            date=date+15-actualNumberOfDayInMonth;
            year=year+1;
            return "/"+date+"/"+month+"/"+year;
        }
        else
            return "nothing";

    }

    private int tellNumberOfDays(int year,int month){

        if(checkLeapYear(year)) {
            int[] numberOfDayInMonthInCommonYear = new int[]{
                    31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            };
            return numberOfDayInMonthInCommonYear[month];
        }else {
            int[] numberOfDayInMonthInLeapYear = new int[]{
                    31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            };
            return numberOfDayInMonthInLeapYear[month];
        }


    }
    private boolean checkLeapYear(int year){

        boolean check;
        if(year % 4 == 0)
        {
            if( year % 100 == 0)
            {
                if ( year % 400 == 0)
                    check = true;
                else
                    check = false;
            }
            else
                check = true;
        }
        else
            check = false;


        return check;
    }

}

