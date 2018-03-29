import 'package:flutter/material.dart';
import 'package:flutter_flux/flutter_flux.dart';
import 'memory_store.dart';

MemoryStore memoryStore = new MemoryStore();

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

class MemoryListState extends State<MemoryList> with StoreWatcherMixin<MemoryList>{
  MemoryStore memoryStore;

  @override
  void initState() {
    super.initState();
    memoryStore = listenToStore(memoryStoreToken);
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Memory'),
      ),
      body: new ListView.builder(
        itemCount: memoryStore.memories.length,
        itemBuilder: (BuildContext context, int idx) {
          return new MemoryWidget(memoryStore.memories[idx]);
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
          return new AddEditMemory('Add');
        }
      )
    );
  }
}

class MemoryWidget extends StatelessWidget {
  MemoryWidget(this.memory);
  final Memory memory;

  @override
  Widget build(BuildContext context) {
    return new GestureDetector(
      onTap: () {
        Navigator.of(context).push(
          new MaterialPageRoute(
            builder: (context) => new MemoryDetail(memory),
          )
        );
      },
      child: new Row(children: <Widget>[
        new Column(children: <Widget>[
          new Text(memory.date),
          new Text(memory.tags),
        ],),
        new Text(memory.text),
      ]),
    );
  }
}

class AddEditMemory extends StatefulWidget {
  AddEditMemory(this.addOrEdit, [this.memoryIdx]);
  final String addOrEdit;
  final int memoryIdx;

  @override
  AddEditMemoryState createState() => new AddEditMemoryState(addOrEdit, memoryIdx);
}

class AddEditMemoryState extends State<AddEditMemory> {
  AddEditMemoryState(this.addOrEdit, this.memoryIdx) :
    memory = new Memory(
      memoryStore.memories.length, '', '', ''
    ),
    dateController = new TextEditingController(),
    tagsController = new TextEditingController(),
    textController = new TextEditingController()
    {
      if (memoryIdx != null) {
        memory.idx = memoryIdx;
        memory.text = textController.text = memoryStore.memories[memoryIdx].text;
        memory.tags = tagsController.text = memoryStore.memories[memoryIdx].tags;
        memory.date = dateController.text = memoryStore.memories[memoryIdx].date;
      }
    }
  final String addOrEdit;
  final int memoryIdx;
  final Memory memory;
  final TextEditingController dateController;
  final TextEditingController tagsController;
  final TextEditingController textController;

  _addMemory(BuildContext context) {
    if (addOrEdit == 'Add') {
      addMemoryAction(memory);
    } else {
      assert(addOrEdit == 'Edit');
      assert(memoryIdx != null);
      updateMemoryAction(memory);
    }

    Navigator.of(context).pop();
    Navigator.of(context).pop();
  }

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
          new Expanded(child: new TextField(
            controller: dateController,
            onChanged: (text) => memory.date = text,
          )),
        ]),
        new Row(children: <Widget>[
          new Container(
            child: new Text('Tags'),
            padding: new EdgeInsets.all(8.0),
          ),
          new Expanded(child: new TextField(
            controller: tagsController,
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
            controller: textController,
            onChanged: (text) => memory.text = text,
          ),
          padding: new EdgeInsets.all(8.0),
        )),
        new RaisedButton(
          onPressed: () => _addMemory(context),
          child: new Center(child: new Text('Save'))
        )
      ]),
    );
  }
}

class MemoryDetail extends StatelessWidget {
  MemoryDetail(this.memory);
  final Memory memory;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(memory.date),
        actions: <Widget>[
          new IconButton(
            icon: new Icon(Icons.delete),
            onPressed: () => deleteMemory(context),
          ),
        ],
      ),
      body: new Text(memory.text),
      floatingActionButton: new FloatingActionButton(
        tooltip: 'Edit',
        child: new Icon(Icons.edit),
        onPressed: () => _editMemoryRoute(context, memory.idx),
      ),
    );
  }

  _editMemoryRoute(BuildContext context, int idx) {
    Navigator.of(context).push(
      new MaterialPageRoute(
        builder: (context) {
          return new AddEditMemory('Edit', idx);
        }
      )
    );
  }

  void deleteMemory(BuildContext context) {
    deleteMemoryAction(memory);
    Navigator.of(context).pop();
  }
}
