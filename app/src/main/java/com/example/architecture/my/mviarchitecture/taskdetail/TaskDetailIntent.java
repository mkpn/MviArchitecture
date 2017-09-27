package com.example.architecture.my.mviarchitecture.taskdetail;

import android.support.annotation.Nullable;

import com.example.architecture.my.mviarchitecture.mvibase.MviIntent;
import com.google.auto.value.AutoValue;

interface TaskDetailIntent extends MviIntent {
    @AutoValue
    abstract class InitialIntent implements TaskDetailIntent {
        @Nullable
        abstract String taskId();

        public static InitialIntent create(@Nullable String taskId) {
            return new AutoValue_TaskDetailIntent_InitialIntent(taskId);
        }
    }

    @AutoValue
    abstract class GetLastState implements TaskDetailIntent {
        public static GetLastState create() {
            return new AutoValue_TaskDetailIntent_GetLastState();
        }
    }

    @AutoValue
    abstract class DeleteTask implements TaskDetailIntent {

        abstract String taskId();

        public static DeleteTask create(String taskId) {
            return new AutoValue_TaskDetailIntent_DeleteTask(taskId);
        }
    }

    @AutoValue
    abstract class ActivateTaskIntent implements TaskDetailIntent {
        abstract String taskId();

        public static ActivateTaskIntent create(String taskId) {
            return new AutoValue_TaskDetailIntent_ActivateTaskIntent(taskId);
        }
    }

    @AutoValue
    abstract class CompleteTaskIntent implements TaskDetailIntent {
        abstract String taskId();

        public static CompleteTaskIntent create(String taskId) {
            return new AutoValue_TaskDetailIntent_CompleteTaskIntent(taskId);
        }
    }

}
