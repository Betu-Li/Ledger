package com.example.ledger.slice;

import com.example.ledger.DisplayFormat;
import com.example.ledger.ResourceTable;
import com.example.ledger.model.Record;
import com.example.ledger.provider.RecordProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.global.icu.text.SimpleDateFormat;
import ohos.global.icu.util.Calendar;
import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;
import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_PARENT;


public class BookSlice extends AbilitySlice {

    Uri uri = Uri.parse("dataability:///com.example.ledger.DataAbility/record");
    private DataAbilityHelper dataAbilityHelper;
    private DataAbilityPredicates predicates;
    String[] columns = {"id","year","month","day","time","amount","type","category"};


    private Text curDateText;
    private Text curWeekText;
    private Text dayInputText;
    private Text dayOutputText;
    private Text balanceText;
    private Image show_more_btn;
    private Image add_record_btn;
    private ListContainer curDayRecordContainer;// 当天记录
    private final String[] weeks={"","周日","周一","周二","周三","周四","周五","周六"};

    private Calendar calendar; // 用于记录程序当前的时间

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_book_layout);

        //addTestData();// 向数据库随机添加一些记录

        calendar = Calendar.getInstance();// 获取系统当前时间

        // 初始化页面
        initComponent();

        // 添加按钮响应事件
        addListener();

    }

    /**
     * 初始化页面（初始化页面组件）
     * done
     */
    private void initComponent() {
        // 获取页面组件
        curDateText = (Text) findComponentById(ResourceTable.Id_current_date_id);
        curWeekText = (Text) findComponentById(ResourceTable.Id_current_week);
        dayInputText = (Text) findComponentById(ResourceTable.Id_current_day_input_money);
        dayOutputText = (Text) findComponentById(ResourceTable.Id_current_day_output_money);
        balanceText = (Text) findComponentById(ResourceTable.Id_balance_text);
        curDayRecordContainer = (ListContainer) findComponentById(ResourceTable.Id_current_day_record_ListContainer) ;
        show_more_btn = (Image) findComponentById(ResourceTable.Id_show_more_btn);
        add_record_btn = (Image) findComponentById(ResourceTable.Id_add_record_btn);

        // 设置当前日期
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        curDateText.setText("今天"+sdf1.format(calendar.getTime()));

        // 设置当前星期
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        curWeekText.setText(weeks[calendar.get(Calendar.DAY_OF_WEEK)]);

        // 更新记录
        reloadRecord();
    }

    /**
     * 刷新记录列表
     * done
     * */
    private void reloadRecord(){
        // 查询当天结余信息
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        double []result= calculateToday(today);;
        double income = result[0];
        double outcome = result[1];
        double balance = result[2];
        dayInputText.setText("" + income);
        dayOutputText.setText("" + outcome);
        balanceText.setText("" + balance);

        // 查询当天的记录列表
        RecordProvider provider = new RecordProvider(getRecordsByDate(today),this);

        // 设置列表的点击事件
        provider.setListener(new RecordProvider.ItemListener() {
            @Override
            public void click(int i, Record record) {
                ShowRecordDetail(record);
            }
        });

        // 设置数据提供
        curDayRecordContainer.setItemProvider(provider);

    }

    /**
     * 获取当天的支出以及收入的金额
     * @return result[] 0:income 1:outcome 2:balance
     * deon
     */
    private double[]  calculateToday(String date){
        List<Record> records = getRecordsByDate(date);
        double income = 0;
        double outcome = 0;
        for(Record record : records){
            if(record.getType().equals("income")){
                income += record.getAmount();
            }else{
                outcome += record.getAmount();
            }
        }
        double balance = income - outcome;
        return new double[]{income, outcome, balance};
    }

    /**
     * 查询数据库中某一天的记录
     * done
     */
    private List<Record> getRecordsByDate(String date) {

        List<Record> records = new ArrayList<>();

        predicates =new DataAbilityPredicates();
        dataAbilityHelper = DataAbilityHelper.creator(this);

        String[] dates=splitDate(date);
        predicates.equalTo("year",dates[0]);
        predicates.equalTo("month",dates[1]);
        predicates.equalTo("day",dates[2]);

        try{
            ResultSet resultSet = dataAbilityHelper.query(uri,columns,predicates);
            while (resultSet.goToNextRow()) {
                String date1 = resultSet.getString(1) + "-" + resultSet.getString(2) + "-" + resultSet.getString(3);
                String time = resultSet.getString(4);
                double amount =resultSet.getDouble(5);
                String type =resultSet.getString(6);
                String cate =resultSet.getString(7);
                Record record = new Record(resultSet.getInt(0), date1, time, amount,type,cate);
                records.add(record);
            }
        } catch (DataAbilityRemoteException e) {
            throw new RuntimeException(e);
        }

        return records;
    }

    /**
     * 向数据库中添加一些测试数据
     * done
     */
    private void addTestData(){
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String testDate1 = today;
        String testTime1 = "09:00:00";
        double testAmount1 = 100.0;
        String testType1 = "income";
        String testCategory1 = "生活费";
        Record testRecord1 = new Record(testDate1, testTime1, testAmount1, testType1, testCategory1);
        insertRecord(testRecord1);

        String testDate2 = today;
        String testTime2 = "14:30:00";
        double testAmount2 = 50.0;
        String testType2 = "expense";
        String testCategory2 = "食品酒水";
        Record testRecord2 = new Record(testDate2, testTime2, testAmount2, testType2, testCategory2);
        insertRecord(testRecord2);

        String testDate3 = today;
        String testTime3 = "18:45:00";
        double testAmount3 = 200.0;
        String testType3 = "income";
        String testCategory3 = "收入";
        Record testRecord3 = new Record(testDate3, testTime3, testAmount3, testType3, testCategory3);
        insertRecord(testRecord3);

    }

    /**
     * 插入数据
     * done
     * */
    private void insertRecord(Record record){
        dataAbilityHelper = DataAbilityHelper.creator(this);
        ValuesBucket values = new ValuesBucket();
        values.putString("year", record.getDate().split("-")[0]);
        values.putString("month", record.getDate().split("-")[1]);
        values.putString("day", record.getDate().split("-")[2]);
        values.putString("time", record.getTime());
        values.putDouble("amount", record.getAmount());
        values.putString("type", record.getType());
        values.putString("category", record.getCategory());
        try{
            int result = dataAbilityHelper.insert(uri, values);
            //new ToastDialog(this).setText("插入结果成功！" ).show();
        }catch(DataAbilityRemoteException e){
            //new ToastDialog(this).setText("插入结果失败！" ).show();
            e.printStackTrace();
        }
    }


    /**
     * 展示详细信息
     * done
     * */
    private void ShowRecordDetail(Record record) {
        CommonDialog cd = new CommonDialog(getContext());
        cd.setCornerRadius(50);

        // 绑定layout文件
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_record_detail_layout,null,false);

        Image image = (Image) dl.findComponentById(ResourceTable.Id_icon_image);
        image.setPixelMap(DisplayFormat.getIconID(record.getCategory()));// 设置图片为类型图片

        Text money_text = (Text) dl.findComponentById(ResourceTable.Id_money_text);
        String flag = "-";
        if(record.getType().equals("income")){
            flag = "+";
        }
        money_text.setText(flag+String.format("%.2f",record.getAmount()));
        money_text.setTextColor(DisplayFormat.getMoneyColor(record.getType()));

        Text cate_text = (Text) dl.findComponentById(ResourceTable.Id_cate_text);
        cate_text.setText(record.getCategory());

        Text time_text = (Text) dl.findComponentById(ResourceTable.Id_time_text);
        time_text.setText(record.getDate()+' '+record.getTime());

