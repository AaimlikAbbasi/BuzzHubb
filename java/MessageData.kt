package com.example.buzzhub

data class MessageData(var sender: String, var receiver: String, val message: String, val time: String)
{
    constructor() : this("", "", "", "")
}
