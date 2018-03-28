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
  @override
  MemoryListState createState() => new MemoryListState();
}

class MemoryListState extends State<MemoryList> {
  final List<MemoryWidget> _memories = [['1-1', 'R, s', 'Memory 1'], ['1-2', 'R, s', 'Memory 2']]
  .map((mem) {
    return new MemoryWidget(new Memory(mem[0], mem[1], mem[2]));
  }).toList();

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Memory'),
      ),
      body: new ListView.builder(
        itemCount: _memories.length,
        itemBuilder: (BuildContext context, int idx) {
          return _memories[idx];
        },
      ),
      floatingActionButton: new FloatingActionButton(
        tooltip: 'Add Memory',
        child: new Icon(Icons.add),
        onPressed: _addMemoryRoute,
      ),
    );
  }

  _addMemoryRoute() {
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (context) {
          return new AddEditMemory('Add', this);
        }
      )
    );
  }

  _addMemory(Memory memory) {
    setState(() {
      _memories.add(new MemoryWidget(memory));
    });
  }

  _updateMemory(Memory memory, int memoryIdx) {
    setState(() {
      _memories[memoryIdx] = new MemoryWidget(memory);
    });
  }
}

class Memory {
  Memory(this.date, this.tags, this.text);
  String date;
  String tags;
  String text;
}

class MemoryWidget extends StatelessWidget {
  MemoryWidget(this.memory);
  final Memory memory;

  @override
  Widget build(BuildContext context) {
    return new Row(children: <Widget>[
      new Column(children: <Widget>[
        new Text(memory.date),
        new Text(memory.tags),
      ],),
      new Text(memory.text),
    ]);
  }
}

class AddEditMemory extends StatelessWidget {
  AddEditMemory(this.addOrEdit, this.memoryListState, [this.memoryIdx]);
  final String addOrEdit;
  final MemoryListState memoryListState;
  final int memoryIdx;

  @override
  Widget build(BuildContext context) {
    final Memory memory = new Memory('', '', '');

    _addMemory() {
      if (addOrEdit == 'Add') {
        memoryListState._addMemory(memory);
      } else {
        assert(addOrEdit == 'Edit');
        assert(memoryIdx != null);
        memoryListState._updateMemory(memory, memoryIdx);
      }

      Navigator.of(context).pop();
    }

    if (memoryIdx != null) {
      memory.text = memoryListState._memories[memoryIdx].memory.text;
      memory.tags = memoryListState._memories[memoryIdx].memory.tags;
      memory.date = memoryListState._memories[memoryIdx].memory.date;
    }

    return new Scaffold(
      appBar: new AppBar(title: new Text('$addOrEdit Memory')),
      body: new Column(children: <Widget>[
        new Row(children: <Widget>[
          new Container(
            child: new Text('Date'),
            padding: new EdgeInsets.all(8.0),
          ),
          new Expanded(child: new TextField(
            onChanged: (text) => memory.date = text,
          )),
        ]),
        new Row(children: <Widget>[
          new Container(
            child: new Text('Tags'),
            padding: new EdgeInsets.all(8.0),
          ),
          new Expanded(child: new TextField(
            onChanged: (text) => memory.tags = text,
          )),
        ]),
        new Container(
          child: new Text('Memory'),
          padding: new EdgeInsets.all(16.0),
        ),
        new Expanded(child: new Container(
          child: new TextField(
            maxLines: null,
            onChanged: (text) => memory.text = text,
          ),
          padding: new EdgeInsets.all(8.0),
        )),
        new RaisedButton(onPressed: _addMemory, child: new Center(child: new Text('Save')))
      ]),
    );
  }
}
