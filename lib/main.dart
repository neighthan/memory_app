import 'package:flutter/material.dart';

void main() => runApp(new MemoryApp());

class MemoryApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Memory',
      home: new MemoryList(),
    );
  }
}

class MemoryList extends StatefulWidget {
  final List<Memory> _memories = ['Memory 1', 'Memory 2'].map((text) {
    return new Memory(text: text);
  }).toList();
  @override
  MemoryListState createState() => new MemoryListState();
}

class MemoryListState extends State<MemoryList> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Memory'),
      ),
      body: new ListView(
        children: widget._memories,
      ),
    );
  }
}

class Memory extends StatelessWidget {
  Memory({this.text});
  final text;

  @override
  Widget build(BuildContext context) {
    return new ListTile(
      title: new Text(text),
    );
  }
}
