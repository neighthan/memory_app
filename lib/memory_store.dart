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

  @override
  String toString() {
    return "idx: $idx, date: $date; tags: $tags; text: $text";
  }
}

class MemoryStore extends Store {
  final String lineSeparator = '\n';
  final String itemSeparator = ',';
  final String lineSeparatorReplacement = '<<newline>>';
  final String itemSeparatorReplacement = '<<comma>>';
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
      for (String line in fileContents.split(lineSeparator)) {
        if (line.isEmpty) {
          break;
        }

        List<String> memItems = line.split(itemSeparator).map(returnSeparators).toList();
        _memories.add(new Memory(idx, memItems[0], memItems[1], memItems[2]));
        idx++;
      }
    }
    memoriesLoaded = true;
  }

  void saveMemories() async {
    final file = await _memoriesFile;

    StringBuffer memoryString = new StringBuffer();
    for (Memory mem in _memories) {
      memoryString.writeAll([mem.date, mem.tags, mem.text].map(replaceSeparators), itemSeparator);
      memoryString.write(lineSeparator);
    }
    file.writeAsString(memoryString.toString());
  }

  String replaceSeparators(String s) {
    return s.replaceAll(lineSeparator, lineSeparatorReplacement)
        .replaceAll(itemSeparator, itemSeparatorReplacement);
  }

  String returnSeparators(String s) {
    return s.replaceAll(lineSeparatorReplacement, lineSeparator)
        .replaceAll(itemSeparatorReplacement, itemSeparator);
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
