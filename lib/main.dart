import 'dart:io';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_flux/flutter_flux.dart';
import 'package:import_file/import_file.dart';
import 'memory_store.dart';

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
    memoryStore.loadMemories().then((_) => setState(() => {}));
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Memory'),
        actions: <Widget>[
          new IconButton(
            icon: new Icon(Icons.import_export),
            onPressed: importMemories,
          ),
        ],
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

  importMemories() async {
    String uri = await ImportFile.importFile('*/*');
    String data = await new File(uri).readAsString();

    for (String line in data.split(Memory.lineSeparator)) {
      try {
        addMemoryAction(Memory.deserialize(memoryStore.memories.length, line));
      } catch(error) {
        print(error);
        print(line);
      }
    }
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
          new Text("${memory.date.month}-${memory.date.day}-${memory.date.year.toString().substring(2, 4)}"),
          new Text(memory.tags.substring(0, min(memory.tags.length, 20)), maxLines: 1),
        ],),
        new Text(memory.text.substring(0, min(memory.text.length, 40)), maxLines: 3, softWrap: true),
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

class AddEditMemoryState extends State<AddEditMemory> with StoreWatcherMixin<AddEditMemory>{
  AddEditMemoryState(this.addOrEdit, this.memoryIdx) :
    dateController = new TextEditingController(),
    tagsController = new TextEditingController(),
    textController = new TextEditingController();
  final String addOrEdit;
  final int memoryIdx;
  final TextEditingController dateController;
  final TextEditingController tagsController;
  final TextEditingController textController;
  MemoryStore memoryStore;

  @override
  void initState() {
    super.initState();
    memoryStore = listenToStore(memoryStoreToken);

    if (memoryIdx != null) {
      textController.text = memoryStore.memories[memoryIdx].text;
      tagsController.text = memoryStore.memories[memoryIdx].tags;
      dateController.text = Memory.formatDateTime(memoryStore.memories[memoryIdx].date);
    } else {
      dateController.text = Memory.formatDateTime(new DateTime.now());
    }
  }

  _addMemory(BuildContext context) {
    Memory memory = new Memory(
      memoryIdx == null ? memoryStore.memories.length : memoryIdx,
      dateController.text,
      tagsController.text,
      textController.text
    );

    if (addOrEdit == 'Add') {
      addMemoryAction(memory);
    } else {
      assert(addOrEdit == 'Edit');
      assert(memoryIdx != null);
      updateMemoryAction(memory);
      Navigator.of(context).pop();
    }

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
          )),
        ]),
        new Row(children: <Widget>[
          new Container(
            child: new Text('Tags'),
            padding: new EdgeInsets.all(8.0),
          ),
          new Expanded(child: new TextField(
            controller: tagsController,
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
  MemoryDetail(this.memory) : timeFormat = Memory.formatDateTime(memory.date);
  final Memory memory;
  final String timeFormat;

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text(timeFormat),
        actions: <Widget>[
          new IconButton(
            icon: new Icon(Icons.edit),
            onPressed: () => _editMemoryRoute(context, memory.idx)
          ),
          new IconButton(
            icon: new Icon(Icons.delete),
            onPressed: () => confirmDeleteMemory(context),
          ),
        ],
      ),
      body: new Text(memory.text),
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

  void confirmDeleteMemory(BuildContext context) {
    showDialog(
      context: context,
      child: new AlertDialog(
        content: new Text('Are you sure you want to delete this memory?'),
        actions: <Widget>[
          new FlatButton(
            child: new Text('No'),
            onPressed: () => Navigator.of(context).pop(),
          ),
          new FlatButton(
            child: new Text('Yes'),
            onPressed: () => deleteMemory(context),
          ),
        ],
      ),
    );
  }

  void deleteMemory(BuildContext context) {
    deleteMemoryAction(memory);
    Navigator.of(context).pop();
    Navigator.of(context).pop();
  }
}
