package com.lyw.cofig;

/**
 * Created by Lenovo on 2018/10/11.
 */
public class ClassicSantana implements CarInterface {
    @Override
    public void radio() {
        System.out.println("经典桑塔纳收音机正在播放");
    }

    @Override
    public void light() {
        System.out.println("经典桑塔纳灯光打开");
    }
}
