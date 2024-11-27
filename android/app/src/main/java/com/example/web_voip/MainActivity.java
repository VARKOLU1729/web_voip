package com.example.web_voip;

import android.content.Context;

import androidx.annotation.NonNull;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import com.mizuvoip.jvoip.*;

public class MainActivity extends FlutterActivity
{
    String LOGTAG = "MIZU";
    String CHANNEL =  "mizu";
    SipStack mysipclient = null;
    Context context;
    public EventNotifier eventNotifier;//this is a custom class

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();// Initialize context here
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        MethodChannel methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL);
        eventNotifier = new EventNotifier(methodChannel);

        methodChannel.setMethodCallHandler((call, result)->{
            String method = call.method;
            switch (method)
            {
                case "init":
                    mysipclient = new SipStack();
                    mysipclient.Init(context);
                    MyNotificationListener listener = new MyNotificationListener();
                    mysipclient.SetNotificationListener(listener);
                    Log.i(LOGTAG, "mizu already initialised");
                    result.success("already initialised");
                    break;

                case "start":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot start because SipStack is null", null);
                    }
                    else {
                        String serverAddress = call.argument("serverAddress");
                        String userName = call.argument("userName");
                        String password = call.argument("password");
                        mysipclient.SetParameter("serveraddress", serverAddress);
                        mysipclient.SetParameter("username", userName);
                        mysipclient.SetParameter("password", password);
                        mysipclient.Start();
                        Log.i(LOGTAG, "mizu Started");
                        result.success("started");
                    }
                    break;

                case "makeCall":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot call because SipStack is not started", null);
                    }
                    else {
                        String contactNumber = call.argument("contactNumber");
                        mysipclient.Call(-1, contactNumber); //VIJAY : -1 is for outgoing
                        Log.i(LOGTAG, "calling to : " + contactNumber);
                        result.success("calling");
                    }
                    break;

                case "endCall":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot hangup because SipStack is not started", null);
                    }else {
                        mysipclient.Hangup();
                        Log.i(LOGTAG, "call hanged up");
                        result.success("call hanged up");
                    }
                    break;

                case "acceptCall":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot hangup because SipStack is not started", null);
                    }else {
                        mysipclient.Accept(-1);
                        Log.i(LOGTAG, "call hanged up");
                        result.success("call accepted");
                    }
                    break;

                case "holdCall":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot hold because SipStack is not started", null);
                    }else {
                        Boolean isalreadyHold = call.argument("onHold");
                        mysipclient.Hold(-1, isalreadyHold);
                        Log.i(LOGTAG, "call toggle hold");
                        result.success("call toggle hold");
                    }

                case "toggleLoudSpeaker":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot hold because SipStack is not started", null);
                    }else {
                        mysipclient.SetSpeakerMode(!mysipclient.IsLoudspeaker());
                        Log.i(LOGTAG, "call hanged up");
                        result.success("call accepted");
                    }

                case "muteCall":
                    if (mysipclient == null) {
                        Log.i(LOGTAG, "mysipclient is null");
                        result.error("0", "ERROR, cannot hold because SipStack is not started", null);
                    }else {
                        Boolean isalreadyMute = call.argument("onMute");
                        //. -2 for all current calls or -1 for current line.
                        mysipclient.Mute(-1, !isalreadyMute, 0);
                        Log.i(LOGTAG, "call toggle mute");
                        result.success("call toggle mute");
                    }
            }
        });
    }

    class MyNotificationListener extends SIPNotificationListener
    {
        @Override
        public void onAll(SIPNotification e) {
            Log.i(LOGTAG, e.toString());
        }

        //to catch events related to incoming and outgng
        @Override
        public void onStatus(SIPNotification.Status e)
        {
            //vijay - for outgoing call
            if(e.endpointtype==SIPNotification.Status.DIRECTION_OUT)
            {
                if(e.getStatus()==SIPNotification.Status.STATUS_CALL_CONNECT)
                {
                    Log.i(LOGTAG, "OutgoingCallConnected");
                    eventNotifier.notifyEvent("OutgoingCallConnected", true);
                }
                else if(e.getStatus()==SIPNotification.Status.STATUS_CALL_RINGING)
                {
                    Log.i(LOGTAG, "OutgoingCallRinging");
                    eventNotifier.notifyEvent("OutgoingCallRinging", true);
                }
                else if(e.getStatus()==SIPNotification.Status.STATUS_CALL_FINISHING)
                {
                    Log.i(LOGTAG, "OutgoingCallEnded");
                    eventNotifier.notifyEvent("CallEnded", true);
                }

            }

            //vijay - for incoming call
            else if(e.endpointtype==SIPNotification.Status.DIRECTION_IN)
            {
                if(e.getStatus()==SIPNotification.Status.STATUS_CALL_CONNECT)
                {
                    Log.i(LOGTAG, "IncomingCallConnected");
                    eventNotifier.notifyEvent("IncomingCallConnected", true);
                }
                else if(e.getStatus()==SIPNotification.Status.STATUS_CALL_RINGING)
                {
                    Log.i(LOGTAG, "Incoming call from "+ e.getPeerDisplayname());
                    eventNotifier.notifyEvent("IncomingCallRinging",e.getPeerDisplayname());
                }
                else if(e.getStatus()==SIPNotification.Status.STATUS_CALL_FINISHING)
                {
                    Log.i(LOGTAG, "IncomingCallEnded");
                    eventNotifier.notifyEvent("CallEnded", true);
                }
            }
        }
    }
}
