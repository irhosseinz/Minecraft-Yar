package ir.boozar.minecraft;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Tools{
    public static String addCommas(String in){
        NumberFormat formatter = new DecimalFormat("###,###,###,###.###");
        //long l=Long.parseLong(in);
        float l=Float.parseFloat(in);
        return formatter.format(l);
        /*String rgx="^(\\d+)(\\d{3},)";
        String o=in+",";
        while(o.matches(rgx)){
            Log.i("hz","addCom0:"+o);
            o=o.replaceAll(rgx,"$1,$2");
            Log.i("hz","addCom1:"+o);
        }
        Log.i("hz", "addComE:" + o);
        return o.substring(0,o.length()-1);*/
    }
    public static String formatNumbers(String i){
        return numberToPersian(addCommas(i));
    }
    public static String numberToPersian(String input){
        String[] map=new String[]{
                "۰"
                ,"۱"
                ,"۲"
                ,"۳"
                ,"۴"
                ,"۵"
                ,"۶"
                ,"۷"
                ,"۸"
                ,"۹"
        };
        int l=input.length();
        String out=input;
        for(int i=0;i<10;i++){
            out=out.replace(i+"",map[i]);
        }
        return out;
    }
    public static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            Boolean first=true;
            String l;
            while ((line = reader.readLine()) != null) {
                if(first){
                    first=false;
                    l=line;
                }else
                    l="\n"+line;
                sb.append(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }





    public static class PersianDate {
        public int hour=1;
        public int minute=0;

        public PersianDate()
        {
            Calendar calendar = new GregorianCalendar();

            hour=calendar.get(Calendar.HOUR_OF_DAY);
            minute=calendar.get(Calendar.MINUTE);

            setGregorianDate(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH));
        }
        public PersianDate(long time)
        {
            Calendar calendar = Calendar.getInstance();
            Date d=new Date(time);
            calendar.setTime(d);
            //Log.i("hz", "new PDate:"+time);
            //Log.i("hz","time0:"+calendar.getTime());

            hour=calendar.get(Calendar.HOUR_OF_DAY);
            minute=calendar.get(Calendar.MINUTE);

            //Log.i("hz","time1:"+calendar.getTime());
            setGregorianDate(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
        }

        public void setTime(int hour,int minute){
            this.hour=hour;
            this.minute=minute;
        }

        public PersianDate(int year, int month, int day)
        {
            //JalaliToGre d=new JalaliToGre(year,month,day);
            //Calendar c=d.getGreDate();
            //gYear=c.get(Calendar.YEAR);
            //gMonth=c.get(Calendar.MONTH);
            //gDay=c.get(Calendar.DAY_OF_MONTH);
            //Log.i("hz", "new PDate:"+year + "," + month + "," + day);
            setIranianDate(year, month, day);
        }
        public long getUnixTime(){
            Calendar calendar = Calendar.getInstance();
            //Log.i("hz","getTimeV:"+String.format("%d,%d,%d,%d,%d",gYear,gMonth,gDay,hour,minute));
            calendar.set(gYear,gMonth-1,gDay,hour,minute);
            long time=calendar.getTimeInMillis();
            //Log.i("hz","getTime:"+time);
            calendar.clear();
            return time;
        }
        public int getDayOfYear(){
            Calendar calendar = Calendar.getInstance();
            //Log.i("hz","getTimeV:"+String.format("%d,%d,%d,%d,%d",gYear,gMonth,gDay,hour,minute));
            calendar.set(gYear,gMonth-1,gDay,hour,minute);
            return calendar.get(Calendar.DAY_OF_YEAR);
        }
        public long getUnixTime(int hour,int minute){
            Calendar calendar = Calendar.getInstance();
            calendar.set(gYear,gMonth-1,gDay,hour,minute);
            long time=calendar.getTimeInMillis();
            //Log.i("hz","setG:"+gYear+","+gMonth+","+gDay);
            calendar.clear();
            return time;
        }
        public int getDayPassed(long date){
            long current=System.currentTimeMillis();
            return (int)((current-date)/(24*3600*1000));
        }
        public String getIranianDate(int type){
            switch (type){
                case 1:
                    return irMonth+"/"+irDay;
                case 2:
                    return String.format(new Locale("fa"),"%02d %s %s",irDay,getMonthStrPer(),String.valueOf(irYear).substring(2));
                case 3:
                    return (String.valueOf(irYear).substring(2)+"/"+irMonth+"/"+irDay);
                case 4:
                    return String.format(new Locale("fa"),"%02d %s %04d %02d:%02d",irDay,getMonthStrPer(),irYear,hour,minute);
                case 5:
                    return String.format(new Locale("fa"),"%02d %s %02d:%02d",irDay,getMonthStrPer(),hour,minute);
                case 6:
                    return String.format(new Locale("fa"),"%04d/%02d/%02d %02d:%02d",irYear,irMonth,irDay,hour,minute);
                case 7:
                    return String.format(new Locale("fa"),"%02d:%02d",hour,minute);
                case 8:
                    return String.format(new Locale("en"),"%s%02d%02d%02d%02d",String.valueOf(irYear).substring(2),irMonth,irDay,hour,minute);
                default:
                    return (irYear+"/"+irMonth+"/"+irDay);
            }
        }

        public String getGregorianDate()
        {
            return (gYear+"/"+gMonth+"/"+gDay);
        }

        public String getJulianDate()
        {
            return (juYear+"/"+juMonth+"/"+juDay);
        }

        public String getWeekDayStr()
        {
            String weekDayStr[]={
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday",
                    "Sunday"};
            return (weekDayStr[getDayOfWeek()]);
        }

        public String getWeekDayPer()
        {
            String weekDayStr[]={
                    "دوشنبه",
                    "سه شنبه",
                    "چهاشنبه",
                    "پنج شنبه",
                    "جمعه",
                    "شنبه",
                    "یک شنبه"};
            return (weekDayStr[getDayOfWeek()]);
        }

        public String getMonthStrPer()
        {
            String MonthStr[]={
                    "فروردین",
                    "اردیبهشت ",
                    "خرداد",
                    "تیر",
                    "مرداد",
                    "شهریور",
                    "مهر",
                    "آبان",
                    "آذر",
                    "دی",
                    "بهمن",
                    "اسفند"
            };
            return (MonthStr[irMonth-1]);
        }

        public String toString()
        {
            return (getWeekDayStr()+
                    ", Gregorian:["+getGregorianDate()+
                    "], Julian:["+getJulianDate()+
                    "], Iranian:["+getIranianDate(1)+"]");
        }

        public int getDayOfWeek()
        {
            return (JDN % 7);
        }

        public void nextDay()
        {
            JDN++;
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }

        public void nextDay(int days)
        {
            JDN+=days;
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }

        public void previousDay()
        {
            JDN--;
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }

        public void previousDay(int days)
        {
            JDN-=days;
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }

        public void setIranianDate(int year, int month, int day)
        {
            irYear =year;
            irMonth = month;
            irDay = day;
            JDN = IranianDateToJDN();
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }

        public void setGregorianDate(int year, int month, int day)
        {
            gYear = year;
            gMonth = month;
            gDay = day;
            //Log.i("hz","setG0:"+gYear+","+gMonth+","+gDay);
            JDN = gregorianDateToJDN(year,month,day);
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }

        public void setJulianDate(int year, int month, int day)
        {
            juYear = year;
            juMonth = month;
            juDay = day;
            JDN = julianDateToJDN(year,month,day);
            JDNToIranian();
            JDNToJulian();
            JDNToGregorian();
        }
        private void IranianCalendar()
        {
            // Iranian years starting the 33-year rule
            int Breaks[]=
                    {-61, 9, 38, 199, 426, 686, 756, 818,1111,1181,
                            1210,1635,2060,2097,2192,2262,2324,2394,2456,3178} ;
            int jm,N,leapJ,leapG,jp,j,jump;
            gYear = irYear + 621;
            leapJ = -14;
            jp = Breaks[0];
            // Find the limiting years for the Iranian year 'irYear'
            j=1;
            do{
                jm=Breaks[j];
                jump = jm-jp;
                if (irYear >= jm)
                {
                    leapJ += (jump / 33 * 8 + (jump % 33) / 4);
                    jp = jm;
                }
                j++;
            } while ((j<20) && (irYear >= jm));
            N = irYear - jp;
            // Find the number of leap years from AD 621 to the begining of the current
            // Iranian year in the Iranian (Jalali) calendar
            leapJ += (N/33 * 8 + ((N % 33) +3)/4);
            if ( ((jump % 33) == 4 ) && ((jump-N)==4))
                leapJ++;
            // And the same in the Gregorian date of Farvardin the first
            leapG = gYear/4 - ((gYear /100 + 1) * 3 / 4) - 150;
            march = 20 + leapJ - leapG;
            // Find how many years have passed since the last leap year
            if ( (jump - N) < 6 )
                N = N - jump + ((jump + 4)/33 * 33);
            leap = (((N+1) % 33)-1) % 4;
            if (leap == -1)
                leap = 4;
        }
        public boolean IsLeap(int irYear1)
        {
            // Iranian years starting the 33-year rule
            int Breaks[]=
                    {-61, 9, 38, 199, 426, 686, 756, 818,1111,1181,
                            1210,1635,2060,2097,2192,2262,2324,2394,2456,3178} ;
            int jm,N,leapJ,leapG,jp,j,jump;
            gYear = irYear1 + 621;
            leapJ = -14;
            jp = Breaks[0];
            // Find the limiting years for the Iranian year 'irYear'
            j=1;
            do{
                jm=Breaks[j];
                jump = jm-jp;
                if (irYear1 >= jm)
                {
                    leapJ += (jump / 33 * 8 + (jump % 33) / 4);
                    jp = jm;
                }
                j++;
            } while ((j<20) && (irYear1 >= jm));
            N = irYear1 - jp;
            // Find the number of leap years from AD 621 to the begining of the current
            // Iranian year in the Iranian (Jalali) calendar
            leapJ += (N/33 * 8 + ((N % 33) +3)/4);
            if ( ((jump % 33) == 4 ) && ((jump-N)==4))
                leapJ++;
            // And the same in the Gregorian date of Farvardin the first
            leapG = gYear/4 - ((gYear /100 + 1) * 3 / 4) - 150;
            march = 20 + leapJ - leapG;
            // Find how many years have passed since the last leap year
            if ( (jump - N) < 6 )
                N = N - jump + ((jump + 4)/33 * 33);
            leap = (((N+1) % 33)-1) % 4;
            if (leap == -1)
                leap = 4;
            if (leap==4 || leap==0)
                return true;
            else
                return false;

        }
        private int IranianDateToJDN()
        {
            IranianCalendar();
            return (gregorianDateToJDN(gYear,3,march)+ (irMonth-1) * 31 - irMonth/7 * (irMonth-7) + irDay -1);
        }
        private void JDNToIranian()
        {
            JDNToGregorian();
            irYear = gYear - 621;
            IranianCalendar(); // This invocation will update 'leap' and 'march'
            int JDN1F = gregorianDateToJDN(gYear,3,march);
            int k = JDN - JDN1F;
            if (k >= 0)
            {
                if (k <= 185)
                {
                    irMonth = 1 + k/31;
                    irDay = (k % 31) + 1;
                    return;
                }
                else
                    k -= 186;
            }
            else
            {
                irYear--;
                k += 179;
                if (leap == 1)
                    k++;
            }
            irMonth = 7 + k/30;
            irDay = (k % 30) + 1;
        }
        private int julianDateToJDN(int year, int month, int day)
        {
            return (year + (month - 8) / 6 + 100100) * 1461/4 + (153 * ((month+9) % 12) + 2)/5 + day - 34840408;
        }
        private void JDNToJulian()
        {
            int j= 4 * JDN + 139361631;
            int i= ((j % 1461)/4) * 5 + 308;
            juDay = (i % 153) / 5 + 1;
            juMonth = ((i/153) % 12) + 1;
            juYear = j/1461 - 100100 + (8-juMonth)/6;
        }
        private int gregorianDateToJDN(int year, int month, int day)
        {
            int jdn = (year + (month - 8) / 6 + 100100) * 1461/4 + (153 * ((month+9) % 12) + 2)/5 + day - 34840408;
            jdn = jdn - (year + 100100+(month-8)/6)/100*3/4+752;
            return (jdn);
        }
        private void JDNToGregorian()
        {
            int j= 4 * JDN + 139361631;
            j = j + (((((4* JDN +183187720)/146097)*3)/4)*4-3908);
            int i= ((j % 1461)/4) * 5 + 308;
            gDay = (i % 153) / 5 + 1;
            gMonth = ((i/153) % 12) + 1;
            gYear = j/1461 - 100100 + (8-gMonth)/6;
        }


        public int irYear; // Year part of a Iranian date
        public int irMonth; // Month part of a Iranian date
        public int irDay; // Day part of a Iranian date
        private int gYear; // Year part of a Gregorian date
        private int gMonth; // Month part of a Gregorian date
        private int gDay; // Day part of a Gregorian date
        private int juYear; // Year part of a Julian date
        private int juMonth; // Month part of a Julian date
        private int juDay; // Day part of a Julian date
        private int leap; // Number of years since the last leap year (0 to 4)
        public int JDN; // Julian Day Number
        private int march; // The march day of Farvardin the first (First day of jaYear)
    }
}