//        Text memo_text = (Text) dl.findComponentById(ResourceTable.Id_memo_text);
//        memo_text.setText("未设置");

        Button btn_ok = (Button) dl.findComponentById(ResourceTable.Id_btn_ok);

        //设置按钮点击
        btn_ok.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                cd.destroy();
            }
        });
        
        Button btn_delete = (Button) dl.findComponentById(ResourceTable.Id_btn_delete);
        btn_delete.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                deleteRecordByID(record.getId());

                reloadRecord();

                cd.destroy();
            }
        });

        cd.setSize(MATCH_PARENT,MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.setAlignment(LayoutAlignment.BOTTOM);
        cd.show();

    }

    private  void addListener(){
        add_record_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
               AbilitySlice slice = new AddBookSlice();
               Intent intent = new Intent();
               present(slice,intent);

            }
        });

        show_more_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                CommonDialog cd = new CommonDialog(getContext());
                cd.setCornerRadius(50);
                DirectionalLayout dl =(DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_today_show_more_dialog,null,false);

                Image test_image = (Image) dl.findComponentById(ResourceTable.Id_test_image);
                Image count_image = (Image) dl.findComponentById(ResourceTable.Id_count_image);

                test_image.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        addTestData();
                        reloadRecord();
                        cd.destroy();
                    }
                });

                count_image.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        AbilitySlice slice = new ConditionalSlice();
                        Intent intent = new Intent();
                        present(slice,intent);
                        cd.destroy();
                    }
                });

                cd.setSize(MATCH_PARENT,400);
                cd.setContentCustomComponent(dl);
                cd.setAlignment(LayoutAlignment.BOTTOM);
                cd.show();
            }
        });


    }




    /**
     * 通过id删除某条记录
     * done
     */
    private void deleteRecordByID(int id) {
        dataAbilityHelper = DataAbilityHelper.creator(this);
        predicates = new DataAbilityPredicates();
        predicates.equalTo("id",id);
        try{
            int result = dataAbilityHelper.delete(uri,predicates);
        } catch (DataAbilityRemoteException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * 拆分年月日
     * @param date 日期
     * @return 拆分后的年月日,数组下标0为年，1为月，2为日
     * done
     */
    private String[] splitDate(String date){
        String year= date.split("-")[0];
        String month=date.split("-")[1];
        String day=date.split("-")[2];
        return new String[]{year, month, day};
    }

}
