import 'package:flutter/material.dart';
import 'dart:js_util' as js_util;
import 'package:js/js.dart';
import 'dart:js' as js;

@JS()
external Start();

@JS()
external Call();

void main() {
  runApp(MaterialApp(home: MyApp(),));
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
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

void call() async {
  print("Placing Call...");
  try {
    var promise = Call();
    var result = await js_util.promiseToFuture(promise);
    print("Call placed: $result");
  } catch (e) {
    print("Failed to place call: $e");
  }
}


class _MyAppState extends State<MyApp> {


  @override
  void initState() {
    super.initState();
    initialize();
  }

  Future<void> initialize() async {
    try {
      await Start();
      Future.delayed(Duration(seconds: 3));
      await Call();
    } catch (e) {
      print("Error during initialization: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.blueGrey,
    );
  }
}
