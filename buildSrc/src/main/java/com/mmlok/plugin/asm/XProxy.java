package com.mmlok.plugin.asm;

public interface XProxy {

    byte[] invoke(byte[] sourceClass,  String className);
}
