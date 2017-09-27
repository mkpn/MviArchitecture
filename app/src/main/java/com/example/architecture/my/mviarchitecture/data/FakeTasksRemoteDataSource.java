/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.architecture.my.mviarchitecture.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.example.architecture.my.mviarchitecture.data.source.TasksDataSource;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeTasksRemoteDataSource implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTasksRemoteDataSource() {
    }

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public Single<List<Task>> getTasks() {
        Collection<Task> values = TASKS_SERVICE_DATA.values();
        return Observable.fromIterable(values).toList();
    }

    @Override
    public Single<Task> getTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        return Single.just(task);
    }

    @Override
    public Completable saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
        return Completable.complete();
    }

    @Override
    public Completable completeTask(@NonNull Task task) {
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERVICE_DATA.put(task.getId(), completedTask);
        return null;
    }

    @Override
    public Completable completeTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERVICE_DATA.put(taskId, completedTask);
        return null;
    }

    @Override
    public Completable activateTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASKS_SERVICE_DATA.put(task.getId(), activeTask);
        return Completable.complete();
    }

    @Override
    public Completable activateTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASKS_SERVICE_DATA.put(taskId, activeTask);
        return null;
    }

    @Override
    public Completable clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
        return null;
    }

    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public Completable deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
        return null;
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(Task... tasks) {
        for (Task task : tasks) {
            TASKS_SERVICE_DATA.put(task.getId(), task);
        }
    }
}
