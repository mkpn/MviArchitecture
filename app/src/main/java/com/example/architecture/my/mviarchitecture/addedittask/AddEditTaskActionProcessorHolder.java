package com.example.architecture.my.mviarchitecture.addedittask;

import android.support.annotation.NonNull;

import com.example.architecture.my.mviarchitecture.data.Task;
import com.example.architecture.my.mviarchitecture.data.source.TasksRepository;
import com.example.architecture.my.mviarchitecture.util.schedulers.BaseSchedulerProvider;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddEditTaskActionProcessorHolder {
    @NonNull
    private TasksRepository mTasksRepository;
    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;

    public AddEditTaskActionProcessorHolder(@NonNull TasksRepository tasksRepository,
                                            @NonNull BaseSchedulerProvider schedulerProvider) {
        this.mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        this.mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

    }


    private ObservableTransformer<AddEditTaskAction.PopulateTask, AddEditTaskResult.PopulateTask>
            populateTaskProcessor =
            actions -> actions.flatMap(action -> mTasksRepository
                    .getTask(action.taskId())
                    .toObservable()
                    .map(AddEditTaskResult.PopulateTask::success)
                    .onErrorReturn(AddEditTaskResult.PopulateTask::failure)
                    .subscribeOn(mSchedulerProvider.io())
                    .observeOn(mSchedulerProvider.ui())
                    .startWith(AddEditTaskResult.PopulateTask.inFlight()));

    private ObservableTransformer<AddEditTaskAction.CreateTask, AddEditTaskResult.CreateTask>
            createTaskProcessor =
            actions -> actions.map(action -> {
                Task task = new Task(action.title(), action.description());
                if (task.isEmpty()) {
                    return AddEditTaskResult.CreateTask.empty();
                }
                mTasksRepository.saveTask(task);
                return AddEditTaskResult.CreateTask.success();
            });

    private ObservableTransformer<AddEditTaskAction.UpdateTask, AddEditTaskResult.UpdateTask>
            updateTaskProcessor =
            actions -> actions.flatMap(action -> mTasksRepository.saveTask(
                    new Task(action.title(), action.description(), action.taskId()))
                    .andThen(Observable.just(AddEditTaskResult.UpdateTask.create())));

    private ObservableTransformer<AddEditTaskAction.GetLastState, AddEditTaskResult.GetLastState>
            getLastStateProcessor =
            actions -> actions.map(ignored -> AddEditTaskResult.GetLastState.create());

    ObservableTransformer<AddEditTaskAction, AddEditTaskResult> actionProcessor =
            actions -> actions.publish(shared -> Observable.merge(
                    shared.ofType(AddEditTaskAction.PopulateTask.class).compose(populateTaskProcessor),
                    shared.ofType(AddEditTaskAction.CreateTask.class).compose(createTaskProcessor),
                    shared.ofType(AddEditTaskAction.UpdateTask.class).compose(updateTaskProcessor),
                    shared.ofType(AddEditTaskAction.GetLastState.class).compose(getLastStateProcessor))
                    .mergeWith(
                            // Error for not implemented actions
                            shared.filter(v -> !(v instanceof AddEditTaskAction.PopulateTask) &&
                                    !(v instanceof AddEditTaskAction.CreateTask) &&
                                    !(v instanceof AddEditTaskAction.UpdateTask) &&
                                    !(v instanceof AddEditTaskAction.GetLastState))
                                    .flatMap(w -> Observable.error(
                                            new IllegalArgumentException("Unknown Action type: " + w)))));


}
