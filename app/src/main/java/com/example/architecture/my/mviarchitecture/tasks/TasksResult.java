package com.example.architecture.my.mviarchitecture.tasks;

import android.support.annotation.NonNull;

import com.example.architecture.my.mviarchitecture.data.Task;
import com.example.architecture.my.mviarchitecture.mvibase.LceStatus;
import com.example.architecture.my.mviarchitecture.mvibase.MviResult;
import com.google.auto.value.AutoValue;

import java.util.List;

import javax.annotation.Nullable;

import static com.example.architecture.my.mviarchitecture.mvibase.LceStatus.FAILURE;
import static com.example.architecture.my.mviarchitecture.mvibase.LceStatus.IN_FLIGHT;
import static com.example.architecture.my.mviarchitecture.mvibase.LceStatus.SUCCESS;

interface TasksResult extends MviResult {
    @AutoValue
    abstract class LoadTasks implements TasksResult {
        @NonNull
        abstract LceStatus status();

        @Nullable
        abstract List<Task> tasks();

        @Nullable
        abstract TasksFilterType filterType();

        @Nullable
        abstract Throwable error();

        @NonNull
        static LoadTasks success(@NonNull List<Task> tasks, @Nullable TasksFilterType filterType) {
            return new AutoValue_TasksResult_LoadTasks(SUCCESS, tasks, filterType, null);
        }

        @NonNull
        static LoadTasks failure(Throwable error) {
            return new AutoValue_TasksResult_LoadTasks(FAILURE, null, null, error);
        }

        @NonNull
        static LoadTasks inFlight() {
            return new AutoValue_TasksResult_LoadTasks(IN_FLIGHT, null, null, null);
        }
    }

    @AutoValue
    abstract class GetLastState implements TasksResult {
        static GetLastState create() {
            return new AutoValue_TasksResult_GetLastState();
        }
    }

    @AutoValue
    abstract class ActivateTaskResult implements TasksResult {
        @NonNull
        abstract LceStatus status();

        @Nullable
        abstract List<Task> tasks();

        @Nullable
        abstract Throwable error();

        @NonNull
        static ActivateTaskResult success(@NonNull List<Task> tasks) {
            return new AutoValue_TasksResult_ActivateTaskResult(SUCCESS, tasks, null);
        }

        @NonNull
        static ActivateTaskResult failure(Throwable error) {
            return new AutoValue_TasksResult_ActivateTaskResult(FAILURE, null, error);
        }

        @NonNull
        static ActivateTaskResult inFlight() {
            return new AutoValue_TasksResult_ActivateTaskResult(IN_FLIGHT, null, null);
        }
    }

    @AutoValue
    abstract class CompleteTaskResult implements TasksResult {
        @NonNull
        abstract LceStatus status();

        @Nullable
        abstract List<Task> tasks();

        @Nullable
        abstract Throwable error();

        @NonNull
        static CompleteTaskResult success(@NonNull List<Task> tasks) {
            return new AutoValue_TasksResult_CompleteTaskResult(SUCCESS, tasks, null);
        }

        @NonNull
        static CompleteTaskResult failure(Throwable error) {
            return new AutoValue_TasksResult_CompleteTaskResult(FAILURE, null, error);
        }

        @NonNull
        static CompleteTaskResult inFlight() {
            return new AutoValue_TasksResult_CompleteTaskResult(IN_FLIGHT, null, null);
        }
    }

    @AutoValue
    abstract class ClearCompletedTasksResult implements TasksResult {
        @NonNull
        abstract LceStatus status();

        @Nullable
        abstract List<Task> tasks();

        @Nullable
        abstract Throwable error();

        @NonNull
        static ClearCompletedTasksResult success(@NonNull List<Task> tasks) {
            return new AutoValue_TasksResult_ClearCompletedTasksResult(SUCCESS, tasks, null);
        }

        @NonNull
        static ClearCompletedTasksResult failure(Throwable error) {
            return new AutoValue_TasksResult_ClearCompletedTasksResult(FAILURE, null, error);
        }

        @NonNull
        static ClearCompletedTasksResult inFlight() {
            return new AutoValue_TasksResult_ClearCompletedTasksResult(IN_FLIGHT, null, null);
        }
    }
}
