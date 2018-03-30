import 'dart:io';
import 'dart:async';
import 'package:flutter_flux/flutter_flux.dart';
import 'package:path_provider/path_provider.dart';

class Memory {
  Memory(this.idx, this.date, this.tags, this.text);
  int idx;
  String date;
  String tags;
  String text;

  static final String lineSeparator = '\n';
  static final String itemSeparator = ',';
  static final String lineSeparatorReplacement = '<<newline>>';
  static final String itemSeparatorReplacement = '<<comma>>';

  @override
  String toString() {
    return "idx: $idx, date: $date; tags: $tags; text: $text";
  }

  String serialize() {
    return (new StringBuffer()
        ..writeAll([date, tags, text].map(replaceSeparators), itemSeparator)
      ).toString();
  }

  static Memory deserialize(int idx, String memString) {
    List<String> memItems = memString.split(itemSeparator).map(returnSeparators).toList();
    return new Memory(idx, memItems[0], memItems[1], memItems[2]);
  }

  static String replaceSeparators(String s) {
    return s.replaceAll(lineSeparator, lineSeparatorReplacement)
        .replaceAll(itemSeparator, itemSeparatorReplacement);
  }

  static String returnSeparators(String s) {
    return s.replaceAll(lineSeparatorReplacement, lineSeparator)
        .replaceAll(itemSeparatorReplacement, itemSeparator);
  }
}

class MemoryStore extends Store {
  bool memoriesLoaded = false;
  final List<Memory> _memories = <Memory>[];
  List<Memory> get memories => new List<Memory>.unmodifiable(_memories);

  MemoryStore() {
    triggerOnAction(addMemoryAction, (Memory memory) {
      _memories.add(memory);
      saveMemories();
    });

    triggerOnAction(updateMemoryAction, (Memory memory) {
      _memories[memory.idx] = memory;
      saveMemories();
    });

    triggerOnAction(deleteMemoryAction, (Memory memory) {
      _memories.removeAt(memory.idx);
      if (_memories.isNotEmpty) {
        // fix the indices of all memories after the deleted one
        for (int i = memory.idx; i < _memories.length; i++) {
          _memories[i].idx--;
        }
      }
      saveMemories();
    });
  }

  Future<void> loadMemories() async {
    if (memoriesLoaded) {
      return null;
    }

    bool testing = false;

    if (testing) {
      for (int i = 0; i < 10; i++) {
        _memories.add(new Memory(i, "1-$i", 'R, s', 'Memory $i'));
      }
    } else {
      final file = await _memoriesFile;
      final fileContents = await file.readAsString();

      int idx = 0;
      for (String line in fileContents.split(Memory.lineSeparator)) {
        if (line.isEmpty) {
          break;
        }

        _memories.add(Memory.deserialize(idx, line));
        idx++;
      }
    }
    memoriesLoaded = true;
  }

  void saveMemories() async {
    final file = await _memoriesFile;

    StringBuffer memoryString = new StringBuffer();
    for (Memory mem in _memories) {
      memoryString.write(mem.serialize());
      memoryString.write(Memory.lineSeparator);
    }
    file.writeAsString(memoryString.toString());
  }

  Future<File> get _memoriesFile async {
    final directory = await getApplicationDocumentsDirectory();
    return new File("${directory.path}/memories.csv");
  }
}

final StoreToken memoryStoreToken = new StoreToken(new MemoryStore());

final Action<Memory> addMemoryAction = new Action<Memory>();
final Action<Memory> updateMemoryAction = new Action<Memory>();
final Action<Memory> deleteMemoryAction = new Action<Memory>();
