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

package com.example.architecture.my.mviarchitecture.statistics;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.architecture.my.mviarchitecture.mvibase.MviIntent;
import com.example.architecture.my.mviarchitecture.mvibase.MviViewModel;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.PublishSubject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link StatisticsFragment}), retrieves the data and updates
 * the UI as required.
 */
public class StatisticsViewModel extends ViewModel
        implements MviViewModel<StatisticsIntent, StatisticsViewState> {
    @NonNull
    private PublishSubject<StatisticsIntent> mIntentsSubject;
    @NonNull
    private PublishSubject<StatisticsViewState> mStatesSubject;
    @NonNull
    private StatisticsActionProcessorHolder mActionProcessorHolder;

    public StatisticsViewModel(@NonNull StatisticsActionProcessorHolder actionProcessorHolder) {
        this.mActionProcessorHolder = checkNotNull(actionProcessorHolder, "actionProcessorHolder cannot be null");

        mIntentsSubject = PublishSubject.create();
        mStatesSubject = PublishSubject.create();

        compose().subscribe(this.mStatesSubject);
    }

    @Override
    public void processIntents(Observable<StatisticsIntent> intents) {
        intents.subscribe(mIntentsSubject);
    }

    @Override
    public Observable<StatisticsViewState> states() {
        return mStatesSubject;
    }

    private Observable<StatisticsViewState> compose() {
        return mIntentsSubject.doOnNext(MviViewModel::logIntent)
                .scan(initialIntentFilter)
                .map(this::actionFromIntent)
                .doOnNext(MviViewModel::logAction)
                .compose(mActionProcessorHolder.actionProcessor)
                .doOnNext(MviViewModel::logResult)
                .scan(StatisticsViewState.idle(), reducer)
                .doOnNext(MviViewModel::logState);
    }

    private BiFunction<StatisticsIntent, StatisticsIntent, StatisticsIntent> initialIntentFilter =
            (previousIntent, newIntent) -> {
                // if isReConnection (e.g. after config change)
                // i.e. we are inside the scan, meaning there has already
                // been intent in the past, meaning the InitialIntent cannot
                // be the first => it is a reconnection.
                if (newIntent instanceof StatisticsIntent.InitialIntent) {
                    return StatisticsIntent.GetLastState.create();
                } else {
                    return newIntent;
                }
            };

    private StatisticsAction actionFromIntent(MviIntent intent) {
        if (intent instanceof StatisticsIntent.InitialIntent) {
            return StatisticsAction.LoadStatistics.create();
        }
        if (intent instanceof StatisticsIntent.GetLastState) {
            return StatisticsAction.GetLastState.create();
        }
        throw new IllegalArgumentException("do not know how to treat this intent " + intent);
    }

    private static BiFunction<StatisticsViewState, StatisticsResult, StatisticsViewState> reducer =
            (previousState, result) -> {
                StatisticsViewState.Builder stateBuilder = previousState.buildWith();
                if (result instanceof StatisticsResult.LoadStatistics) {
                    StatisticsResult.LoadStatistics loadResult = (StatisticsResult.LoadStatistics) result;
                    switch (loadResult.status()) {
                        case SUCCESS:
                            return stateBuilder.isLoading(false)
                                    .activeCount(loadResult.activeCount())
                                    .completedCount(loadResult.completedCount())
                                    .build();
                        case FAILURE:
                            return stateBuilder.isLoading(false).error(loadResult.error()).build();
                        case IN_FLIGHT:
                            return stateBuilder.isLoading(true).build();
                    }
                } else if (result instanceof StatisticsResult.GetLastState) {
                    return stateBuilder.build();
                } else {
                    throw new IllegalArgumentException("Don't know this result " + result);
                }
                throw new IllegalStateException("Mishandled result? Should not happen (as always)");
            };
}
