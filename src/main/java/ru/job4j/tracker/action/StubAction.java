package ru.job4j.tracker.action;

import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.store.MemTracker;

public class StubAction implements UserAction {
    private boolean call = false;

    @Override
    public String name() {
        return "Stub action";
    }

    @Override
    public boolean execute(Input input, MemTracker tracker) {
        call = true;
        return false;
    }

    public boolean isCall() {
        return call;
    }
}
