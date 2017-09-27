package com.example.architecture.my.mviarchitecture.statistics;

import com.example.architecture.my.mviarchitecture.mvibase.MviIntent;
import com.google.auto.value.AutoValue;

interface StatisticsIntent extends MviIntent {
    // should we create a top singleton that any view could use?
    @AutoValue
    abstract class InitialIntent implements StatisticsIntent {
        public static InitialIntent create() {
            return new AutoValue_StatisticsIntent_InitialIntent();
        }
    }

    // should we create a top singleton that any view could use?
    @AutoValue
    abstract class GetLastState implements StatisticsIntent {
        public static GetLastState create() {
            return new AutoValue_StatisticsIntent_GetLastState();
        }
    }
}
