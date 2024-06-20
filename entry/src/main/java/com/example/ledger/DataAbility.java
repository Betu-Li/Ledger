package com.example.ledger;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.MessageParcel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.*;

public class DataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    private RdbStore rbdStore;
    StoreConfig config = StoreConfig.newDefaultConfig("Bookkeeping.db");
    RdbOpenCallback callback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rbdStore) {
            rbdStore.executeSql("CREATE TABLE IF NOT EXISTS records (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "year TEXT , month TEXT , day TEXT ," +
                    "time TEXT ," +
                    "amount REAL not null," +
                    "type TEXT not null," +
                    "category TEXT not null)");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {

        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "DataAbility onStart");

        // 初始化数据库连接
        DatabaseHelper helper = new DatabaseHelper(this);
        rbdStore = helper.getRdbStore(config, 1, callback);
    }

    /**
     * 重写查询方法
     * @param uri 表示data ability的访问路径
     * @param columns 传递查询的列名
     * @param predicates 传递查询的条件
     * @return 返回查询结果
     */
    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates,"records");
        return rbdStore.query(rdbPredicates, columns);
    }


    /**
     * 重写插入方法
     * @param uri 表示data ability的访问路径
     * @param value 传递操作数据的容器
     * @return
     * */
    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "DataAbility insert");
        if (rbdStore == null) {
            return 0;
        }
        ValuesBucket values = new ValuesBucket();
        values.putString("year", value.getString("year"));
        values.putString("month", value.getString("month"));
        values.putString("day", value.getString("day"));
        values.putString("time", value.getString("time"));
        values.putDouble("amount", value.getDouble("amount"));
        values.putString("type", value.getString("type"));
        values.putString("category", value.getString("category"));
        rbdStore.insert("records", values);
        return 1;
    }

    /**
     * 重写删除方法
     * @param uri 表示data ability的访问路径
     * @param predicates 传递删除的条件
     * @return
     * */
    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "records");
        return rbdStore.delete(rdbPredicates);
    }

    /**
     * 重写更新方法
     * @param uri 表示data ability的访问路径
     * @param value 传递操作数据的容器
     * @param predicates 传递更新的条件
     * @return
     */
    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, "records");
        ValuesBucket values = new ValuesBucket();
        values.putString("year", value.getString("year"));
        values.putString("month", value.getString("month"));
        values.putString("day", value.getString("day"));
        values.putString("time", value.getString("time"));
        values.putDouble("amount", value.getDouble("amount"));
        values.putString("type", value.getString("type"));
        values.putString("category", value.getString("category"));
        return rbdStore.update(values, rdbPredicates);
    }


    @Override
    public FileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = new File(uri.getDecodedPathList().get(0));//get(0)是获取URI完整字段中查询参数字段。
        if (mode == null || !"rw".equals(mode)){
            file.setReadOnly();
        }
        FileInputStream fileIs = new FileInputStream(file);
        FileDescriptor fd = null;
        try {
            fd = fileIs.getFD();
        }catch (IOException e){
            HiLog.info(LABEL_LOG, "failed to getFD");
        }
        // 绑定文件描述符
        return MessageParcel.dupFileDescriptor(fd);
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}