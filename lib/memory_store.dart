import 'dart:io';
import 'dart:async';
import 'package:flutter_flux/flutter_flux.dart';
import 'package:path_provider/path_provider.dart';

class Memory {
  Memory(this.idx, String dateString, this.tags, this.text):
    date = parseDateTime(dateString);
  int idx;
  DateTime date;
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
        ..writeAll([date.toString(), tags, text].map(replaceSeparators), itemSeparator)
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

  static DateTime parseDateTime(String dateString) {
    try {
      return DateTime.parse(dateString);
    } catch (FormatException) {
      List<String> splits = dateString.split(' ');
      List<int> monthDayYear = splits[0].split('-').map(int.parse).toList();
      List<int> hourMinute = splits[1].split(':').map(int.parse).toList();
      String ampm = splits[2];

      int year = monthDayYear[2];
      int hour = hourMinute[0];
      year += 2000;

      if (hour == 12 && ampm == 'AM') {
        hour = 0;
      } else if (hour != 12 && ampm == 'PM') {
        hour += 12;
      }

      return new DateTime(year, monthDayYear[0], monthDayYear[1], hour, hourMinute[1]);
    }
  }

  static String formatDateTime(DateTime date) {
    String timeFormat = "${date.month}-${date.day}-"
      "${date.year.toString().substring(2, 4)} ";

    String ampm = "AM";
    int hour = date.hour;
    if (hour >= 12) {
      ampm = "PM";
      if (hour > 12) {
        hour -= 12;
      }
    } else if (hour == 0) {
      hour = 12;
    }

    timeFormat += "$hour:${date.minute < 10 ? 0 : ''}${date.minute} $ampm";
    return timeFormat;
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
        _memories.add(new Memory(i, DateTime.parse("2018-01-0$i").toString(), 'R, s', 'Memory $i'));
      }
    } else {
      try {
        final file = await memoriesFile;
        final fileContents = await file.readAsString();

        int idx = 0;
        for (String line in fileContents.split(Memory.lineSeparator)) {
          if (line.isEmpty) {
            break;
          }

          _memories.add(Memory.deserialize(idx, line));
          idx++;
        }
      } on FileSystemException {
        // no memories file exists;
      }
    }
    memoriesLoaded = true;
  }

  void saveMemories() async {
    final file = await memoriesFile;

    StringBuffer memoryString = new StringBuffer();
    for (Memory mem in _memories) {
      memoryString.write(mem.serialize());
      memoryString.write(Memory.lineSeparator);
    }
    file.writeAsString(memoryString.toString());
  }

  static Future<File> get memoriesFile async {
    final directory = await getApplicationDocumentsDirectory();
    return new File("${directory.path}/memories.csv");
  }
}

final StoreToken memoryStoreToken = new StoreToken(new MemoryStore());

final Action<Memory> addMemoryAction = new Action<Memory>();
final Action<Memory> updateMemoryAction = new Action<Memory>();
final Action<Memory> deleteMemoryAction = new Action<Memory>();
