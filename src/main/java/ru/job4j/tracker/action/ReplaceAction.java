package ru.job4j.tracker.action;

import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.store.MemTracker;

public class ReplaceAction implements UserAction {

    private final Output out;

    public ReplaceAction(Output out) {
        this.out = out;
    }

    @Override
    public String name() {
        return "=== Edit item ====";
    }

    @Override
    public boolean execute(Input input, MemTracker tracker) {
        int id = Integer.parseInt(input.askStr("Enter id: "));
        String name = input.askStr("Enter name: ");
        if (tracker.replace(id, new Item(name))) {
            out.println("Item is successfully replaced!");
        } else {
            out.println("Wrong id!");
        }
        return true;
    }
}
