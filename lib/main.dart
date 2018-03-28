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
  final List<Memory> _memories = [['1-1', 'R, s', 'Memory 1'], ['1-2', 'R, s', 'Memory 2']]
  .map((mem) {
    return new Memory(date: mem[0], tags: mem[1], text: mem[2]);
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
  Memory({this.date, this.tags, this.text});
  final date;
  final tags;
  final text;

  @override
  Widget build(BuildContext context) {
    return new Row(children: <Widget>[
      new Column(children: <Widget>[
        new Text(date),
        new Text(tags),
      ],),
      new Text(text),
    ]);
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
        new Row(children: <Widget>[
          new Container(
            child: new Text('Date'),
            padding: new EdgeInsets.all(8.0),
          ),
          new Expanded(child: new TextField()),
        ]),
        new Row(children: <Widget>[
          new Container(
            child: new Text('Tags'),
            padding: new EdgeInsets.all(8.0),
          ),
          new Expanded(child: new TextField()),
        ]),
        new Container(
          child: new Text('Memory'),
          padding: new EdgeInsets.all(16.0),
        ),
        new Expanded(child: new Container(
          child: new TextField(maxLines: null),
          padding: new EdgeInsets.all(8.0),
        )),
        new RaisedButton(onPressed: _addMemory, child: new Center(child: new Text('Save')))
      ]),
    );
  }

  _addMemory() {
  }
}
