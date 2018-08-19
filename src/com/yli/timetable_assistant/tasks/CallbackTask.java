package com.yli.timetable_assistant.tasks;

import javafx.concurrent.Task;


 public abstract class CallbackTask<V> extends Task<V> {

     CallbackTask(TaskCallbacks<V> callbacks){
         this.setOnRunning(e->callbacks.onLoading());
         this.setOnSucceeded(e->callbacks.onSucceeded(getValue()));
         this.setOnFailed(e->callbacks.onFailed(getException()));
    }


    public interface TaskCallbacks<V> {
        void onLoading();
        void onSucceeded(V result);
        void onFailed(Throwable e);
    }

}
