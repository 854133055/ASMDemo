package com.mmlok.lib;

public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello World");
    }

    public void test(boolean outPut){
        long var0 = 1;
        var0 = var0 + 2;
        int var1 = (int) var0;
        if (outPut) {
           System.out.println(var1 + System.currentTimeMillis());
        }
    }
}