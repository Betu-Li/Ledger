package com.example.ledger;

import com.example.ledger.slice.BookSlice;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(BookSlice.class.getName());// 设置程序入口
    }
}
