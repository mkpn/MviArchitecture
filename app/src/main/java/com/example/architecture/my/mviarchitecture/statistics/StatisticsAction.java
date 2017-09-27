package com.example.architecture.my.mviarchitecture.statistics;

import com.example.architecture.my.mviarchitecture.mvibase.MviAction;
import com.google.auto.value.AutoValue;

interface StatisticsAction extends MviAction {
    @AutoValue
    abstract class LoadStatistics implements StatisticsAction {
        public static LoadStatistics create() {
            return new AutoValue_StatisticsAction_LoadStatistics();
        }
    }

    @AutoValue
    abstract class GetLastState implements StatisticsAction {
        public static GetLastState create() {
            return new AutoValue_StatisticsAction_GetLastState();
        }
    }
}
