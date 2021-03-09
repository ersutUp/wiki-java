package top.ersut.reactive;

import java.util.Observable;

//继承Observable
public class ObserverDemo extends Observable {
    public static void main(String[] args) {
        ObserverDemo demo = new ObserverDemo();
        //实现Observer接口并添加到Observable
        demo.addObserver((o,arg)->{
            System.out.println("观察者1:"+arg);
        });
        demo.addObserver((o,arg)->{
            System.out.println("观察者2:"+arg);
        });

        //设置变化点
        demo.setChanged();
        //通知变化
        demo.notifyObservers(11);
    }
}
