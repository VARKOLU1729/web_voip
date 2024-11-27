import 'dart:js_util' as js_util;
import 'package:flutter/material.dart';
import 'package:js/js.dart';
import 'dart:js' as js;

@JS()
external Start();

@JS()
external Call(String);

@JS()
external AcceptCall();

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _MyAppState();
}

void start() async {
  print("Initializing Webphone...");
  try {
    var promise = Start();
    var result = await js_util.promiseToFuture(promise);
    print("Webphone initialized: $result");
  } catch (e) {
    print("Failed to initialize Webphone: $e");
  }
}



class _MyAppState extends State<HomeScreen> {

  String enteredText = "";

  @override
  void initState() {
    super.initState();
    initialize();
  }

  Future<void> initialize() async {
    try {
      await Start();
    } catch (e) {
      print("Error during initialization: $e");
    }
  }

  void call(String contactNumber) async {
    print("Placing Call...");
    try {
      var promise = Call(contactNumber);
      var result = await js_util.promiseToFuture(promise);
      print("Call placed: $result");
    } catch (e) {
      print("Failed to place call: $e");
    }
  }

  void acceptCall() async
  {
    try {
      var promise = AcceptCall();
      var result = await js_util.promiseToFuture(promise);
      print("Call placed: $result");
    } catch (e) {
      print("Failed to place call: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.blueGrey,
      body: Column(
        children: [
          TextField(
            onChanged: (val){
              setState(() {
                enteredText = val;
              });
            },
          ),
          TextButton(onPressed:(){call(enteredText);}, child: Text("Click To Call", style: TextStyle(color:Colors.white),)),
          TextButton(onPressed:(){acceptCall();}, child: Text("AcceptCall", style: TextStyle(color:Colors.white),))
        ],
      ),
    );
  }
}