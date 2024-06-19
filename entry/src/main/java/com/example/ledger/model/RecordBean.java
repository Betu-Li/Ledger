package com.example.ledger.model;

import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.PrimaryKey;
import ohos.global.icu.text.SimpleDateFormat;
import ohos.global.icu.util.Calendar;


@Entity(tableName = "record")
public class RecordBean extends OrmObject {
    public static final String INCOME = "收";
    public static final String PAY = "支";
    public static final String  INCOME_LABEL = "收入";
    public static final String  PAY_LABEL = "支出";

    @PrimaryKey(autoGenerate = true) // 主键自增
    private long id;
    
    private String kind;// 收入/支出 种类
    private String cateItem;// 细分种类
    private double money;// 金额
    private String memo; // 备注
    private long time; //时间
    private Integer year;
    private Integer month;
    private Integer day;

    public RecordBean(long id, String kind, String cateItem, double money, String memo, Calendar date) {
        this.id = id;
        this.kind = kind;
        this.cateItem = cateItem;
        this.money = money;
        this.memo = memo;
        // 获取当前时间
        this.time = date.getTimeInMillis(); 
        this.year = date.get(Calendar.YEAR);
        this.month = date.get(Calendar.MONTH);
        this.day = date.get(Calendar.DAY_OF_MONTH);
        // this.time = time
    }

    /**
     * 获取记录的创建时间
     *
     */
    public String getCreateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.id);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    /**
     * 获取记账时间
     * 
     */
    public String getRecordTime() {
       Calendar calendar = Calendar.getInstance();
       calendar.setTimeInMillis(this.time);
       calendar.set(Calendar.YEAR,getYear());
       calendar.set(Calendar.MONTH,getMonth());
       calendar.set(Calendar.DAY_OF_MONTH,getDay()); 
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy年-MM月-dd日 HH:mm:ss");
       
       return sdf.format(calendar.getTime());
    }

    /**
     * 获取简易记账时间
     *
     * */
    public String getRecordSimpleTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(calendar.getTime());
    }

    public RecordBean() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCateItem() {
        return cateItem;
    }

    public void setCateItem(String cateItem) {
        this.cateItem = cateItem;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "RecordBean{" +
                "id=" + id +
                ", kind='" + kind + '\'' +
                ", cateItem='" + cateItem + '\'' +
                ", money=" + money +
                ", memo='" + memo + '\'' +
                ", time=" + time +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }
}