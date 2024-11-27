package com.example.web_voip;

import io.flutter.plugin.common.MethodChannel;

public class EventNotifier
{
    public MethodChannel methodChannel;

    public EventNotifier(MethodChannel methodChannel)
    {
        this.methodChannel = methodChannel;
    }

    public void notifyEvent(String eventName, Object eventData)
    {
        methodChannel.invokeMethod(eventName, eventData);
    }
}