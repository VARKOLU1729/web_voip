import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:web_voip/Widgets/dial_pad.dart';
import 'package:web_voip/android_screens/call_screen.dart';

import '../Services/native_event_listener.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {

  final NativeEventNotifier notifier = NativeEventNotifier();
  static const platform = MethodChannel('mizu');
  String enteredContact = "";

  void onButtonPressed(String char)
  {
    if(enteredContact.length<10) {
      setState(() {
        enteredContact += char;
      });
    }
  }

  void removeLastChar()
  {
    if(enteredContact.length>0) {
      setState(() {
        enteredContact = enteredContact.substring(0, enteredContact.length - 1);
      });
    }
  }

  void makeCall()
  {
    if(enteredContact.length==10) {
      print("gng to call screen");
      Navigator.of(context).push(MaterialPageRoute(
          builder: (context) => CallScreen(callDirection:0, contactNumber: enteredContact)));
    }
  }

  void initialise() async
  {
    String a = await platform.invokeMethod("init");
    print(a);
  }
  
  void start() async
  {
    String serverAddress = "sip-bgn-int.ttsl.tel:49868";
    String userName = "0605405970003";
    String password = "NFnjgudZXs";
    String a = await platform.invokeMethod("start", {"serverAddress":serverAddress, "userName" : userName, "password": password});
    print(a);
  }
  
  // void call() async
  // {
  //   String a = await platform.invokeMethod("makeCall", {"contactNumber" : "6301450563"});
  //   print("a");
  // }

  // void hangUp() async
  // {
  //   String a = await platform.invokeMethod("endCall");
  //   print(a);
  // }

  // void acceptCall() async
  // {
  //   String a = await platform.invokeMethod("acceptCall");
  //   print(a);
  // }
  
  @override
  void initState()
  {
    super.initState();
    initialise();
    start();
    notifier.startListening((eventData) {
      switch(eventData.keys.first.toString()) {
        case "IncomingCallRinging":
          print("vijay...................................");
          String contactNumber = eventData["IncomingCallRinging"].toString().substring(4,14);
          print("vonta..................................."+contactNumber);
          Navigator.push(context, MaterialPageRoute(builder: (context)=>CallScreen(callDirection:1, contactNumber: contactNumber)));
          break;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
    backgroundColor: Colors.black,
      // body: Center(child: Column(
      //   mainAxisAlignment: MainAxisAlignment.center,
      //   children: [
      //     TextButton(onPressed: call, child: Text("Call", style: TextStyle(color: Colors.white),)),
      //     TextButton(onPressed: hangUp, child: Text("hang Up ", style: TextStyle(color: Colors.white),)),
      //     TextButton(onPressed: acceptCall, child: Text("accept ", style: TextStyle(color: Colors.white),)),
      //   ],
      // )),
      body: DialPad(
          makeCall: makeCall,
          onButtonPressed: onButtonPressed,
          removeLastChar: removeLastChar,
          enteredContact: enteredContact,
      )
    );
  }
}
