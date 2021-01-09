package com.verkoopapp.models


class SocketCheckConnectionEvent(var args:String,val tag:String)


class SocketOnReceiveEvent(var args: Array<Any>,val tag:String)