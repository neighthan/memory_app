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
      floatingActionButton: new FloatingActionButton(
        tooltip: 'Add Memory',
        child: new Icon(Icons.add),
        onPressed: _addMemory,
      ),
    );
  }

  _addMemory() {
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (context) {
          return new AddEditMemory('Add');
        }
      )
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

class AddEditMemory extends StatelessWidget {
  AddEditMemory(this.addOrEdit);
  final String addOrEdit;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(title: new Text('$addOrEdit Memory')),
      body: new Column(children: <Widget>[
        new TextField(),
        new RaisedButton(onPressed: _addMemory, child: new Center(child: new Text('Save')))
      ]),
    );
  }

  _addMemory() {
  }
}
