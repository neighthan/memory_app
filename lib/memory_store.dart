import 'package:flutter_flux/flutter_flux.dart';

class Memory {
  Memory(this.idx, this.date, this.tags, this.text);
  int idx;
  String date;
  String tags;
  String text;
}

class MemoryStore extends Store {
  final List<Memory> _memories = <Memory>[];
  List<Memory> get memories => new List<Memory>.unmodifiable(_memories);

  MemoryStore() {
    loadMemories();

    triggerOnAction(addMemoryAction, (Memory memory) {
      _memories.add(memory);
    });

    triggerOnAction(updateMemoryAction, (Memory memory) {
      _memories[memory.idx] = memory;
    });
  }

  void loadMemories() {
    for (int i = 0; i < 10; i++) {
      _memories.add(new Memory(i, "1-$i", 'R, s', 'Memory $i'));
    }
  }
}

final StoreToken memoryStoreToken = new StoreToken(new MemoryStore());

final Action<Memory> addMemoryAction = new Action<Memory>();
final Action<Memory> updateMemoryAction = new Action<Memory>();
