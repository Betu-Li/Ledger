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

public class ConditionalSlice extends AbilitySlice {
    Uri uri = Uri.parse("dataability:///com.example.ledger.DataAbility/record");
    private DataAbilityHelper dataAbilityHelper;
    private DataAbilityPredicates predicates;
    String[] columns = {"id","year","month","day","time","amount","type","category"};


    private Text InputText;
    private Text OutputText;
    private Text BalanceText;
    private Text conditional_text;
    private Text conditional_year_month;
    private ListContainer curDayRecordContainer; //记录列表

    int Index = 0;//页面序号
    String date; //保存查询时间



    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_conditional_layout);

        //初始化页面
        initComponent();

        //切换按钮响应事件
        addListener();
    }

    private void addListener() {
        conditional_text.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (Index == 0){
                    Index =1;
                }else{
                    Index =0;
                }
                date = new SimpleDateFormat("yyyy-MM").format(new Date());
                reloadRecord(Index);

            }
        });
        conditional_year_month.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                CommonDialog cd =new CommonDialog(getContext());
                cd.setCornerRadius(50);

                // 绑定layout文件
                DirectionalLayout dl;
                if(Index == 0){
                    dl=(DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_month_layout,null,false);
                }else{
                    dl=(DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_year_layout,null,false);
                }


                //设置picker
                DatePicker datePicker =(DatePicker) dl.findComponentById(ResourceTable.Id_date_picker);
                datePicker.setWheelModeEnabled(true);//可滑动

                Button btn_cancel =(Button) dl.findComponentById(ResourceTable.Id_calender_cancel);
                btn_cancel.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        cd.destroy();
                    }
                });
                Button btn_ok = (Button) dl.findComponentById(ResourceTable.Id_calender_ok);
                btn_ok.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        int year = datePicker.getYear();
                        int month = 11;
                        if (Index ==0) {
                            month = datePicker.getMonth();
                            conditional_year_month.setText(year+"年"+month+"月");
                        }else{
                            conditional_year_month.setText(year+"年");
                        }
                        date = String.format("%04d-%02d", year, month);

                        reloadRecord(Index);

                        cd.destroy();
                    }
                });
                cd.setSize(MATCH_PARENT,MATCH_CONTENT);
                cd.setContentCustomComponent(dl);
                cd.setAlignment(LayoutAlignment.BOTTOM);
                cd.show();
            }
        });
    }

    /**
     * 初始化页面
     */
    private void initComponent() {
        //获取页面组件
        InputText = (Text) findComponentById(ResourceTable.Id_Input_money);
        OutputText = (Text) findComponentById(ResourceTable.Id_Output_money);
        BalanceText = (Text) findComponentById(ResourceTable.Id_balance_money);
        conditional_text= (Text) findComponentById(ResourceTable.Id_conditional_text);
        conditional_year_month = (Text) findComponentById(ResourceTable.Id_conditional_year_month);
        curDayRecordContainer = (ListContainer) findComponentById(ResourceTable.Id_record_ListContainer) ;

        date = new SimpleDateFormat("yyyy-MM").format(new Date());//获取当前月份//初始化时设置为今本月
        // 刷新页面
        reloadRecord(Index);
    }

    /**
     * 刷新记录
     */
    private void reloadRecord(int i){
        List<Record> records;
        if(i ==0){
            records = getRecordsByMonth(date);//查询当月的记录
            double[] result = calculateMonth(date);// 计算当月结余情况
            InputText.setText("" + result[0]);
            OutputText.setText("" + result[1]);
            BalanceText.setText("" + result[2]);

            String[] dates=splitDate(date);
            conditional_text.setText("按月记录：");
            conditional_year_month.setText(dates[0]+"年"+dates[1]+"月");

        }else{
            records = getRecordsByYear(date);
            //查询今年的记录
            double[] result = calculateYear(date);
            InputText.setText("" + result[0]);
            OutputText.setText("" + result[1]);
            BalanceText.setText("" + result[2]);

            conditional_text.setText("按年记录：");
            String[] dates=splitDate(date);
            conditional_year_month.setText(dates[0]+"年");

        }

        //设置列表点击事件
        RecordProvider provider = new RecordProvider(records,this);
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

                reloadRecord(Index);

                cd.destroy();
            }
        });

        cd.setSize(MATCH_PARENT,MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.setAlignment(LayoutAlignment.BOTTOM);
        cd.show();

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
     * 查询一年的记录
     */
    private List<Record> getRecordsByYear(String date) {
        List<Record> records = new ArrayList<>();
        predicates = new DataAbilityPredicates();
        dataAbilityHelper = DataAbilityHelper.creator(this);

        String[] dates=splitDate(date);
        predicates.equalTo("year",dates[0]);

        try{
            ResultSet resultSet = dataAbilityHelper.query(uri, columns, predicates);
            while (resultSet.goToNextRow()) {
                Record record = new Record();
                record.setId(resultSet.getInt(0));
                record.setDate(resultSet.getString(1) + "-" + resultSet.getString(2) + "-" + resultSet.getString(3));
                record.setTime(resultSet.getString(4));
                record.setAmount(resultSet.getDouble(5));
                record.setType(resultSet.getString(6));
                record.setCategory(resultSet.getString(7));
                records.add(record);
            }
        }catch (DataAbilityRemoteException e){
            e.printStackTrace();
        }
        return records;
    }


    /**
     * 查询一月的记录
     */
    private List<Record> getRecordsByMonth(String date) {
        List<Record> records = new ArrayList<>();
        predicates = new DataAbilityPredicates();
        dataAbilityHelper = DataAbilityHelper.creator(this);

        String[] dates=splitDate(date);
        predicates.equalTo("year",dates[0]);
        predicates.equalTo("month",dates[1]);
        try{
            ResultSet resultSet = dataAbilityHelper.query(uri, columns, predicates);
            while (resultSet.goToNextRow()) {
                Record record = new Record();
                record.setId(resultSet.getInt(0));
                record.setDate(resultSet.getString(1) + "-" + resultSet.getString(2) + "-" + resultSet.getString(3));
                record.setTime(resultSet.getString(4));
                record.setAmount(resultSet.getDouble(5));
                record.setType(resultSet.getString(6));
                record.setCategory(resultSet.getString(7));
                records.add(record);
            }
        }catch (DataAbilityRemoteException e){
            e.printStackTrace();
        }

        return records;
    }
    /**
     * 计算选择月的收支情况
     * @param date yyyy-MM
     * @return double[]，0为收入，1为支出，2为结余
     */
    public double[] calculateMonth(String date){
        List<Record> records = getRecordsByMonth(date);
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
     * 计算选择年的收支情况
     * @param date yyyy
     * @return double[]，0为收入，1为支出，2为结余
     */
    public double[] calculateYear(String date){
        List<Record> records = getRecordsByYear(date);
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
     * 拆分年月
     * @param date 日期
     * @return 拆分后的年月日,数组下标0为年，1为月
     * done
     */
    private String[] splitDate(String date){
        String year= date.split("-")[0];
        String month=date.split("-")[1];
        return new String[]{year, month};
    }
}