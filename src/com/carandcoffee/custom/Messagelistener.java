package com.carandcoffee.custom;

public interface Messagelistener {

abstract void handlemessage(info msg);
abstract void handlvoicmessage(info msg);
abstract void handlimagemessage(info msg);
}
