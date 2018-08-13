package com.yli.timetable_assistant.tasks;

import javafx.concurrent.Task;


 abstract class ParamTask<V,P> extends Task<V> {
    final P param;


     ParamTask(TaskCallbacks<V> callbacks, P param){
        this.param = param;
        this.setOnRunning(e->callbacks.onLoading());
        this.setOnSucceeded(e->callbacks.onFinishedLoading(getValue()));
    }


    public interface TaskCallbacks<V> {
        void onLoading();
        void onFinishedLoading(V result);
    }

}
